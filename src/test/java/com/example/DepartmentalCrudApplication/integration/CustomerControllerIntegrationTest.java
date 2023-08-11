package com.example.DepartmentalCrudApplication.integration;

import com.example.DepartmentalCrudApplication.dto.CustomerDTO;
import com.example.DepartmentalCrudApplication.model.Customer;
import com.example.DepartmentalCrudApplication.model.OrderDetails;
import com.example.DepartmentalCrudApplication.service.CustomerService;
import com.example.DepartmentalCrudApplication.service.ProductInventoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.sql.Timestamp;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerControllerIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductInventoryService productInventoryService;


    @Test
    @Sql(statements = {
            "UPDATE PRODUCT_INVENTORY SET count = count + (SELECT quantity FROM ORDER_DETAILS WHERE order_id = (SELECT max(order_id) FROM ORDER_DETAILS)) WHERE product_id = (SELECT product_id FROM ORDER_DETAILS WHERE order_id = (SELECT max(order_id) FROM ORDER_DETAILS))",
            "DELETE FROM CUSTOMER WHERE customer_id = (SELECT max(customer_id) FROM CUSTOMER)",
            "SELECT setval('customer_customer_id_seq', (SELECT max(customer_id) FROM CUSTOMER), true)",
            "DELETE FROM ORDER_DETAILS WHERE order_id= (SELECT max(order_id) FROM ORDER_DETAILS)",
            "SELECT setval('order_details_order_id_seq', (SELECT max(order_id) FROM ORDER_DETAILS), true)"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testAddCustomer(){
        Customer customer = new Customer();
        customer.setCustomerName("Sagar Srivastava");
        customer.setContactNumber(8772345411L);
        customer.setCustomerAddress("Delhi");
        customer.setCustomerEmail("srivastavasagar2001@gmail.com");

        OrderDetails order = new OrderDetails();
        order.setProductId(2L);
        order.setOrderTimestamp(Timestamp.valueOf("2023-08-09 15:47:39.268"));
        order.setQuantity(2L);

        customer.setOrderDetails(order);

        String baseUrl = "http://localhost:" + port + "/addCustomer";
        ResponseEntity<String> response = testRestTemplate.postForEntity(baseUrl,customer,String.class);

        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertEquals("Bill details have been sent to the customer mail...", response.getBody());
    }

    @Test
    void testGetCustomerByName(){
        String customerName = "Jaskeerat Singh";
        String baseUrl = "http://localhost:" + port + "/getCustomerByName/" + customerName;

        List<CustomerDTO> response = testRestTemplate.getForEntity(baseUrl,List.class).getBody();

        Assertions.assertEquals(1, response.size());

        String newCustomerName = "xyz";
        baseUrl = "http://localhost:" + port + "/getCustomerByName/" + newCustomerName;
        String response2 = testRestTemplate.getForObject(baseUrl, String.class);
        Assertions.assertEquals("Customer with name " + newCustomerName + " Not found", response2);
    }
}
