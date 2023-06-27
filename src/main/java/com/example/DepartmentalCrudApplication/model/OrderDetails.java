package com.example.DepartmentalCrudApplication.model;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Date;

@Data
@Entity
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class OrderDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @NotNull(message = "Product ID cannot be null")
    @Positive
    private Long productId;

    private Date orderTimestamp;

    @NotNull(message = "Quantity cannot be null")
    @Positive
    private Long quantity;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    private Customer customer;

}
