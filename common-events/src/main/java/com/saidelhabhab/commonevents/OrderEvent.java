package com.saidelhabhab.commonevents;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEvent implements Serializable {

    private EventType eventType;

    private UUID orderId;

    private UUID customerId;

    private String email;

    private BigDecimal totalAmount;

    private String message;

    private LocalDateTime createdAt;
}