package com.example.DepartmentalCrudApplication.service.IMPL;

import com.example.DepartmentalCrudApplication.exceptions.ProductNotFoundException;
import com.example.DepartmentalCrudApplication.model.Customer;
import com.example.DepartmentalCrudApplication.model.Product_Inventory;
import com.example.DepartmentalCrudApplication.repository.CustomerRepository;
import com.example.DepartmentalCrudApplication.repository.ProductRepository;
import com.example.DepartmentalCrudApplication.service.CustomerService;
import com.example.DepartmentalCrudApplication.service.ProductInventoryService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
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

    HashMap<Long, LinkedList<Customer>> backordersRecord = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

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
        return backordersRecord;
    }

    @Override
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }
}


