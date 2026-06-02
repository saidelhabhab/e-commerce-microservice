package com.saidelhabhab.inventoryservice.service;

import com.saidelhabhab.inventoryservice.dto.*;
import com.saidelhabhab.inventoryservice.entity.Inventory;
import com.saidelhabhab.inventoryservice.mapper.InventoryMapper;
import com.saidelhabhab.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames="inventory")
public class InventoryService {

    private final InventoryRepository repository;

    private final InventoryMapper mapper;



    @Transactional
    @CacheEvict(allEntries = true)
    public InventoryResponseDTO createInventory(
            UUID productId,
            Long variantId
    ) {

        boolean exists = variantId == null
                ? repository.findByProductIdAndVariantIdIsNull(productId).isPresent()
                : repository.findByProductIdAndVariantId(productId, variantId).isPresent();

        if (exists) {
            return getInventory(productId, variantId);
        }

        Inventory inventory = Inventory.builder()
                .productId(productId)
                .variantId(variantId)
                .quantityAvailable(0)
                .quantityReserved(0)
                .build();

        return mapper.toDto(
                repository.save(inventory)
        );
    }

    @Transactional
    public void reserveStock(UUID productId, Long variantId, Integer qty) {

        Inventory inv = find(productId, variantId);

        int available = inv.getQuantityAvailable() - inv.getQuantityReserved();

        if (available < qty) {
            throw new RuntimeException("Not enough stock");
        }

        inv.setQuantityReserved(inv.getQuantityReserved() + qty);
        repository.save(inv);
    }

    @Transactional
    public void confirmStock(UUID productId, Long variantId, Integer qty) {

        Inventory inv = findOrCreate(productId, variantId);

        int newAvailable = inv.getQuantityAvailable() - qty;
        int newReserved = inv.getQuantityReserved() - qty;

        if (newAvailable < 0) {
            throw new RuntimeException("Stock inconsistent");
        }

        inv.setQuantityAvailable(newAvailable);
        inv.setQuantityReserved(Math.max(0, newReserved));

        repository.save(inv);
    }

    @Transactional
    public void releaseStock(UUID productId, Long variantId, Integer qty) {

        Inventory inv = find(productId, variantId);

        // decrease reserved
        int newReserved = inv.getQuantityReserved() - qty;
        inv.setQuantityReserved(Math.max(0, newReserved));

        // return stock to available
        inv.setQuantityAvailable(inv.getQuantityAvailable() + qty);

        repository.save(inv);
    }


    public InventoryResponseDTO getInventory(UUID productId, Long variantId) {
        return mapper.toDto(findOrCreate(productId, variantId));
    }

    private Inventory findOrCreate(UUID productId, Long variantId) {

        return variantId == null
                ? repository.findByProductIdAndVariantIdIsNull(productId)
                  .orElseGet(() -> {
                      Inventory inv = Inventory.builder()
                              .productId(productId)
                              .variantId(null)
                              .quantityAvailable(0)
                              .quantityReserved(0)
                              .build();
                      return repository.save(inv);
                  })

                : repository.findByProductIdAndVariantId(productId, variantId)
                  .orElseGet(() -> {
                      Inventory inv = Inventory.builder()
                              .productId(productId)
                              .variantId(variantId)
                              .quantityAvailable(0)
                              .quantityReserved(0)
                              .build();
                      return repository.save(inv);
                  });
    }

    private Inventory find(UUID productId, Long variantId) {

        return variantId == null
                ? repository.findByProductIdAndVariantIdIsNull(productId)
                  .orElseThrow(() -> new RuntimeException("Inventory not found"))
                : repository.findByProductIdAndVariantId(productId, variantId)
                  .orElseThrow(() -> new RuntimeException("Inventory not found"));
    }

    @Cacheable(key = "'all'")
    @Transactional(readOnly = true)
    public List<InventoryResponseDTO> getAll() {

        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .filter(i -> i.getProductId() != null)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "#productId")
    public List<InventoryResponseDTO> getByProductId(UUID productId) {

        return repository.findAll()
                .stream()
                .filter(i -> i.getProductId().equals(productId))
                .map(mapper::toDto)
                .toList();
    }



    @Transactional
    @CacheEvict(allEntries = true)
    public InventoryResponseDTO addStock(InventoryRequestDTO dto) {

        Inventory inv = findOrCreate(dto.getProductId(), dto.getVariantId());

        inv.setQuantityAvailable(inv.getQuantityAvailable() + dto.getQuantity());

        return mapper.toDto(repository.save(inv));
    }

}