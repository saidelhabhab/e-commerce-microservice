package com.saidelhabhab.orderservice.entity;

import com.saidelhabhab.orderservice.enums.DeliveryStatus;
import com.saidelhabhab.orderservice.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private UUID orderId;

    private UUID customerId;

    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> items;

    @PrePersist
    public void prePersist() {
        this.orderId = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.CREATED;
        this.deliveryStatus = DeliveryStatus.NOT_SHIPPED;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}