package com.saidelhabhab.orderservice.controller;

import com.saidelhabhab.orderservice.dto.OrderRequestDTO;
import com.saidelhabhab.orderservice.dto.OrderResponseDTO;
import com.saidelhabhab.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping
    public OrderResponseDTO create(@RequestBody OrderRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<OrderResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{orderId}")
    public OrderResponseDTO getById(@PathVariable UUID orderId) {
        return service.getById(orderId);
    }
}
