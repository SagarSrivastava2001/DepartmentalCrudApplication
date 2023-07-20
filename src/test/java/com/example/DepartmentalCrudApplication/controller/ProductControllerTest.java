package com.example.DepartmentalCrudApplication.controller;

import com.example.DepartmentalCrudApplication.dto.ProductInventoryUpdateDTO;
import com.example.DepartmentalCrudApplication.model.Customer;
import com.example.DepartmentalCrudApplication.model.OrderDetails;
import com.example.DepartmentalCrudApplication.model.Product_Inventory;
import com.example.DepartmentalCrudApplication.repository.ProductRepository;
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
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class ProductControllerTest {

    @Autowired
    ProductController productController = new ProductController();

    private ProductInventoryService productInventoryService;

    @Autowired
    private ProductRepository productRepository;

    private Map<Long, LinkedList<Customer>> mp = new HashMap<>();


    @Autowired
    public ProductControllerTest(ProductInventoryService productInventoryService) {
        this.productInventoryService = productInventoryService;
    }

    @Test
    void addProduct() {
        Product_Inventory product = new Product_Inventory();
        product.setProductId(1L);
        product.setProductName("Dairy Milk");
        product.setCount(10L);
        product.setProductDesc("Chocolate");
        product.setAvailability(true);
        product.setExpiry("30 Mar, 2028");

        ResponseEntity<Object> responseEntity = productController.addProduct(product);
        String actual = responseEntity.getBody().toString();
        String expected = "The product details are added.";
        Assertions.assertEquals(expected, actual);
    }

//    @Test
//    void allProducts() {
//        // Product 1
//        Product_Inventory product1 = new Product_Inventory();
//        product1.setProductId(1L);
//        product1.setAvailability(true);
//        product1.setCount(35L);
//        product1.setExpiry("30 Mar, 2028");
//        product1.setPrice(10L);
//        product1.setProductDesc("Chocolate");
//        product1.setProductName("Dairy Milk");
//
//
//        // Product 2
//        Product_Inventory product2 = new Product_Inventory();
//        product2.setProductId(2L);
//        product2.setAvailability(true);
//        product2.setCount(2L);
//        product2.setExpiry("15 Jan, 2025");
//        product2.setPrice(1000L);
//        product2.setProductDesc("Toy");
//        product2.setProductName("Car");
//
//
//        // Product 3
//        Product_Inventory product3 = new Product_Inventory();
//        product3.setProductId(3L);
//        product3.setAvailability(true);
//        product3.setCount(10L);
//        product3.setExpiry("5th July, 2025");
//        product3.setPrice(50000L);
//        product3.setProductDesc("Hair Straightner");
//        product3.setProductName("Dyson Hair Straightner");
//
//        // Product 4
//        Product_Inventory product4 = new Product_Inventory();
//        product4.setProductId(4L);
//        product4.setAvailability(true);
//        product4.setCount(50L);
//        product4.setExpiry("21 Aug, 2028");
//        product4.setPrice(1000L);
//        product4.setProductDesc("Chocolate");
//        product4.setProductName("Ferrero Rocher");
//
//        ResponseEntity<Object> responseEntity = productController.allProducts();
//        List<Product_Inventory> actualProducts = (List<Product_Inventory>) responseEntity.getBody();
//        List<Product_Inventory> expectedProducts = Arrays.asList(product1, product2, product3, product4);
//        Assertions.assertEquals(expectedProducts, actualProducts);
//    }
//
//    @Test
//    void getProductById() {
//        Product_Inventory product1 = new Product_Inventory();
//        product1.setProductId(1L);
//        product1.setAvailability(true);
//        product1.setCount(35L);
//        product1.setExpiry("30 Mar, 2028");
//        product1.setPrice(10L);
//        product1.setProductDesc("Chocolate");
//        product1.setProductName("Dairy Milk");
//
//        ResponseEntity<Object> responseEntity = productController.getProductById(1L);
//        Optional<Product_Inventory> actualProduct = (Optional<Product_Inventory>) responseEntity.getBody();
//        Assertions.assertTrue(actualProduct.isPresent());
//        Assertions.assertEquals(product1, actualProduct.get());
//    }

    @Test
    void getProductByIdException(){
        long id = 10L;
        ResponseEntity<Object> responseEntity = productController.getProductById(id);
        String expected = "Product Not found";
        String actual = (String) responseEntity.getBody();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void discount() {
        ResponseEntity<Object> responseEntity = productController.discount();
        Map<String, String> discounts = (Map<String, String>) responseEntity.getBody();
        Assertions.assertNotNull(discounts);
        Assertions.assertEquals(4, discounts.size());
    }

    @Test
    void updateProductDetails() {
        ResponseEntity<Object> responseEntity = productController.getProductById(1L);
        Optional<Product_Inventory> product = (Optional<Product_Inventory>) responseEntity.getBody();
        if (product.isPresent()) {
            product.get().setCount(40L);
            Product_Inventory updatedProduct = productRepository.save(product.get());
            Assertions.assertEquals(updatedProduct.getCount(), product.get().getCount());
        } else {
            Assertions.fail("Product not found");
        }
    }

    @Test
    void noBackOrdersFalse() {
        Long productId = 1L;
        ProductInventoryUpdateDTO product = new ProductInventoryUpdateDTO();
        product.setAvailability(true);
        product.setCount(10L);
        product.setExpiry("30 Mar, 2028");

        ResponseEntity<Object> response = productController.updateProductDetails(productId, product);
        String message = response.getBody().toString();
        Assertions.assertTrue(message.contains("Customer who was in Backorder has received the product"));
    }

    @Test
    void noCustomerFalse() {
        long id = 5L;
        ProductInventoryUpdateDTO product = new ProductInventoryUpdateDTO();
        product.setCount(10L);
        product.setExpiry("15 July, 2030");
        product.setAvailability(true);

        Customer customer1 = new Customer();
        customer1.setCustomerId(1L);
        customer1.setContactNumber(76689092343L);
        customer1.setCustomerAddress("Delhi");
        customer1.setCustomerName("Ankit Kumar");

        OrderDetails order1 = new OrderDetails();
        order1.setProductId(1L);
        order1.setOrderId(1L);
        order1.setQuantity(2L);
        order1.setOrderTimestamp(Timestamp.valueOf("2023-06-11 20:03:07.576"));

        customer1.setOrderDetails(order1);
        LinkedList<Customer> lt = new LinkedList<>();
        lt.add(customer1);

        mp.put(5L, lt);

        ResponseEntity<Object> response = productController.updateProductDetails(id, product);

        Assertions.assertFalse(Boolean.parseBoolean(response.getBody().toString()));
    }


    @Test
    void NoRecordInBackorders() {
        long id = 1L;

        ProductInventoryUpdateDTO product = new ProductInventoryUpdateDTO();
        product.setAvailability(true);
        product.setCount(10L);
        product.setExpiry("14 October, 2026");

        ResponseEntity<Object> response = productController.updateProductDetails(id, product);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertFalse(Boolean.parseBoolean(response.getBody().toString()));
    }


    @Test
    void checkBackorders(){
        HashMap<Long, LinkedList<Customer>> actualBackorderList = (HashMap<Long, LinkedList<Customer>>)((productController.optimisedBackOrders()).getBody());

        LinkedList<Customer> customerList = new LinkedList<>();
        HashMap<Long, LinkedList<Customer>> expectedBackorderList = new HashMap<>();

        Customer customer = new Customer();
        customer.setCustomerId(2L);
        customer.setCustomerName("Manik Kumar");
        customer.setCustomerAddress("Delhi");
        customer.setContactNumber(7668909224L);

        OrderDetails order = new OrderDetails();
        order.setOrderId(2L);
        order.setProductId(4L);
        order.setOrderTimestamp(Date.valueOf("2023-07-11"));
        order.setQuantity(1000L);

        customer.setOrderDetails(order);
        customerList.add(customer);

        expectedBackorderList.put(4L, customerList);

        Assertions.assertEquals(expectedBackorderList, actualBackorderList);
    }
}