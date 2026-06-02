package com.saidelhabhab.inventoryservice.entity;

import com.saidelhabhab.inventoryservice.enums.InventoryStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "inventory",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "product_id",
                                "variant_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="product_id", nullable = false)
    private UUID productId;

    @Column(name="variant_id")
    private Long variantId;

    @Column(nullable = false)
    private Integer quantityAvailable;

    @Column(nullable = false)
    private Integer quantityReserved;

    private Integer reorderPoint;

    private Integer reorderQuantity;

    @Enumerated(EnumType.STRING)
    private InventoryStatus status;

    @Version
    private Long version;

    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void updateStock() {

        if(quantityAvailable == null)
            quantityAvailable = 0;

        if(quantityReserved == null)
            quantityReserved = 0;

        if(reorderPoint == null)
            reorderPoint = 5;

        if(reorderQuantity == null)
            reorderQuantity = 20;

        int effective = quantityAvailable - quantityReserved;

        if(effective <= 0){
            status = InventoryStatus.OUT_OF_STOCK;
        }

        else if(
                effective <= reorderPoint
        ){
            status = InventoryStatus.LOW_STOCK;
        }

        else{
            status = InventoryStatus.AVAILABLE;
        }

        if(createdAt == null){
            createdAt = LocalDateTime.now();
        }

    }

    public Integer getEffectiveStock(){

        return Math.max(
                0,
                quantityAvailable
                        - quantityReserved
        );

    }
}