package com.saidelhabhab.orderservice.dto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderResponseDTO {
    private UUID orderId;
    private UUID customerId;
    private BigDecimal totalAmount;
    private String status;
    private List<OrderItemDTO> items;
    private String createdAt;
}

