package com.example.DepartmentalCrudApplication.controller;

import com.example.DepartmentalCrudApplication.dto.CustomerDTO;
import com.example.DepartmentalCrudApplication.exceptions.CustomerNotFoundException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
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
    public ResponseEntity<Object> addCustomer(@Valid @RequestBody Customer customer) {
        try {
            Optional<Product_Inventory> product = productInventoryService.getProductById(customer.getOrderDetails().getProductId());
            customerService.addCustomer(customer);
            String response = customerService.discountMethod(product, customer);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to add customer details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add customer details.");
        }
    }

    @ApiOperation(value = "Get Customer by Name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Customer found", response = CustomerDTO.class),
            @ApiResponse(code = 204, message = "No content"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/getCustomerByName/{customerName}")
    public ResponseEntity<Object> getCustomerByName(@PathVariable String customerName){
        try{
            List<CustomerDTO> customers = customerService.findByCustomerName(customerName);
            if (customers.isEmpty()) {
                throw new CustomerNotFoundException("Not found in the record");
            } else {
                return ResponseEntity.ok(customers);
            }
        }
        catch (CustomerNotFoundException e){
            logger.error("Customer Not Found with name : {}", customerName);
            return ResponseEntity.ok("Customer with name " + customerName + " Not found");
        }
    }
}
