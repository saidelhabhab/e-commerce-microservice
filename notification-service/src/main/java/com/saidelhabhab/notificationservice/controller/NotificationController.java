package com.saidelhabhab.notificationservice.controller;

import com.saidelhabhab.notificationservice.dto.NotificationDTO;
import com.saidelhabhab.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @GetMapping("/{customerId}")
    public ResponseEntity<List<NotificationDTO>> getNotifications(
            @PathVariable UUID customerId
    ){
        return ResponseEntity.ok(
                service.getByCustomer(customerId)
        );
    }
}