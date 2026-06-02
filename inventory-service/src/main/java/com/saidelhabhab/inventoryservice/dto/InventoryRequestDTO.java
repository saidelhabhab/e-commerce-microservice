package com.saidelhabhab.inventoryservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class InventoryRequestDTO {

    private UUID productId;

    private Long variantId;

    private Integer quantity;

}