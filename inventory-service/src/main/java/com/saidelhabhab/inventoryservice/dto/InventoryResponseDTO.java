package com.saidelhabhab.inventoryservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class InventoryResponseDTO {

    private UUID id;
    private UUID productId;
    private Long variantId;
    private Integer quantity;
    private Integer reservedQuantity;
    private UUID customerId;
    private String status;
    private LocalDateTime updatedAt;
}