package com.saidelhabhab.orderservice.service;

import com.saidelhabhab.commonevents.EventType;
import com.saidelhabhab.commonevents.NotificationEvent;
import com.saidelhabhab.orderservice.client.InventoryClient;
import com.saidelhabhab.orderservice.client.ProductClient;
import com.saidelhabhab.orderservice.dto.OrderItemDTO;
import com.saidelhabhab.orderservice.dto.OrderRequestDTO;
import com.saidelhabhab.orderservice.dto.OrderResponseDTO;
import com.saidelhabhab.orderservice.dto.ProductDTO;
import com.saidelhabhab.orderservice.entity.Order;
import com.saidelhabhab.orderservice.entity.OrderItem;
import com.saidelhabhab.orderservice.enums.DeliveryStatus;
import com.saidelhabhab.orderservice.enums.OrderStatus;
import com.saidelhabhab.orderservice.kafka.OrderEventPublisher;
import com.saidelhabhab.orderservice.mapper.OrderMapper;
import com.saidelhabhab.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;
    private final OrderMapper mapper;
    private final OrderEventPublisher publisher;

    @Transactional
    public OrderResponseDTO create(OrderRequestDTO dto) {

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new RuntimeException("items required");
        }

        Order order = new Order();
        order.setCustomerId(dto.getCustomerId());

        List<OrderItem> items = new ArrayList<>();

        try {

            for (OrderItemDTO i : dto.getItems()) {

                ProductDTO product = productClient.getProductById(i.getProductId());

                if (product == null) {
                    throw new RuntimeException("product not found");
                }

                BigDecimal price;

                if (!product.isHasVariants()) {
                    i.setVariantId(null);
                    price = product.getPrice();
                } else {
                    if (i.getVariantId() == null) {
                        throw new RuntimeException("variant required");
                    }

                    price = product.getVariants()
                            .stream()
                            .filter(v -> Objects.equals(v.getId(), i.getVariantId()))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("variant not found"))
                            .getPrice();
                }

                // 🔥 RESERVE STOCK FIRST
                inventoryClient.reserveStock(
                        i.getProductId(),
                        i.getVariantId(),
                        i.getQuantity()
                );

                OrderItem item = new OrderItem();
                item.setProductId(i.getProductId());
                item.setVariantId(i.getVariantId());
                item.setQuantity(i.getQuantity());
                item.setPrice(price);
                item.setTotal(price.multiply(BigDecimal.valueOf(i.getQuantity())));
                item.setOrder(order);

                items.add(item);
            }

            order.setItems(items);

            order.setTotalAmount(
                    items.stream()
                            .map(OrderItem::getTotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
            );

            order.setStatus(OrderStatus.RESERVED);

            Order saved = repository.save(order);

            publisher.sendNotification(

                    NotificationEvent.builder()
                            .eventType(EventType.ORDER_CREATED)
                            .orderId(saved.getOrderId())
                            .customerId(saved.getCustomerId())
                            .email("saidmullerlover@gmail.com")
                            .title("Commande créée")
                            .message("Votre commande a été créée avec succès")
                            .build()
            );

            OrderResponseDTO dtos = mapper.toDto(saved);
            dto.setItems(
                    saved.getItems()
                            .stream()
                            .map(mapper::toItemDto)
                            .toList()
            );
            return dtos;

        } catch (Exception e) {

            // 🔥 ROLLBACK STOCK if anything fails
            dto.getItems().forEach(i -> {
                try {
                    inventoryClient.releaseStock(
                            i.getProductId(),
                            i.getVariantId(),
                            i.getQuantity()
                    );
                } catch (Exception ignored) {}
            });

            throw new RuntimeException("Order creation failed: " + e.getMessage());
        }
    }

    @Transactional
    public void confirmOrder(UUID orderId) {

        Order order = repository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("order not found"));

        order.getItems().forEach(i ->
                inventoryClient.confirmStock(
                        i.getProductId(),
                        i.getVariantId(),
                        i.getQuantity()
                )
        );

        order.setStatus(OrderStatus.PAID);
        publisher.sendNotification(

                NotificationEvent.builder()
                        .eventType(EventType.ORDER_PAID)
                        .orderId(order.getOrderId())
                        .customerId(order.getCustomerId())
                        .email("saidmullerlover@gmail.com")
                        .title("Paiement confirmé")
                        .message("Votre paiement a été confirmé")
                        .build()
        );
    }

    @Transactional
    public void cancelOrder(UUID orderId) {

        Order order = repository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            return;
        }

        order.getItems().forEach(i ->
                inventoryClient.releaseStock(
                        i.getProductId(),
                        i.getVariantId(),
                        i.getQuantity()
                )
        );

        order.setStatus(OrderStatus.CANCELLED);

        publisher.sendNotification(

                NotificationEvent.builder()
                        .eventType(EventType.ORDER_CANCELLED)
                        .orderId(order.getOrderId())
                        .customerId(order.getCustomerId())
                        .email("saidmullerlover@gmail.com")
                        .title("Commande annulée")
                        .message("Votre commande a été annulée")
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrders() {

        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }


    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrders(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return repository.findAll(pageable)
                .stream()
                .map(mapper::toDto)
                .toList();
    }


    @Transactional
    public void shipOrder(UUID orderId) {

        Order order = repository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot ship cancelled order");
        }

        requireStatus(order, OrderStatus.PAID,
                "Order must be PAID before shipping");

        order.setStatus(OrderStatus.SHIPPED);
        // delivery state
        order.setDeliveryStatus(DeliveryStatus.IN_TRANSIT);

        publisher.sendNotification(

                NotificationEvent.builder()
                        .eventType(EventType.ORDER_SHIPPED)
                        .orderId(order.getOrderId())
                        .customerId(order.getCustomerId())
                        .email("saidmullerlover@gmail.com")
                        .title("Commande expédiée")
                        .message("Votre commande est en cours de livraison")
                        .build()
        );
    }

    @Transactional
    public void deliverOrder(UUID orderId) {

        Order order = repository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot deliver cancelled order");
        }

        requireStatus(order, OrderStatus.SHIPPED,
                "Order must be SHIPPED before delivery");

        if (order.getDeliveryStatus() != DeliveryStatus.IN_TRANSIT) {
            throw new RuntimeException("Order must be IN_TRANSIT before delivery");
        }

        order.setStatus(OrderStatus.DELIVERED);
        order.setDeliveryStatus(DeliveryStatus.DELIVERED);

        publisher.sendNotification(

                NotificationEvent.builder()
                        .eventType(EventType.ORDER_DELIVERED)
                        .orderId(order.getOrderId())
                        .customerId(order.getCustomerId())
                        .email("saidmullerlover@gmail.com")
                        .title("Commande livrée")
                        .message("Votre commande a été livrée")
                        .build()
        );
    }

    private void requireStatus(Order order, OrderStatus expected, String message) {
        if (order.getStatus() != expected) {
            throw new RuntimeException(message);
        }
    }


}