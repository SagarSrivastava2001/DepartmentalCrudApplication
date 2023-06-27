package com.example.DepartmentalCrudApplication.model;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.*;

@Data
@Entity
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @NotEmpty(message = "Customer name field can't be empty")
    private String customerName;

    @NotEmpty(message = "Address field can't be empty")
    private String customerAddress;

    @NotNull(message = "Contact Number field can't be empty")
    private Long contactNumber;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private OrderDetails orderDetails;
}
