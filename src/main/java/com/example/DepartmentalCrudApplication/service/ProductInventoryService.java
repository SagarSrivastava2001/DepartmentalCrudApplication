package com.example.DepartmentalCrudApplication.service;

import com.example.DepartmentalCrudApplication.model.Product_Inventory;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductInventoryService {
    void addProduct(Product_Inventory product);
    List<Product_Inventory> allProducts();

    Optional<Product_Inventory> getProductById(Long id);

    Map<String,String> discount();

    Boolean updateProductDetails(Long id, Product_Inventory product);

}

