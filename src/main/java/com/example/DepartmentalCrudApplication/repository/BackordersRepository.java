package com.example.DepartmentalCrudApplication.repository;

import com.example.DepartmentalCrudApplication.model.Backorders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BackordersRepository extends JpaRepository<Backorders, Long>{
}
