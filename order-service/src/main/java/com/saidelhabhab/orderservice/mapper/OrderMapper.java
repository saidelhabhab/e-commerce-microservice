package com.saidelhabhab.orderservice.mapper;

import com.saidelhabhab.orderservice.dto.OrderItemDTO;
import com.saidelhabhab.orderservice.dto.OrderRequestDTO;
import com.saidelhabhab.orderservice.dto.OrderResponseDTO;
import com.saidelhabhab.orderservice.entity.Order;
import com.saidelhabhab.orderservice.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    Order toEntity(OrderRequestDTO dto);

    @Mapping(source = "items", target = "items")
    OrderResponseDTO toDto(Order order);

    List<OrderResponseDTO> toDtoList(List<Order> orders);

    OrderItemDTO toItemDto(OrderItem entity);
    List<OrderItemDTO> toItemDtoList(List<OrderItem> items);

    OrderItem toItemEntity(OrderItemDTO dto);
}