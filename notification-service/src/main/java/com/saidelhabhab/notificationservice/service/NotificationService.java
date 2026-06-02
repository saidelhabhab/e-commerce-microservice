package com.saidelhabhab.notificationservice.service;

import com.saidelhabhab.commonevents.NotificationEvent;
import com.saidelhabhab.notificationservice.dto.NotificationDTO;
import com.saidelhabhab.notificationservice.entity.Notification;
import com.saidelhabhab.notificationservice.mapper.NotificationMapper;
import com.saidelhabhab.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    private final NotificationMapper mapper;

    public List<NotificationDTO> getByCustomer(UUID customerId){

        return mapper.toDtoList(
                repository.findByCustomerId(customerId)
        );
    }

    public void create(NotificationEvent event) {

        Notification notification = Notification.builder()
                .customerId(event.getCustomerId())
                .title(event.getTitle())
                .message(event.getMessage())
                .readFlag(false)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(notification);
    }

}