package com.example.DepartmentalCrudApplication.service;

import com.example.DepartmentalCrudApplication.dto.ProductInventoryUpdateDTO;
import com.example.DepartmentalCrudApplication.exceptions.ProductNotFoundException;
import com.example.DepartmentalCrudApplication.model.Product_Inventory;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductInventoryService {
    void addProduct(Product_Inventory product);
    List<Product_Inventory> allProducts();

    Optional<Product_Inventory> getProductById(Long id);

    Map<String,String> discount();

    Boolean updateProductDetails(Long id, @RequestBody ProductInventoryUpdateDTO updateDTO) throws ProductNotFoundException;
}

