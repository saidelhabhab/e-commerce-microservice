package com.saidelhabhab.inventoryservice.controller;

import com.saidelhabhab.inventoryservice.dto.InventoryRequestDTO;
import com.saidelhabhab.inventoryservice.dto.InventoryResponseDTO;
import com.saidelhabhab.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService service;

    // ✅ ADD STOCK
    @PostMapping("/add")
    public InventoryResponseDTO addStock(@RequestBody InventoryRequestDTO dto) {
        return service.addStock(dto);
    }

    // 🔥 RESERVE (order created)
    @PostMapping("/reserve")
    public String reserve(
            @RequestParam UUID productId,
            @RequestParam(required = false) Long variantId,
            @RequestParam int quantity
    ) {
        service.reserveStock(productId, variantId, quantity);
        return "Stock reserved";
    }

    // 🔥 CONFIRM (order paid)
    @PostMapping("/confirm")
    public String confirm(
            @RequestParam UUID productId,
            @RequestParam(required = false) Long variantId,
            @RequestParam int quantity
    ) {
        service.confirmStock(productId, variantId, quantity);
        return "Stock confirmed";
    }

    // ❌ CANCEL (order cancelled)
    @PostMapping("/release")
    public String release(
            @RequestParam UUID productId,
            @RequestParam(required = false) Long variantId,
            @RequestParam int quantity
    ) {
        service.releaseStock(productId, variantId, quantity);
        return "Stock released";
    }

    // ✅ GET INVENTORY
    @GetMapping("/{productId}")
    public InventoryResponseDTO get(@PathVariable UUID productId) {
        return service.get(productId);
    }
}