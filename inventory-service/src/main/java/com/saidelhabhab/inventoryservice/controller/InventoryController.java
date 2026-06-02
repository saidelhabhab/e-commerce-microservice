package com.saidelhabhab.inventoryservice.controller;

import com.saidelhabhab.inventoryservice.dto.InventoryRequestDTO;
import com.saidelhabhab.inventoryservice.dto.InventoryResponseDTO;
import com.saidelhabhab.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService service;

    // =========================
    // CREATE INVENTORY ROW
    // =========================
    @PostMapping("/create")
    public InventoryResponseDTO createInventory(
            @RequestParam UUID productId,
            @RequestParam(required = false) Long variantId
    ) {
        return service.createInventory(productId, variantId);
    }

    // =========================
    // ADD STOCK (IMPORTANT)
    // =========================
    @PostMapping("/add")
    public InventoryResponseDTO addStock(@RequestBody InventoryRequestDTO dto) {
        return service.addStock(dto);
    }

    // =========================
    // RESERVE STOCK (ORDER)
    // =========================
    @PostMapping("/reserve")
    public void reserve(
            @RequestParam UUID productId,
            @RequestParam(required = false) Long variantId,
            @RequestParam Integer quantity
    ) {
        service.reserveStock(productId, variantId, quantity);
    }

    // =========================
    // CONFIRM ORDER
    // =========================
    @PostMapping("/confirm")
    public void confirm(
            @RequestParam UUID productId,
            @RequestParam(required = false) Long variantId,
            @RequestParam Integer quantity
    ) {
        service.confirmStock(productId, variantId, quantity);
    }

    // =========================
    // RELEASE STOCK (ROLLBACK)
    // =========================
    @PostMapping("/release")
    public void release(
            @RequestParam UUID productId,
            @RequestParam(required = false) Long variantId,
            @RequestParam Integer quantity
    ) {
        service.releaseStock(productId, variantId, quantity);
    }

    // =========================
    // GET ONE
    // =========================
    @GetMapping
    public InventoryResponseDTO getInventory(
            @RequestParam UUID productId,
            @RequestParam(required = false) Long variantId
    ) {
        return service.getInventory(productId, variantId);
    }

    // =========================
    // GET ALL
    // =========================
    @GetMapping("/all")
    public List<InventoryResponseDTO> getAll() {
        return service.getAll();
    }

    // =========================
    // GET BY PRODUCT
    // =========================
    @GetMapping("/product/{productId}")
    public List<InventoryResponseDTO> getByProductId(@PathVariable UUID productId) {
        return service.getByProductId(productId);
    }
}