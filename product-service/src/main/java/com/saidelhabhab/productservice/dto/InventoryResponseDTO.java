package com.saidelhabhab.productservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class InventoryResponseDTO {

    private UUID id;

    private UUID productId;

    private Long variantId;

    private Integer quantityAvailable;

    private Integer quantityReserved;

    private Integer effectiveStock;

    private String status;

}