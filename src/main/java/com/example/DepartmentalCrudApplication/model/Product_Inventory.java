package com.example.DepartmentalCrudApplication.model;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Entity
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Product_Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotEmpty(message = "Product Description field can't be empty")
    private String productDesc;

    @NotEmpty(message = "Product Name field can't be empty")
    private String productName;

    @Positive
    @NotNull(message = "Price cannot be null")
    private Long price;

    @NotEmpty(message = "Expiry field can't be empty")
    private String expiry;

    @Positive
    @NotNull(message = "Product Count cannot be null")
    private Long count;

    @NotNull(message = "Availability cannot be null")
    private Boolean availability;
}
