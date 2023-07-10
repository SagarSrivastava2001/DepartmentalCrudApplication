package com.example.DepartmentalCrudApplication.dto;

import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.Date;

@Data
@Component
public class CustomerDTO {
    private Long customerId;
    private String customerName;
    private String customerAddress;
    private Long contactNumber;
    private Long orderId;
    private Long productId;
    private Date orderTimestamp;
    private Long quantity;
}
