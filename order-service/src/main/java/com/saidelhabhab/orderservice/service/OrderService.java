package com.saidelhabhab.orderservice.service;

import com.saidelhabhab.orderservice.client.ProductClient;
import com.saidelhabhab.orderservice.dto.OrderRequestDTO;
import com.saidelhabhab.orderservice.dto.OrderResponseDTO;
import com.saidelhabhab.orderservice.entity.Order;
import com.saidelhabhab.orderservice.entity.OrderItem;
import com.saidelhabhab.orderservice.mapper.OrderMapper;
import com.saidelhabhab.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;

    private final ProductClient productClient;

    // =========================
    // CREATE ORDER (SAFE)
    // =========================

    @Transactional
    public OrderResponseDTO create(OrderRequestDTO dto) {

        // ✅ 1. VALIDATION
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new RuntimeException("Order must contain at least one item");
        }

        Order order = mapper.toEntity(dto);

        // ✅ 2. BUILD ITEMS
        List<OrderItem> items = dto.getItems().stream()
                .map(i -> {

                    // ❌ quantity check
                    if (i.getQuantity() <= 0) {
                        throw new RuntimeException("Invalid quantity");
                    }

                    OrderItem item = new OrderItem();
                    item.setProductId(i.getProductId());
                    item.setVariantId(i.getVariantId());
                    item.setQuantity(i.getQuantity());

                    var product = productClient.getProductById(i.getProductId());

                    if (product == null) {
                        throw new RuntimeException("Product not found");
                    }

                    BigDecimal price;

                    // =========================
                    // 🟢 NO VARIANT
                    // =========================
                    if (i.getVariantId() == null) {

                        if (product.getQuantity() < i.getQuantity()) {
                            throw new RuntimeException("Not enough stock for product");
                        }

                        price = product.getPrice();

                        if (price == null) {
                            throw new RuntimeException("Product price is null");
                        }
                    }

                    // =========================
                    // 🟡 WITH VARIANT
                    // =========================
                    else {

                        if (product.getVariants() == null || product.getVariants().isEmpty()) {
                            throw new RuntimeException("Product has no variants");
                        }

                        var variant = product.getVariants().stream()
                                .filter(v -> Objects.equals(v.getId(), i.getVariantId()))
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("Variant not found"));

                        if (variant.getQuantity() < i.getQuantity()) {
                            throw new RuntimeException("Not enough stock for variant");
                        }

                        price = variant.getPrice();

                        if (price == null) {
                            throw new RuntimeException("Variant price is null");
                        }
                    }

                    // 🔥 3. DECREASE STOCK (IMPORTANT)
                    productClient.decreaseStock(
                            i.getProductId(),
                            i.getVariantId(),
                            i.getQuantity()
                    );

                    // ✅ SET DATA
                    item.setPrice(price);
                    item.setTotal(price.multiply(BigDecimal.valueOf(i.getQuantity())));
                    item.setOrder(order);

                    return item;
                })
                .toList();

        // ✅ 4. TOTAL
        BigDecimal total = items.stream()
                .map(OrderItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setItems(items);
        order.setTotalAmount(total);

        // ✅ 5. SAVE
        Order saved = repository.save(order);

        return mapper.toDto(saved);
    }

    // =========================
    // GET ALL
    // =========================
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAll() {
        return mapper.toDtoList(repository.findAll());
    }

    // =========================
    // GET BY ID (SAFE NULL HANDLING)
    // =========================
    @Transactional(readOnly = true)
    public OrderResponseDTO getById(UUID orderId) {

        Order order = repository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return mapper.toDto(order);
    }

    // =========================
    // DELETE ORDER (IMPORTANT)
    // =========================
    @Transactional
    public void delete(UUID orderId) {

        Order order = repository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        repository.delete(order);
    }
}