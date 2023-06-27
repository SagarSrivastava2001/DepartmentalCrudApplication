package com.example.DepartmentalCrudApplication.repository;

import com.example.DepartmentalCrudApplication.model.Product_Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product_Inventory, Long> {
}
