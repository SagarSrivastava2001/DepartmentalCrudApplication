package com.example.DepartmentalCrudApplication.controller;

import com.example.DepartmentalCrudApplication.model.Customer;
import com.example.DepartmentalCrudApplication.model.OrderDetails;
import com.example.DepartmentalCrudApplication.model.Product_Inventory;
import com.example.DepartmentalCrudApplication.service.CustomerService;
import com.example.DepartmentalCrudApplication.service.ProductInventoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class CustomerControllerTest {

    @Autowired
    private CustomerController customerController;

    @Autowired
    private ProductController productController;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductInventoryService productInventoryService;

    private static final Logger logger = LoggerFactory.getLogger(CustomerControllerTest.class);

    @Test
    void addCustomer() {
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
        customerController.addCustomer(customer);

        Optional<Customer> retrievedCustomer = customerService.getCustomerById(1L);
        Assertions.assertEquals(customer,retrievedCustomer.get());
    }

    @Test
    void addCustomerWithInvalidProduct(){
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setContactNumber( 76689092343L);
        customer.setCustomerAddress("Delhi");
        customer.setCustomerName("Ankit Kumar");

        OrderDetails order = new OrderDetails();
        order.setProductId(10L);
        order.setOrderId(1L);
        order.setQuantity(2L);
        order.setOrderTimestamp(Timestamp.valueOf("2023-06-11 20:03:07.576"));

        customer.setOrderDetails(order);
        String response = customerController.addCustomer(customer);

        String expectedMessage = "The product is out of stock for now.\nWe'll notify you once the product is restocked";
        Assertions.assertEquals(expectedMessage, response);
        Assertions.assertTrue(logger.isWarnEnabled());
    }

    @Test
    void addCustomerProductNotAvailable(){
        Customer customer = new Customer();
        customer.setCustomerId(7L);
        customer.setContactNumber(7566787898L);
        customer.setCustomerAddress("Himachal Pradesh");
        customer.setCustomerName("Sumit Singh");

        OrderDetails order = new OrderDetails();
        order.setProductId(4l);
        order.setOrderId(26l);
        order.setQuantity(30l);
        order.setOrderTimestamp(Timestamp.valueOf("2023-06-21 17:34:38.054000"));

        customer.setOrderDetails(order);
        String response = customerController.addCustomer(customer);

        String expectedMessage = "The product is out of stock for now.\nWe'll notify you once the product is restocked ";
        Assertions.assertEquals(expectedMessage, response);
        Assertions.assertTrue(logger.isWarnEnabled());
    }

    @Test
    void DiscountPriceTest(){
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setContactNumber( 76689092343L);
        customer.setCustomerAddress("Delhi");
        customer.setCustomerName("Ankit Kumar");

        OrderDetails order = new OrderDetails();
        order.setProductId(4L);
        order.setOrderId(1L);
        order.setQuantity(2L);
        order.setOrderTimestamp(Timestamp.valueOf("2023-06-11 20:03:07.576"));

        customer.setOrderDetails(order);
        customerController.addCustomer(customer);

        Optional<Product_Inventory> product = productInventoryService.getProductById(4L);

        // Assert-1

        order.setQuantity(4L);
        customer.setOrderDetails(order);
        customerController.addCustomer(customer);

        long productPrice = product.get().getPrice();
        long quantity = order.getQuantity();

        Assertions.assertEquals(3800L, productPrice * quantity * 0.95);

        // Assert - 2

        order.setQuantity(8L);
        quantity = order.getQuantity();
        customer.setOrderDetails(order);
        String result = customerController.addCustomer(customer);

        Assertions.assertEquals(6400L,productPrice * quantity * 0.80);

        order.setQuantity(1L);
        quantity = order.getQuantity();
        customer.setOrderDetails(order);
        result = customerController.addCustomer(customer);

        Assertions.assertTrue(result.contains(String.valueOf(productPrice * quantity)));
    }
}