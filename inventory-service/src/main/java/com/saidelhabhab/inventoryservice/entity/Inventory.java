package com.saidelhabhab.inventoryservice.entity;

import com.saidelhabhab.inventoryservice.enums.InventoryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID productId;

    private Long variantId; // nullable

    private Integer quantity;

    private Integer reservedQuantity; // 🔥 مهم لل orders

    private UUID customerId; // optional

    @Enumerated(EnumType.STRING)
    private InventoryStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Version // 🔥 باش تمنع race condition
    private Long version;
}