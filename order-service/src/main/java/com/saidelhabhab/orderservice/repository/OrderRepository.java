package com.saidelhabhab.orderservice.repository;

import com.saidelhabhab.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderId(UUID orderId);

    boolean existsByOrderId(UUID orderId);

    @Query("SELECT o FROM Order o JOIN FETCH o.items")
    List<Order> findAllWithItems();
}