package com.saidelhabhab.productservice.repository;

import com.saidelhabhab.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductId(UUID productId);

    boolean existsByBarcode(String barcode);
}