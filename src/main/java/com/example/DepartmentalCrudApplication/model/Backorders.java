package com.example.DepartmentalCrudApplication.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Date;


@Data
@Entity
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Backorders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long backorderId;

    private Long customerId;
    private String customerName;
    private String customerAddress;
    private Long contactNumber;
    private Long orderId;
    private Long productId;
    private Date orderTimestamp;
    private Long quantity;
}
