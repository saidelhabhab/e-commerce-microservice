package com.saidelhabhab.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @PostMapping("/api/inventory/reserve")
    String reserve(
            @RequestParam UUID productId,
            @RequestParam(required = false) Long variantId,
            @RequestParam int quantity
    );

    @PostMapping("/api/inventory/confirm")
    String confirm(
            @RequestParam UUID productId,
            @RequestParam(required = false) Long variantId,
            @RequestParam int quantity
    );

    @PostMapping("/api/inventory/release")
    String release(
            @RequestParam UUID productId,
            @RequestParam(required = false) Long variantId,
            @RequestParam int quantity
    );
}