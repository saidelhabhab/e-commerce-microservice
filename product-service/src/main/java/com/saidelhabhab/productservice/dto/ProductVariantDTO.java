package com.saidelhabhab.productservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVariantDTO {

    private Long id;   // ❗ لازم يرجع من service

    private String color;
    private String ram;
    private String storage;
    private BigDecimal price;
    private Integer quantity;
}