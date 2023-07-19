package com.example.DepartmentalCrudApplication.service;

import com.example.DepartmentalCrudApplication.dto.ProductInventoryUpdateDTO;
import com.example.DepartmentalCrudApplication.exceptions.ProductNotFoundException;
import com.example.DepartmentalCrudApplication.model.Customer;
import com.example.DepartmentalCrudApplication.model.OrderDetails;
import com.example.DepartmentalCrudApplication.model.Product_Inventory;
import com.example.DepartmentalCrudApplication.repository.ProductRepository;
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
class ProductInventoryServiceTest {

    @Autowired
    private ProductInventoryService productInventoryService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductInventoryUpdateDTO productInventoryUpdateDTO;

    private Map<Long, LinkedList<Customer>> mp = new HashMap<>();

    @Test
    public void addProduct() {

        // Product 1
        Product_Inventory product1 = new Product_Inventory();
        product1.setProductId(1L);
        product1.setAvailability(true);
        product1.setCount(43L);
        product1.setExpiry("30 Mar, 2028");
        product1.setPrice(10L);
        product1.setProductDesc("Chocolate");
        product1.setProductName("Dairy Milk");

        productInventoryService.addProduct(product1);

        Optional<Product_Inventory> retrievedProduct1 = productInventoryService.getProductById(1L);
        Assertions.assertTrue(retrievedProduct1.isPresent());
        Assertions.assertEquals(product1, retrievedProduct1.get());

        // Product 2
        Product_Inventory product2 = new Product_Inventory();
        product2.setProductId(2L);
        product2.setAvailability(true);
        product2.setCount(30L);
        product2.setExpiry("15 Jan, 2025");
        product2.setPrice(1000L);
        product2.setProductDesc("Toy");
        product2.setProductName("Car");

        productInventoryService.addProduct(product2);

        Optional<Product_Inventory> retrievedProduct2 = productInventoryService.getProductById(2L);
        Assertions.assertTrue(retrievedProduct2.isPresent());
        Assertions.assertEquals(product2, retrievedProduct2.get());

        // Product 3
        Product_Inventory product3 = new Product_Inventory();
        product3.setProductId(3L);
        product3.setAvailability(true);
        product3.setCount(10L);
        product3.setExpiry("5th July, 2025");
        product3.setPrice(50000L);
        product3.setProductDesc("Hair Straightner");
        product3.setProductName("Dyson Hair Straightner");

        productInventoryService.addProduct(product3);

        Optional<Product_Inventory> retrievedProduct3 = productInventoryService.getProductById(3L);
        Assertions.assertTrue(retrievedProduct3.isPresent());
        Assertions.assertEquals(product3, retrievedProduct3.get());
    }

    @Test
    void allProducts() {
        // Product 1
        Product_Inventory product1 = new Product_Inventory();
        product1.setProductId(1L);
        product1.setAvailability(true);
        product1.setCount(35L);
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
        product3.setCount(10L);
        product3.setExpiry("5th July, 2025");
        product3.setPrice(50000L);
        product3.setProductDesc("Hair Straightner");
        product3.setProductName("Dyson Hair Straightner");

        // Product 4
        Product_Inventory product4 = new Product_Inventory();
        product4.setProductId(4L);
        product4.setAvailability(true);
        product4.setCount(50L);
        product4.setExpiry("21 Aug, 2028");
        product4.setPrice(1000L);
        product4.setProductDesc("Chocolate");
        product4.setProductName("Ferrero Rocher");

        List<Product_Inventory> actualProducts = productInventoryService.allProducts();
        List<Product_Inventory> expectedProducts = Arrays.asList(product1,product2,product3,product4);
        Assertions.assertEquals(expectedProducts,actualProducts);
    }

    @Test
    void getProductById() {
        Product_Inventory product1 = new Product_Inventory();
        product1.setProductId(1L);
        product1.setAvailability(true);
        product1.setCount(35L);
        product1.setExpiry("30 Mar, 2028");
        product1.setPrice(10L);
        product1.setProductDesc("Chocolate");
        product1.setProductName("Dairy Milk");

        Optional<Product_Inventory> actualProduct = productInventoryService.getProductById(1L);
        Assertions.assertTrue(actualProduct.isPresent());
        Assertions.assertEquals(product1, actualProduct.get());
    }

    @Test
    void getProductByIdException(){
        Long productId = 10L;

        Assertions.assertThrows(ProductNotFoundException.class, () -> {
            Optional<Product_Inventory> product = productInventoryService.getProductById(productId);

            if (!product.isPresent()) {
                throw new ProductNotFoundException("Product Not Available");
            }
        });
    }

    @Test
    void discount() {
        Map<String, String> discounts = productInventoryService.discount();
        Assertions.assertNotNull(discounts);
        Assertions.assertEquals(4, discounts.size());
    }

    @Test
    void updateProductDetails() {
        Optional<Product_Inventory> product = productInventoryService.getProductById(1L);
        product.get().setCount(40L);

        Product_Inventory updatedProduct = productRepository.save(product.get());
        Assertions.assertEquals(updatedProduct.getCount(),product.get().getCount());
    }

    @Test
    void noBackOrdersFalse() {

        Long productId = 1L;

        ProductInventoryUpdateDTO product = new ProductInventoryUpdateDTO();
        product.setAvailability(true);
        product.setCount(10L);

        boolean result = productInventoryService.updateProductDetails(productId, product);

        Assertions.assertTrue(result);
    }

    @Test
    void noCustomerFalse() {
        long id = 5L;
        ProductInventoryUpdateDTO product = new ProductInventoryUpdateDTO();
        product.setCount(10L);

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

        boolean result = productInventoryService.updateProductDetails(id, product);

        Assertions.assertFalse(result);
    }

    @Test
    void removeCustomersAndUpdate() {

        Customer customer = new Customer();
        customer.setCustomerId(5L);
        customer.setCustomerName("Amit Kumar");
        customer.setCustomerAddress("Delhi");
        customer.setContactNumber(76689782343L);

        OrderDetails order = new OrderDetails();
        order.setOrderId(10L);
        order.setOrderTimestamp(Timestamp.valueOf("2023-06-11 20:03:07.576000"));
        order.setQuantity(5L);
        order.setProductId(2L);

        customer.setOrderDetails(order);

        customerService.addCustomer(customer);

        ProductInventoryUpdateDTO product = new ProductInventoryUpdateDTO();
        product.setExpiry("15 Jan, 2025");
        product.setCount(30L);
        product.setAvailability(true);

        boolean result = productInventoryService.updateProductDetails(2L, product);

        Assertions.assertTrue(result);

        product.setCount(20L);
        productInventoryService.updateProductDetails(2L, product);
        List<Customer> filteredCustomer = new LinkedList<>();
        Assertions.assertTrue(filteredCustomer.isEmpty());


        product.setCount(20L);
        customer.getOrderDetails().setQuantity(20L);
        customerService.addCustomer(customer);
        productInventoryService.updateProductDetails(2L, product);
        Assertions.assertTrue(product.getAvailability());
    }

    @Test
    void NoRecordInBackorders(){
        long id = 1;

        ProductInventoryUpdateDTO product = new ProductInventoryUpdateDTO();
        product.setAvailability(true);
        product.setCount(10l);

        Boolean result = productInventoryService.updateProductDetails(id,product);
        Assertions.assertTrue(result);
    }
}