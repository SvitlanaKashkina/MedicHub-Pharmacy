package com.medichub.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private Double amount;
    private LocalDateTime paymentDate;
    private String status;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
}

