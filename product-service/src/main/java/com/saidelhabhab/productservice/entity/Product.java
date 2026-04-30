package com.saidelhabhab.productservice.entity;

import com.saidelhabhab.productservice.enums.ProductCategory;
import com.saidelhabhab.productservice.enums.ProductStatus;
import com.saidelhabhab.productservice.enums.TaxClass;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"variants", "images"}) // تجنب infinite loop
public class Product {

    // =========================
    // IDENTIFIERS
    // =========================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // UUID public ID (أفضل من expose id)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID productId;


    // =========================
    // BASIC INFO
    // =========================

    @Column(unique = true)
    private String barcode;

    @Column(nullable = false)
    private String name;

    // SEO slug (auto-generated)
    @Column(unique = true)
    private String slug;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    private String brand;


    // =========================
    // PRICING
    // =========================

    // يستخدم فقط إذا ما كانش variants
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    private BigDecimal oldPrice;

    private BigDecimal discount;


    // =========================
    // STOCK
    // =========================

    // simple product stock
    private boolean inStock;

    private Integer  quantity;


    // =========================
    // CLASSIFICATION
    // =========================

    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @Enumerated(EnumType.STRING)
    private TaxClass taxClass;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;


    // =========================
    // VARIANTS (SKU SYSTEM)
    // =========================

    @Column(name = "has_variants", nullable = false)
    private boolean hasVariants = false;

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<ProductVariant> variants;


    // =========================
    // IMAGES
    // =========================

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<ProductImage> images;

    // fallback image
    private String imagePath;


    // =========================
    // SEARCH
    // =========================

    @ElementCollection
    private List<String> keywords;


    // =========================
    // MARKETING
    // =========================

    private boolean isFeatured;
    private boolean isNew;
    private boolean isBestSeller;


    // =========================
    // RATING
    // =========================

    private Double averageRating = 0.0;
    private int ratingCount = 0;


    // =========================
    // AUDIT
    // =========================

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    // =========================
    // LIFECYCLE HOOKS
    // =========================

    @PrePersist
    public void onCreate() {

        if (productId == null) {
            productId = UUID.randomUUID();
        }

        // ❗ fallback فقط
        if (slug == null && name != null) {
            slug = name.toLowerCase().replace(" ", "-");
        }

        if (status == null) {
            status = ProductStatus.ACTIVE;
        }

        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


    // =========================
    // BUSINESS LOGIC
    // =========================

    /**
     * يرجع أقل ثمن (variant أو product)
     */
    @Transient
    public BigDecimal getEffectivePrice() {
        if (hasVariants && variants != null && !variants.isEmpty()) {
            return variants.stream()
                    .map(ProductVariant::getPrice)
                    .min(BigDecimal::compareTo)
                    .orElse(price);
        }
        return price;
    }

    /**
     * مجموع الكمية
     */
    @Transient
    public Integer getTotalQuantity() {
        if (hasVariants && variants != null) {
            return variants.stream()
                    .mapToInt(ProductVariant::getQuantity)
                    .sum();
        }
        return quantity;
    }
}