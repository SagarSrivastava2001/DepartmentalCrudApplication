package com.example.DepartmentalCrudApplication.service;
import com.example.DepartmentalCrudApplication.model.Customer;
import java.util.*;

public interface CustomerService {
    void addCustomer(Customer customer);
    HashMap<Long, LinkedList<Customer>> optimisedBackOrders();
    Optional<Customer> getCustomerById(Long id);
}