package com.example.DepartmentalCrudApplication.controller;

import com.example.DepartmentalCrudApplication.model.Customer;
import com.example.DepartmentalCrudApplication.model.Product_Inventory;
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
import javax.validation.Valid;
import java.util.Optional;

@Api(tags = "Customer API")
@RestController
public class CustomerController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductInventoryService productInventoryService;

    @ApiOperation(value = "Add Customer Details")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Customer details added successfully"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Product not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping("Customer Details")
    public String addCustomer(@Valid @RequestBody Customer customer) {
        try {
            Optional<Product_Inventory> product = productInventoryService.getProductById(customer.getOrderDetails().getProductId());
            customerService.addCustomer(customer);

            if (!product.isPresent() || product.isEmpty()) {
                logger.warn("Product not found for customer ID: {}", customer.getCustomerId());
                return "The product is out of stock for now.\nWe'll notify you once the product is restocked";
            }

            Boolean productAvailability = product.get().getAvailability();
            Long productCount = product.get().getCount();
            Long quantity = customer.getOrderDetails().getQuantity();

            if (productAvailability == false || quantity > productCount) {
                logger.warn("Product is out of stock for customer ID: {}", customer.getCustomerId());
                return "The product is out of stock for now.\nWe'll notify you once the product is restocked ";
            }

            long price = (product.get().getPrice() * customer.getOrderDetails().getQuantity());
            double newPrice = 0;
            Boolean isDiscount = false;

            // Discount = 5%
            if (price >= 2000 && price < 4000) {
                newPrice = price * 0.05;
                isDiscount = true;
            }
            // Discount = 10%
            else if (price >= 4000 && price < 8000) {
                newPrice = price * 0.10;
                isDiscount = true;
            }
            // Discount = 20%
            else if (price >= 8000) {
                newPrice = price * 0.20;
                isDiscount = true;
            }

            if (isDiscount == false) {
                logger.info("Customer details added for customer ID: {}. Price: {}", customer.getCustomerId(), price);
                return "The customer details are added.\n\nThe price is " + (product.get().getPrice() * customer.getOrderDetails().getQuantity()) + ". Please Pay.";
            }
            else{
                logger.info("Customer details added for customer ID: {}. Price: {}. Discounted Price: {}", customer.getCustomerId(), price, newPrice);
                return "The customer details are added.\n\nThe price is " + (product.get().getPrice() * customer.getOrderDetails().getQuantity()) + ".\n\nDiscounted Price is " + newPrice + ".\n\nPlease Pay. " + ((product.get().getPrice() * customer.getOrderDetails().getQuantity()) - newPrice);
            }
        } catch (Exception e) {
            logger.error("Failed to add customer details: {}", e.getMessage());
            return "Failed to add customer details.";
        }
    }
}
