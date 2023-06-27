package com.example.DepartmentalCrudApplication.controller;

import com.example.DepartmentalCrudApplication.dto.ProductInventoryUpdateDTO;
import com.example.DepartmentalCrudApplication.exceptions.ProductNotFoundException;
import com.example.DepartmentalCrudApplication.model.Customer;
import com.example.DepartmentalCrudApplication.model.Product_Inventory;
import com.example.DepartmentalCrudApplication.repository.ProductRepository;
import com.example.DepartmentalCrudApplication.service.CustomerService;
import com.example.DepartmentalCrudApplication.service.ProductInventoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Api(tags = "Product API")
@RestController
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductInventoryService productInventoryService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerService customerService;

    @ApiOperation(value = "Add Product")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Product details added successfully"),
            @ApiResponse(code = 400, message = "Bad request")
    })
    @PostMapping("/addProduct")
    public String addProduct(@RequestBody Product_Inventory product) {
        productInventoryService.addProduct(product);
        logger.info("Product added: ID={}, Name={}", product.getProductId(), product.getProductName());
        return "The product details are added.";
    }

    @ApiOperation(value = "Get All Products")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/allProducts")
    public List<Product_Inventory> allProducts() {
        return productInventoryService.allProducts();
    }

    @ApiOperation(value = "Get Product by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Product not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/ProductById{id}")
    public Optional<Product_Inventory> getProductById(Long id) {

        try {
            Optional<Product_Inventory> product = productInventoryService.getProductById(id);
            if (!product.isPresent()) {
                throw new ProductNotFoundException("Product ID : " + id);
            }
            return product;
        } catch (ProductNotFoundException e) {
            logger.error("Product not found: ID={}", id);
            Product_Inventory temp = new Product_Inventory();
            return Optional.of(temp);
        }
    }

    @ApiOperation(value = "Get Discount")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/Discount")
    public Map<String, String> discount() {
        return productInventoryService.discount();
    }

    @ApiOperation(value = "Optimized Back Orders")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("BackOrders")
    public HashMap<Long, LinkedList<Customer>> optimisedBackOrders() {
        return customerService.optimisedBackOrders();
    }

    @ApiOperation(value = "Update Product Details")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Product details updated successfully"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Product not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PatchMapping("Update Product Details{id}")
    public String updateProductDetails(@PathVariable Long id, @RequestBody ProductInventoryUpdateDTO updateDTO) {

        try {
            Optional<Product_Inventory> product = productRepository.findById(id);

            if (product.isPresent()) {
                Boolean isBackOrder = productInventoryService.updateProductDetails(id, product.get());

                product.get().setExpiry(updateDTO.getExpiry());
                product.get().setCount(updateDTO.getCount());
                product.get().setAvailability(updateDTO.getAvailability());
                productRepository.save(product.get());

                if (isBackOrder) {
                    return "Customer who was in Backorder has received the product and have been removed from the backorder.";
                }
                return "Product details have been updated";
            } else {
                throw new ProductNotFoundException("Product ID : " + id);
            }
        } catch (ProductNotFoundException e) {
            logger.error("Product not found: ID={}", id);
            return "Product Not Found Error";
        }
    }
}
