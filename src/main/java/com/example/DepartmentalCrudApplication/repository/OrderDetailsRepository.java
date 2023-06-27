package com.example.DepartmentalCrudApplication.repository;

import com.example.DepartmentalCrudApplication.model.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
}
