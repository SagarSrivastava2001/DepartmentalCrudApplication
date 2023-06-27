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
import org.springframework.test.context.junit4.SpringRunner;
import javax.transaction.Transactional;
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

        String actual = productController.addProduct(product);
        String expected = "The product details are added.";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void allProducts() {
        // Product 1
        Product_Inventory product1 = new Product_Inventory();
        product1.setProductId(1L);
        product1.setAvailability(true);
        product1.setCount(43L);
        product1.setExpiry("30 Mar, 2028");
        product1.setPrice(10L);
        product1.setProductDesc("Chocolate");
        product1.setProductName("Dairy Milk");


        // Product 2
        Product_Inventory product2 = new Product_Inventory();
        product2.setProductId(2L);
        product2.setAvailability(true);
        product2.setCount(2L);
        product2.setExpiry("15 Jan, 2025");
        product2.setPrice(1000L);
        product2.setProductDesc("Toy");
        product2.setProductName("Car");


        // Product 3
        Product_Inventory product3 = new Product_Inventory();
        product3.setProductId(3L);
        product3.setAvailability(true);
        product3.setCount(12L);
        product3.setExpiry("5th July, 2025");
        product3.setPrice(50000L);
        product3.setProductDesc("Hair Straightner");
        product3.setProductName("Dyson Hair Straightner");

        // Product 4
        Product_Inventory product4 = new Product_Inventory();
        product4.setProductId(4L);
        product4.setAvailability(true);
        product4.setCount(24L);
        product4.setExpiry("21 Aug, 2028");
        product4.setPrice(1000L);
        product4.setProductDesc("Chocolate");
        product4.setProductName("Ferrero Rocher");

        List<Product_Inventory> actualProducts = productController.allProducts();
        List<Product_Inventory> expectedProducts = Arrays.asList(product1, product2, product3, product4);
        Assertions.assertEquals(expectedProducts, actualProducts);
    }

    @Test
    void getProductById() {
        Product_Inventory product1 = new Product_Inventory();
        product1.setProductId(1L);
        product1.setAvailability(true);
        product1.setCount(43L);
        product1.setExpiry("30 Mar, 2028");
        product1.setPrice(10L);
        product1.setProductDesc("Chocolate");
        product1.setProductName("Dairy Milk");

        Optional<Product_Inventory> actualProduct = productController.getProductById(1L);
        Assertions.assertTrue(actualProduct.isPresent());
        Assertions.assertEquals(product1, actualProduct.get());
    }

    @Test
    void getProductByIdException(){
        long id = 10L;
        Optional<Product_Inventory> product = productController.getProductById(id);
        Optional<Product_Inventory> expectedProduct = Optional.of(new Product_Inventory());

        Assertions.assertEquals(expectedProduct, product);
    }

    @Test
    void discount() {
        Map<String, String> discounts = productController.discount();
        Assertions.assertNotNull(discounts);
        Assertions.assertEquals(4, discounts.size());
    }

    @Test
    void updateProductDetails() {
        Optional<Product_Inventory> product = productController.getProductById(1L);
        product.get().setCount(40L);

        Product_Inventory updatedProduct = productRepository.save(product.get());
        Assertions.assertEquals(updatedProduct.getCount(), product.get().getCount());
    }

    @Test
    void noBackOrdersFalse() {
        Long productId = 1L;
        ProductInventoryUpdateDTO product = new ProductInventoryUpdateDTO();
        product.setAvailability(true);
        product.setCount(10L);
        product.setExpiry("30 Mar, 2028");

        boolean result = Boolean.parseBoolean(productController.updateProductDetails(productId, product));
        Assertions.assertFalse(result);
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
        customer1.setContactNumber( 76689092343L);
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

        boolean result = Boolean.parseBoolean(productController.updateProductDetails(id, product));

        Assertions.assertFalse(result);
    }

    @Test
    void NoRecordInBackorders(){
        long id = 1;

        ProductInventoryUpdateDTO product = new ProductInventoryUpdateDTO();
        product.setAvailability(true);
        product.setCount(10l);
        product.setExpiry("14 October, 2026");

        Boolean result = Boolean.valueOf(productController.updateProductDetails(id,product));
        Assertions.assertFalse(result);
    }

    @Test
    void checkBackorders(){
        HashMap<Long, LinkedList<Customer>> actualBackorderList = productController.optimisedBackOrders();
        LinkedList<Customer> customerList = new LinkedList<>();
        LinkedList<Customer> customerList2 = new LinkedList<>();
        LinkedList<Customer> customerList3 = new LinkedList<>();

        HashMap<Long, LinkedList<Customer>> expectedBackorderList = new HashMap<>();

        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setCustomerName("Ankit Kumar");
        customer.setCustomerAddress("Delhi");
        customer.setContactNumber(76689092343L);

        OrderDetails order = new OrderDetails();
        order.setOrderId(1L);
        order.setProductId(5L);
        order.setOrderTimestamp(Timestamp.valueOf("2023-06-11 20:03:07.576"));
        order.setQuantity(2L);

        customer.setOrderDetails(order);
        customerList.add(customer);

        expectedBackorderList.put(5L, customerList);

        Customer customer2 = new Customer();
        customer2.setCustomerId(1L);
        customer2.setCustomerName("Ankit Kumar");
        customer2.setCustomerAddress("Delhi");
        customer2.setContactNumber(76689092343L);

        OrderDetails order2 = new OrderDetails();
        order2.setOrderId(1L);
        order2.setProductId(10L);
        order2.setOrderTimestamp(Timestamp.valueOf("2023-06-11 20:03:07.576"));
        order2.setQuantity(2L);

        customer2.setOrderDetails(order2);
        customerList2.add(customer2);

        expectedBackorderList.put(10L, customerList2);

        Customer customer3 = new Customer();
        customer3.setCustomerId(7L);
        customer3.setContactNumber(7566787898L);
        customer3.setCustomerAddress("Himachal Pradesh");
        customer3.setCustomerName("Sumit Singh");

        OrderDetails order3 = new OrderDetails();
        order3.setProductId(4l);
        order3.setOrderId(26l);
        order3.setQuantity(30l);
        order3.setOrderTimestamp(Timestamp.valueOf("2023-06-21 17:34:38.054000"));

        customer3.setOrderDetails(order3);
        customerList3.add(customer3);

        expectedBackorderList.put(4l, customerList3);

        Assertions.assertEquals(expectedBackorderList, actualBackorderList);
    }
}