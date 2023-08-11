package com.example.DepartmentalCrudApplication.integration;

import com.example.DepartmentalCrudApplication.dto.ProductInventoryUpdateDTO;
import com.example.DepartmentalCrudApplication.model.Product_Inventory;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    @Sql(statements = {
            "DELETE FROM PRODUCT_INVENTORY WHERE product_id = (SELECT max(product_id) FROM PRODUCT_INVENTORY)",
            "SELECT setval('product_inventory_product_id_seq', (SELECT max(product_id) FROM PRODUCT_INVENTORY), true)"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testAddProduct(){
        Product_Inventory product = new Product_Inventory();
        product.setCount(20L);
        product.setAvailability(true);
        product.setExpiry("10 Nov, 2025");
        product.setProductName("Pepsi");
        product.setProductDesc("Cold Drink");
        product.setPrice(20L);

        String baseUrl = "http://localhost:" + port + "/addProduct";
        ResponseEntity<String> response = testRestTemplate.postForEntity(baseUrl, product, String.class);

        Assertions.assertEquals("The product details are added.", response.getBody());
    }

    @Test
    void testGetAllProducts(){
        String baseUrl = "http://localhost:" + port + "/allProducts";
        List<Product_Inventory> actualList = testRestTemplate.getForObject(baseUrl, List.class);

        Assertions.assertEquals(2, actualList.size());
    }

    @Test
    void testGetProductById(){
        Long id = 1L;
        String baseUrl = "http://localhost:" + port + "/productById/" + id;
        Product_Inventory actualProduct = testRestTemplate.getForObject(baseUrl, Product_Inventory.class);

        Assertions.assertEquals("Remote Control Car", actualProduct.getProductDesc());

        id = 10L;
        baseUrl = "http://localhost:" + port + "/productById/" + id;
        String response = testRestTemplate.getForObject(baseUrl, String.class);

        Assertions.assertEquals("Product Not found", response);
    }

    @Test
    void testDiscountMethod(){
        String baseUrl = "http://localhost:" + port + "/discount";
        Map<String, String> response = testRestTemplate.getForObject(baseUrl, Map.class);

        Assertions.assertEquals(4, response.size());
    }
}
