package com.saidelhabhab.productservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String color;        // watch, phone

    private String ram;          // PC, Tablet (ex: 16GB)

    private String storage;      // 256GB / 512GB

    private BigDecimal price;

    private Integer  quantity;

    @Column(unique = true, nullable = false)
    private String sku; // unique code لكل variant

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
