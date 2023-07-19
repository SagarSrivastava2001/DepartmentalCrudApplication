package com.example.DepartmentalCrudApplication.dao;

import com.example.DepartmentalCrudApplication.dto.ProductInventoryUpdateDTO;
import com.example.DepartmentalCrudApplication.exceptions.ProductNotFoundException;
import com.example.DepartmentalCrudApplication.model.Backorders;
import com.example.DepartmentalCrudApplication.model.Customer;
import com.example.DepartmentalCrudApplication.model.OrderDetails;
import com.example.DepartmentalCrudApplication.model.Product_Inventory;
import com.example.DepartmentalCrudApplication.repository.CustomerRepository;
import com.example.DepartmentalCrudApplication.repository.ProductRepository;
import com.example.DepartmentalCrudApplication.service.ProductInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public class BackorderDao {
    private final JdbcTemplate jdbcTemplate;

    public BackorderDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductInventoryService productInventoryService;

    @Autowired
    private ProductRepository productRepository;


    public void insertBackorder(Customer customer){
        String insertSql = "INSERT INTO backorders(backorder_id, contact_number, customer_address, customer_id, customer_name, order_id, order_timestamp, product_id, quantity)" + "VALUES(?,?,?,?,?,?,?,?,?)";
        String selectSql = "SELECT backorder_Id FROM backorders ORDER BY backorder_Id DESC LIMIT 1";
        List<Long> backorderIds = jdbcTemplate.query(selectSql, (resultSet, rowNum) -> resultSet.getLong("backorder_Id"));
        Long lastBackorderId = backorderIds.isEmpty() ? null : backorderIds.get(0);

        if(lastBackorderId == null){
            lastBackorderId = 1l;
        }
        else{
            lastBackorderId += 1;
        }

        String customerIDSql = "SELECT customer_id from customer ORDER BY customer_id DESC LIMIT 1";
        List<Long> customerIds = jdbcTemplate.query(customerIDSql, (resultSet, rowNum) -> resultSet.getLong("customer_id"));
        Long lastCustomerId = customerIds.isEmpty() ? null : customerIds.get(0);

        if(lastCustomerId == null){
            lastCustomerId = 1l;
        }
        else{
            lastCustomerId += 1;
        }

        String orderIDSql = "SELECT order_id from order_details ORDER BY order_id DESC LIMIT 1";
        List<Long> orderIds = jdbcTemplate.query(orderIDSql, (resultSet, rowNum) -> resultSet.getLong("order_id"));
        Long lastOrderId = orderIds.isEmpty() ? null : orderIds.get(0);

        if(lastOrderId == null){
            lastOrderId = 1l;
        }
        else{
            lastOrderId += 1;
        }

        jdbcTemplate.update(insertSql, lastBackorderId, customer.getContactNumber(), customer.getCustomerAddress(), lastCustomerId, customer.getCustomerName(), lastOrderId, customer.getOrderDetails().getOrderTimestamp(), customer.getOrderDetails().getProductId(), customer.getOrderDetails().getQuantity());
    }

    public List<Backorders> retrieveBackorders(Long productId){
        String retrieveSql = "SELECT * FROM backorders WHERE product_id = ?";
        List<Backorders> backorders = jdbcTemplate.query(retrieveSql, new Object[]{productId}, new BeanPropertyRowMapper<>(Backorders.class));

        backorders.forEach(backorder -> {
            Customer customer = new Customer();
            customer.setCustomerId(backorder.getCustomerId());
            customer.setCustomerName(backorder.getCustomerName());
            customer.setCustomerAddress(backorder.getCustomerAddress());
            customer.setContactNumber(backorder.getContactNumber());

            OrderDetails order = new OrderDetails();

            order.setOrderTimestamp(backorder.getOrderTimestamp());
            order.setQuantity(backorder.getQuantity());
            order.setOrderId(backorder.getOrderId());
            order.setProductId(backorder.getProductId());

            customer.setOrderDetails(order);

            Optional<Product_Inventory> prevProduct = productInventoryService.getProductById(order.getProductId());
            long quantityAvailable = prevProduct.get().getCount();

            if(!prevProduct.isPresent()){
                throw new ProductNotFoundException("Product ID: " + order.getProductId());
            }

            if(order.getQuantity() <= quantityAvailable){
                long updatedQuantity = quantityAvailable - order.getQuantity();
                ProductInventoryUpdateDTO product = new ProductInventoryUpdateDTO();
                product.setCount(updatedQuantity);
                product.setExpiry(prevProduct.get().getExpiry());
                product.setAvailability(true);

                if(updatedQuantity == 0){
                    product.setAvailability(false);
                }

                prevProduct.get().setCount(updatedQuantity);
                prevProduct.get().setAvailability(product.getAvailability());

                productRepository.save(prevProduct.get());

                customerRepository.save(customer);

                String deleteSql = "DELETE FROM backorders where customer_id = ?";
                jdbcTemplate.update(deleteSql, customer.getCustomerId());
            }
        });

        return backorders;
    }
}
