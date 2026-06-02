package com.saidelhabhab.notificationservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class NotificationDTO {

    private UUID id;

    private UUID customerId;

    private String title;

    private String message;

    private Boolean readFlag;

    private LocalDateTime createdAt;
}