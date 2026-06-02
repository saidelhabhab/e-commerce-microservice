package com.saidelhabhab.productservice.client;

import com.saidelhabhab.productservice.dto.InventoryResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(
        name = "inventory-service"
)
public interface InventoryClient {


    @PostMapping("/api/inventory/create")
    InventoryResponseDTO createInventory(
            @RequestParam UUID productId,
            @RequestParam(required = false)
            Long variantId
    );

}