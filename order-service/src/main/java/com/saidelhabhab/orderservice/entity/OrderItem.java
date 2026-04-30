package com.saidelhabhab.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID productId;

    private Long variantId;   // ⭐ مهم nullable

    private String sku;       // ⭐ مهم nullable (for simple product)

    private String productName; // snapshot ❗

    private int quantity;

    private BigDecimal price; // snapshot ❗

    private BigDecimal total;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}