package com.example.DepartmentalCrudApplication.controller;

import com.example.DepartmentalCrudApplication.dao.BackorderDao;
import com.example.DepartmentalCrudApplication.dto.CustomerDTO;
import com.example.DepartmentalCrudApplication.model.Backorders;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;
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
        customer.setContactNumber( 7668909234L);
        customer.setCustomerAddress("Delhi");
        customer.setCustomerName("Ankit Kumar");

        OrderDetails order = new OrderDetails();
        order.setProductId(1L);
        order.setOrderId(1L);
        order.setQuantity(2L);
        order.setOrderTimestamp(Timestamp.valueOf("2023-07-11 13:08:11.343000"));

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
        ResponseEntity<Object> responseEntity = customerController.addCustomer(customer);

        String expectedMessage = "The product is out of stock for now.\nWe'll notify you once the product is restocked";
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(expectedMessage, responseEntity.getBody());
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
        ResponseEntity<Object> responseEntity = customerController.addCustomer(customer);

        String expectedMessage = "The product is out of stock for now.\nWe'll notify you once the product is restocked ";
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(expectedMessage, responseEntity.getBody());
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

        ResponseEntity<Object> responseEntity1 = customerController.addCustomer(customer);
        String actualResponse = (String) responseEntity1.getBody();
        String expectedResponse = "The customer details are added.\n\nThe price is 4000.\n\nDiscounted Price is 400.0.\n\nPlease Pay. 3600.0";
        Assertions.assertEquals(HttpStatus.OK, responseEntity1.getStatusCode());
        Assertions.assertEquals(expectedResponse, actualResponse);

        // Assert - 2

        order.setQuantity(8L);
        quantity = order.getQuantity();
        customer.setOrderDetails(order);

        ResponseEntity<Object> responseEntity2 = customerController.addCustomer(customer);
        String actualResponse2 = (String) responseEntity2.getBody();
        String expectedResponse2 = "The customer details are added.\n\nThe price is 8000.\n\nDiscounted Price is 1600.0.\n\nPlease Pay. 6400.0";
        Assertions.assertEquals(HttpStatus.OK, responseEntity1.getStatusCode());
        Assertions.assertEquals(expectedResponse2, actualResponse2);


        // Assert - 3
        order.setQuantity(1L);
        quantity = order.getQuantity();
        customer.setOrderDetails(order);
        long expectedPrice = productPrice * quantity;
        ResponseEntity<Object> responseEntity3 = customerController.addCustomer(customer);
        Assertions.assertEquals(HttpStatus.OK, responseEntity3.getStatusCode());
        Assertions.assertTrue(responseEntity3.getBody().toString().contains(String.valueOf(expectedPrice)));
    }

    @Test
    void getCustomerByName(){
        CustomerDTO customer = new CustomerDTO();
        customer.setCustomerAddress("Delhi");
        customer.setCustomerName("Ankit Kumar");
        customer.setCustomerId(1L);
        customer.setContactNumber(7668909234L);
        customer.setOrderId(1L);
        customer.setQuantity(2L);
        customer.setProductId(1L);
        customer.setOrderTimestamp(Timestamp.valueOf("2023-07-11 13:08:11.343000"));

        ResponseEntity<Object> response = customerController.getCustomerByName("Ankit Kumar");
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        List<CustomerDTO> responseList = (List<CustomerDTO>) response.getBody();
        CustomerDTO actualResponse = responseList.get(0);
        Assertions.assertEquals(customer, actualResponse);
    }

    @Test
    void getCustomerByNameException(){
        String customerName = "Akshay Kumar";
        ResponseEntity<Object> responseEntity = customerController.getCustomerByName(customerName);
        String actualResponse = (String)responseEntity.getBody();
        String expectedResponse = "Customer with name " + customerName + " Not found";
        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testBackorderId(){
        Backorders backorder = new Backorders();
        backorder.setBackorderId(1L);
        Assertions.assertNotNull(backorder.getBackorderId());
    }
}