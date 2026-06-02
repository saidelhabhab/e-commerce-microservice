package com.saidelhabhab.notificationservice.mapper;

import com.saidelhabhab.notificationservice.dto.NotificationDTO;
import com.saidelhabhab.notificationservice.entity.Notification;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationDTO toDto(Notification entity);

    Notification toEntity(NotificationDTO dto);

    List<NotificationDTO> toDtoList(List<Notification> list);
}