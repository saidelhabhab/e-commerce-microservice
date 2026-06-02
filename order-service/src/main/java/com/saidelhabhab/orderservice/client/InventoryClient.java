package com.saidelhabhab.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name="inventory-service")
public interface InventoryClient {

    @PostMapping("/api/inventory/reserve")
    void reserveStock(

            @RequestParam UUID productId,

            @RequestParam(required=false)
            Long variantId,

            @RequestParam Integer quantity
    );

    @PostMapping("/api/inventory/confirm")
    void confirmStock(

            @RequestParam UUID productId,

            @RequestParam(required=false)
            Long variantId,

            @RequestParam Integer quantity
    );

    @PostMapping("/api/inventory/release")
    void releaseStock(

            @RequestParam UUID productId,

            @RequestParam(required=false)
            Long variantId,

            @RequestParam Integer quantity
    );

}