package com.saidelhabhab.productservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DecreaseStockRequest {
    private Long variantId;
    private Integer quantity;
}