package com.saidelhabhab.orderservice.mapper;

import com.saidelhabhab.orderservice.dto.*;
import com.saidelhabhab.orderservice.entity.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    // =========================
    // ORDER
    // =========================
    Order toEntity(OrderRequestDTO dto);

    // =====================
    // ORDER -> DTO
    // =====================
    @Mapping(source = "orderId", target = "orderId")
    @Mapping(source = "customerId", target = "customerId")
    @Mapping(source = "totalAmount", target = "totalAmount")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "items", target = "items")
    OrderResponseDTO toDto(Order order);

    List<OrderResponseDTO> toDtoList(List<Order> orders);

    // =========================
    // ITEMS
    // =========================
    OrderItem toItemEntity(OrderItemDTO dto);

    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "variantId", target = "variantId")
    @Mapping(source = "quantity", target = "quantity")
    OrderItemDTO toItemDto(OrderItem entity);

    List<OrderItemDTO> toItemDtoList(List<OrderItem> items);
}