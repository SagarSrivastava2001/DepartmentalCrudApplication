package com.example.DepartmentalCrudApplication.dto;

import lombok.Data;

@Data
public class ProductInventoryUpdateDTO {
    private String expiry;
    private Long count;
    private Boolean availability;
}
