package com.example.DepartmentalCrudApplication.serviceIMPL;

import com.example.DepartmentalCrudApplication.dao.BackorderDao;
import com.example.DepartmentalCrudApplication.dto.CustomerDTO;
import com.example.DepartmentalCrudApplication.exceptions.CustomerNotFoundException;
import com.example.DepartmentalCrudApplication.exceptions.ProductNotFoundException;
import com.example.DepartmentalCrudApplication.model.Customer;
import com.example.DepartmentalCrudApplication.model.OrderDetails;
import com.example.DepartmentalCrudApplication.model.Product_Inventory;
import com.example.DepartmentalCrudApplication.repository.CustomerRepository;
import com.example.DepartmentalCrudApplication.repository.ProductRepository;
import com.example.DepartmentalCrudApplication.service.CustomerService;
import com.example.DepartmentalCrudApplication.service.EmailSenderService;
import com.example.DepartmentalCrudApplication.service.ProductInventoryService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public CustomerServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    private ProductRepository productRepository;

    // Call Services
    @Autowired
    private ProductInventoryService productInventoryService;

    @Autowired
    private BackorderDao backorderDao;

    HashMap<Long, LinkedList<Customer>> backordersRecord = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
	private EmailSenderService emailSenderService;

    @Override
    public void addCustomer(Customer customer) throws ProductNotFoundException{
        Long orderProductId = customer.getOrderDetails().getProductId();
        Long quantity = customer.getOrderDetails().getQuantity();

        try {
            Optional<Product_Inventory> product = productInventoryService.getProductById(orderProductId);

            if (!product.isPresent()) {
                LinkedList<Customer> temp = new LinkedList<>();
                temp.add(customer);
                backordersRecord.put(orderProductId, temp);
                throw new ProductNotFoundException("Product ID : " + orderProductId);
            }
            else{
                Boolean productAvailability = product.get().getAvailability();
                Long productCount = product.get().getCount();

                if (productAvailability == null || !productAvailability || quantity > productCount) {
                    LinkedList<Customer> arr = backordersRecord.getOrDefault(orderProductId, new LinkedList<>());
                    arr.add(customer);
                    backordersRecord.put(orderProductId, arr);
                    backorderDao.insertBackorder(customer);
                    logger.info("Added customer to backordersRecord. Customer ID: {}, Product ID: {}", customer.getCustomerId(), orderProductId);
                } else {
                    product.get().setCount(productCount - quantity);
                    if (product.get().getCount() == 0) {
                        product.get().setAvailability(false);
                    }
                    customerRepository.save(customer);
                    logger.info("Saved customer. Customer ID: {}", customer.getCustomerId());
                }
            }
        } catch (ProductNotFoundException e) {
            logger.error("Product Not Available: {}", e.getMessage());
        }
    }

    @Override
    public HashMap<Long, LinkedList<Customer>> optimisedBackOrders() {
        backordersRecord.clear();

        String query = "SELECT * FROM backorders";
        jdbcTemplate.query(query, (resultSet) -> {
            while (resultSet.next()) {
                // Extract the data from the current row
                long productId = resultSet.getLong("product_id");
                long contactNumber = resultSet.getLong("contact_number");
                String customerAddress = resultSet.getString("customer_address");
                long customerId = resultSet.getLong("customer_id");
                String customerName = resultSet.getString("customer_name");
                long orderId = resultSet.getLong("order_id");
                Date orderTimestamp = resultSet.getDate("order_timestamp");
                long quantity = resultSet.getLong("quantity");

                // Create a new Customer object
                Customer customer = new Customer();
                customer.setCustomerId(customerId);
                customer.setCustomerName(customerName);
                customer.setCustomerAddress(customerAddress);
                customer.setContactNumber(contactNumber);

                OrderDetails order = new OrderDetails();
                order.setOrderId(orderId);
                order.setOrderTimestamp(orderTimestamp);
                order.setQuantity(quantity);
                order.setProductId(productId);

                customer.setOrderDetails(order);

                // Update the backordersRecord HashMap
                LinkedList<Customer> customers = backordersRecord.getOrDefault(productId, new LinkedList<>());
                customers.add(customer);
                backordersRecord.put(productId, customers);
            }
            return null;
        });

        return backordersRecord;
    }

    @Override
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public String discountMethod(Optional<Product_Inventory> product, Customer customer) {

        if (!product.isPresent() || product.isEmpty()) {
            logger.warn("Product not found for customer ID: {}", customer.getCustomerId());
            return "The product is out of stock for now.\nWe'll notify you once the product is restocked";
        }

        Boolean productAvailability = product.get().getAvailability();
        Long productCount = product.get().getCount();
        Long quantity = customer.getOrderDetails().getQuantity();

        if (productAvailability == false || quantity > productCount) {
            logger.warn("Product is out of stock for customer ID: {}", customer.getCustomerId());
            return "The product is out of stock for now.\nWe'll notify you once the product is restocked ";
        }

        long price = (product.get().getPrice() * customer.getOrderDetails().getQuantity());
        double newPrice = 0;
        Boolean isDiscount = false;

        // Discount = 5%
        if (price >= 2000 && price < 4000) {
            newPrice = price * 0.05;
            isDiscount = true;
        }
        // Discount = 10%
        else if (price >= 4000 && price < 8000) {
            newPrice = price * 0.10;
            isDiscount = true;
        }
        // Discount = 20%
        else if (price >= 8000) {
            newPrice = price * 0.20;
            isDiscount = true;
        }

        if (isDiscount == false) {
            String body = "Hi " + customer.getCustomerName() + " \nHere is your bill for the purchase:\n\nThe price is " + product.get().getPrice() * customer.getOrderDetails().getQuantity();
            String subject = "Customer Bill for Purchase";
            emailSenderService.sendSimpleEmail(customer.getCustomerEmail(), body, subject);
            logger.info("Customer details added for customer ID: {}. Price: {}", customer.getCustomerId(), price);
            return "Bill details have been sent to the customer mail...";
        }
        else{
            String body = "Hi " + customer.getCustomerName() + " \nHere is your bill for the purchase:\n\nThe price is " + product.get().getPrice() * customer.getOrderDetails().getQuantity() + "\n\nDiscounted Price is " + newPrice;
            String subject = "Customer Bill for Purchase";
            emailSenderService.sendSimpleEmail(customer.getCustomerEmail(), body, subject);
            logger.info("Customer details added for customer ID: {}. Price: {}. Discounted Price: {}", customer.getCustomerId(), price, newPrice);
            return "Bill details have been sent to the customer mail...";
        }
    }

    @Override
    public List<CustomerDTO> findByCustomerName(String customerName) {
        List<CustomerDTO> customerDTOs = new ArrayList<>();
        try{
            List<Customer> customers = customerRepository.findByCustomerName(customerName);

            if(customers.isEmpty()){
                throw new CustomerNotFoundException("Not Available in the Record");
            }

            for (Customer customer : customers) {
                CustomerDTO customerDTO = new CustomerDTO();
                customerDTO.setCustomerName(customer.getCustomerName());
                customerDTO.setCustomerId(customer.getCustomerId());
                customerDTO.setCustomerAddress(customer.getCustomerAddress());
                customerDTO.setContactNumber(customer.getContactNumber());

                customerDTO.setOrderId(customer.getOrderDetails().getOrderId());
                customerDTO.setProductId(customer.getOrderDetails().getProductId());
                customerDTO.setOrderTimestamp(customer.getOrderDetails().getOrderTimestamp());
                customerDTO.setQuantity(customer.getOrderDetails().getQuantity());

                customerDTOs.add(customerDTO);
            }
        }
        catch(CustomerNotFoundException e){
            logger.error("Customer with Name ---> " + customerName + " : {}", e.getMessage());
        }

        return customerDTOs;
    }
}


