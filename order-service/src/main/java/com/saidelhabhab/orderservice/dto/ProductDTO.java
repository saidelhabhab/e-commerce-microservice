package com.saidelhabhab.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ProductDTO {
    private UUID productId;
    private String name;
    private BigDecimal price;
    // ✅ ADD THIS
    private int quantity;
    // (optional لكن مزيان)
    private int totalQuantity;
    private boolean hasVariants;
    private List<ProductVariantDTO> variants;
}