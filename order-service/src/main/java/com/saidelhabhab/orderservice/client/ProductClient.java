package com.saidelhabhab.orderservice.client;

import com.saidelhabhab.orderservice.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClient {

    @GetMapping("/api/products/{productId}")
    ProductDTO getProductById(@PathVariable UUID productId);

    @PutMapping("/api/products/{productId}/decrease-stock")
    void decreaseStock(
            @PathVariable UUID productId,
            @RequestParam(required = false) Long variantId,
            @RequestParam int quantity
    );
}