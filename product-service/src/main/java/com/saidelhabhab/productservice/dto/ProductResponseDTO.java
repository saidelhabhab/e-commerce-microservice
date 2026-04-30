package com.saidelhabhab.productservice.dto;

import com.saidelhabhab.productservice.enums.ProductCategory;
import com.saidelhabhab.productservice.enums.TaxClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {

    // =========================
    // IDENTIFIERS
    // =========================
    private UUID productId;
    private String barcode;

    // =========================
    // BASIC INFO
    // =========================
    private String name;
    private String slug;
    private String description;
    private String brand;

    // =========================
    // PRICING
    // =========================
    private BigDecimal price;
    private BigDecimal oldPrice;
    private BigDecimal discount;

    // 💡 مهم: price محسوب (min variant أو product)
    private BigDecimal effectivePrice;

    // =========================
    // STOCK
    // =========================
    private boolean inStock;
    private Integer quantity;

    // 💡 مجموع الكمية (variants)
    private int totalQuantity;

    // =========================
    // CLASSIFICATION
    // =========================
    private ProductCategory category;
    private TaxClass taxClass;
    private String status;

    // =========================
    // VARIANTS
    // =========================
    private boolean hasVariants;
    private List<ProductVariantDTO> variants;

    // =========================
    // IMAGES
    // =========================
    private String imageUrl;
    private List<String> images;

    // =========================
    // MARKETING
    // =========================
    private boolean isFeatured;
    private boolean isNew;
    private boolean isBestSeller;

    // =========================
    // RATING
    // =========================
    private Double averageRating;
    private int ratingCount;

    // =========================
    // SEARCH
    // =========================
    private List<String> keywords;

    // =========================
    // AUDIT
    // =========================
    private String createdAt;
    private String updatedAt;
}