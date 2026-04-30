package com.saidelhabhab.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductVariantDTO {
    private Long id;
    private BigDecimal price;
    private String sku;
    private int quantity;
}