package com.example.DepartmentalCrudApplication.service;
import com.example.DepartmentalCrudApplication.dto.CustomerDTO;
import com.example.DepartmentalCrudApplication.model.Customer;
import com.example.DepartmentalCrudApplication.model.Product_Inventory;

import java.util.*;

public interface CustomerService {
    void addCustomer(Customer customer);
    HashMap<Long, LinkedList<Customer>> optimisedBackOrders();
    Optional<Customer> getCustomerById(Long id);
    String discountMethod(Optional<Product_Inventory> product, Customer customer);

    List<CustomerDTO> findByCustomerName(String customerName);

    String sendEmail(String toEmail, String body, String subject);
}