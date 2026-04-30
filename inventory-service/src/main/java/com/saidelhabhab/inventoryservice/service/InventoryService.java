package com.saidelhabhab.inventoryservice.service;

import com.saidelhabhab.inventoryservice.dto.InventoryRequestDTO;
import com.saidelhabhab.inventoryservice.dto.InventoryResponseDTO;
import com.saidelhabhab.inventoryservice.entity.Inventory;
import com.saidelhabhab.inventoryservice.enums.InventoryStatus;
import com.saidelhabhab.inventoryservice.mapper.InventoryMapper;
import com.saidelhabhab.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository repository;
    private final InventoryMapper mapper;

    // ✅ CREATE / ADD STOCK
    @CacheEvict(value = "inventory", allEntries = true)
    @Transactional
    public InventoryResponseDTO addStock(InventoryRequestDTO dto) {

        Inventory inventory = repository
                .findByProductIdAndVariantId(dto.getProductId(), dto.getVariantId())
                .orElse(new Inventory());

        inventory.setProductId(dto.getProductId());
        inventory.setVariantId(dto.getVariantId());

        int newQty = (inventory.getQuantity() == null ? 0 : inventory.getQuantity()) + dto.getQuantity();

        inventory.setQuantity(newQty);
        inventory.setReservedQuantity(0);
        inventory.setStatus(InventoryStatus.AVAILABLE);
        inventory.setUpdatedAt(LocalDateTime.now());

        if (inventory.getCreatedAt() == null) {
            inventory.setCreatedAt(LocalDateTime.now());
        }

        return mapper.toDto(repository.save(inventory));
    }

    // 🔥 RESERVE STOCK (order)
    @Transactional
    public void reserveStock(UUID productId, Long variantId, int quantity) {

        Inventory inv = repository
                .findByProductIdAndVariantId(productId, variantId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        if (inv.getQuantity() - inv.getReservedQuantity() < quantity) {
            throw new RuntimeException("Not enough available stock");
        }

        inv.setReservedQuantity(inv.getReservedQuantity() + quantity);
        inv.setStatus(InventoryStatus.RESERVED);
    }

    // 🔥 CONFIRM ORDER
    @Transactional
    public void confirmStock(UUID productId, Long variantId, int quantity) {

        Inventory inv = repository
                .findByProductIdAndVariantId(productId, variantId)
                .orElseThrow();

        inv.setQuantity(inv.getQuantity() - quantity);
        inv.setReservedQuantity(inv.getReservedQuantity() - quantity);

        if (inv.getQuantity() == 0) {
            inv.setStatus(InventoryStatus.OUT_OF_STOCK);
        }
    }

    // ❌ CANCEL ORDER
    @Transactional
    public void releaseStock(UUID productId, Long variantId, int quantity) {

        Inventory inv = repository
                .findByProductIdAndVariantId(productId, variantId)
                .orElseThrow();

        inv.setReservedQuantity(inv.getReservedQuantity() - quantity);
    }

    // ✅ GET
    @Cacheable(value = "inventory", key = "#productId")
    public InventoryResponseDTO get(UUID productId) {

        Inventory inv = repository
                .findByProductIdAndVariantIdIsNull(productId)
                .orElseThrow();

        return mapper.toDto(inv);
    }
}