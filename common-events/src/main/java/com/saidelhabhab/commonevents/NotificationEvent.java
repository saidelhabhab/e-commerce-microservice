package com.saidelhabhab.commonevents;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEvent implements Serializable {

    private EventType eventType;

    private UUID orderId;

    private UUID customerId;

    private String email;

    private String title;

    private String message;

    private LocalDateTime createdAt;
}