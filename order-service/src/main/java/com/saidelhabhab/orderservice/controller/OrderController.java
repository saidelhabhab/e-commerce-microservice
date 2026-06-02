package com.saidelhabhab.orderservice.controller;

import com.saidelhabhab.orderservice.dto.ApiResponse;
import com.saidelhabhab.orderservice.dto.OrderRequestDTO;
import com.saidelhabhab.orderservice.dto.OrderResponseDTO;
import com.saidelhabhab.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDTO>> create(@RequestBody OrderRequestDTO dto) {
        OrderResponseDTO res = service.create(dto);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Order created successfully", res)
        );
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<Void>> confirm(@PathVariable UUID id) {
        service.confirmOrder(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Order confirmed", null)
        );
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable UUID id) {
        service.cancelOrder(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Order cancelled", null)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getAllOrders() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Orders fetched", service.getAllOrders())
        );
    }

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getAllOrdersPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Paged orders fetched", service.getAllOrders(page, size))
        );
    }

    @PostMapping("/{id}/ship")
    public ResponseEntity<ApiResponse<Void>> ship(@PathVariable UUID id) {
        service.shipOrder(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Order shipped", null)
        );
    }

    @PostMapping("/{id}/deliver")
    public ResponseEntity<ApiResponse<Void>> deliver(@PathVariable UUID id) {
        service.deliverOrder(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Order delivered", null)
        );
    }
}