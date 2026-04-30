package com.saidelhabhab.productservice.dto;

import com.saidelhabhab.productservice.enums.ProductCategory;
import com.saidelhabhab.productservice.enums.TaxClass;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequestDTO {

    private String barcode;

    @NotBlank
    private String name;
    private String description;
    @Positive
    private BigDecimal price;

    private BigDecimal oldPrice;
    private BigDecimal discount;
    private boolean inStock;
    private Integer quantity;
    private String brand;
    @NotNull
    private ProductCategory category;
    private TaxClass taxClass;

    private List<ProductVariantDTO> variants;

}