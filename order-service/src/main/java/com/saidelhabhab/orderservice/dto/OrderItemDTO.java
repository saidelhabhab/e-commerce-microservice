package com.saidelhabhab.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class OrderItemDTO {
    private UUID productId;
    private Long variantId;
    private int quantity;
}