package com.saidelhabhab.commonevents;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryEvent implements Serializable {

    private EventType eventType;

    private UUID orderId;

    private UUID productId;

    private Integer quantity;

    private String message;
}