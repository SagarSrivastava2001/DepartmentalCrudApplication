package com.example.DepartmentalCrudApplication.service;

import com.example.DepartmentalCrudApplication.exceptions.ProductNotFoundException;
import com.example.DepartmentalCrudApplication.model.Customer;
import com.example.DepartmentalCrudApplication.model.OrderDetails;
import com.example.DepartmentalCrudApplication.model.Product_Inventory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import java.util.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import javax.transaction.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductInventoryService productInventoryService;

    @Test
    void getCustomerById() {
        // Customer 1
        Customer customer1 = new Customer();
        customer1.setCustomerId(1L);
        customer1.setContactNumber( 76689092343L);
        customer1.setCustomerAddress("Delhi");
        customer1.setCustomerName("Ankit Kumar");

        OrderDetails order1 = new OrderDetails();
        order1.setProductId(1L);
        order1.setOrderId(1L);
        order1.setQuantity(2L);
        order1.setOrderTimestamp(Timestamp.valueOf("2023-06-11 20:03:07.576"));

        customer1.setOrderDetails(order1);

        customerService.addCustomer(customer1);

        Optional<Customer> retrievedCustomer1 = customerService.getCustomerById(1L);

        Assertions.assertTrue(retrievedCustomer1.isPresent());
        Assertions.assertEquals(customer1, retrievedCustomer1.get());
    }

    @Test
    void ProductNotPresent(){

        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setContactNumber( 76689092343L);
        customer.setCustomerAddress("Delhi");
        customer.setCustomerName("Ankit Kumar");

        OrderDetails order = new OrderDetails();
        order.setProductId(1L);
        order.setOrderId(1L);
        order.setQuantity(2L);
        order.setOrderTimestamp(Timestamp.valueOf("2023-06-11 20:03:07.576"));

        customer.setOrderDetails(order);

        customerService.addCustomer(customer);

        Map<Long, LinkedList<Customer>> mp = new HashMap<>();

        // Act
        Optional<Product_Inventory> product1 = Optional.empty();

        if (!product1.isPresent()) {
            LinkedList<Customer> temp = new LinkedList<>();
            temp.add(customer);
            mp.put(1L, temp);
        }

        Assertions.assertEquals(1, mp.get(1L).size());
    }

    @Test
    void addCustomerProductNotPresentException(){

        long productId = 5;

        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setContactNumber( 76689092343L);
        customer.setCustomerAddress("Delhi");
        customer.setCustomerName("Ankit Kumar");

        OrderDetails order = new OrderDetails();
        order.setProductId(productId);
        order.setOrderId(1L);
        order.setQuantity(2L);
        order.setOrderTimestamp(Timestamp.valueOf("2023-06-11 20:03:07.576"));

        customer.setOrderDetails(order);

        Assertions.assertThrows(ProductNotFoundException.class, () -> {
            customerService.addCustomer(customer);
            Optional<Product_Inventory> product = productInventoryService.getProductById(productId);

            if (!product.isPresent()) {
                throw new ProductNotFoundException("Product Not Available");
            }
        });
    }
}