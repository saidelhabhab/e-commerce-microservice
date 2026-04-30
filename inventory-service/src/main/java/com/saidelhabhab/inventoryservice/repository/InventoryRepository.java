package com.saidelhabhab.inventoryservice.repository;

import com.saidelhabhab.inventoryservice.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    Optional<Inventory> findByProductIdAndVariantId(UUID productId, Long variantId);

    Optional<Inventory> findByProductIdAndVariantIdIsNull(UUID productId);
}