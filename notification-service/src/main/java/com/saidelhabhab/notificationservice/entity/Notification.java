package com.saidelhabhab.notificationservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String eventType;

    private UUID customerId;

    private String title;

    private String message;

    private Boolean readFlag;

    private LocalDateTime createdAt;
}