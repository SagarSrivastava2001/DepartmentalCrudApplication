package com.example.DepartmentalCrudApplication.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ProductInventoryUpdateDTO {
    private String expiry;
    private Long count;
    private Boolean availability;
}
