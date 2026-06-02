package com.saidelhabhab.productservice.service;

import com.saidelhabhab.productservice.client.InventoryClient;
import com.saidelhabhab.productservice.dto.ProductRequestDTO;
import com.saidelhabhab.productservice.dto.ProductResponseDTO;
import com.saidelhabhab.productservice.entity.Product;
import com.saidelhabhab.productservice.entity.ProductVariant;
import com.saidelhabhab.productservice.mapper.ProductMapper;
import com.saidelhabhab.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
@CacheEvict(value = "products", allEntries = true)
@Transactional
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;
    private final InventoryClient inventoryClient;


    @Transactional
    public ProductResponseDTO create(ProductRequestDTO dto) {

        if (dto.getBarcode() != null &&
                repository.existsByBarcode(dto.getBarcode())) {

            throw new RuntimeException("Barcode already exists");
        }

        Product product = mapper.toEntity(dto);

        product.setProductId(UUID.randomUUID());

        product.setSlug(
                dto.getName()
                        .toLowerCase()
                        .replace(" ", "-")
                        + "-"
                        + UUID.randomUUID()
                        .toString()
                        .substring(0, 5)
        );

        if (dto.getVariants() != null &&
                !dto.getVariants().isEmpty()) {

            product.setHasVariants(true);

            List<ProductVariant> variants =
                    mapper.toVariantEntityList(dto.getVariants());

            variants.forEach(v -> {

                v.setSku(
                        "SKU-" +
                                UUID.randomUUID()
                                        .toString()
                                        .substring(0, 8)
                );

                v.setProduct(product);
            });

            product.setVariants(variants);

        } else {

            product.setHasVariants(false);
        }

        Product saved = repository.save(product);

        // ==========================
        // CREATE INVENTORY ONLY
        // ==========================

        if (!saved.isHasVariants()) {

            inventoryClient.createInventory(saved.getProductId(), null);

        } else {

            saved.getVariants().forEach(v ->

                    inventoryClient.createInventory(saved.getProductId(), v.getId())

            );
        }

        return mapper.toDto(saved);
    }

    // GET ALL
    @Transactional(readOnly = true)
    @Cacheable(value = "products")
    public List<ProductResponseDTO> getAll() {
        return mapper.toDtoList(repository.findAll());
    }

    // GET BY ID
    @Cacheable(value = "product", key = "#productId")
    @Transactional(readOnly = true)
    public ProductResponseDTO getById(UUID productId) {
        Product product = repository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return mapper.toDto(product);
    }

    // UPDATE
    @Transactional
    @CacheEvict(value = "product", key = "#productId")
    public ProductResponseDTO update(UUID productId, ProductRequestDTO dto) {

        Product product = repository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // =========================
        // SAVE OLD VALUES (IMPORTANT)
        // =========================
        String oldName = product.getName();
        String oldBarcode = product.getBarcode();

        // =========================
        // BARCODE VALIDATION (before overwrite)
        // =========================
        if (dto.getBarcode() != null
                && !Objects.equals(oldBarcode, dto.getBarcode())
                && repository.existsByBarcode(dto.getBarcode())) {

            throw new RuntimeException("Barcode already exists");
        }

        // =========================
        // MAP BASIC FIELDS
        // =========================
        mapper.update(dto, product);

        // =========================
        // SLUG UPDATE IF NAME CHANGED
        // =========================
        if (!Objects.equals(oldName, dto.getName())) {

            product.setSlug(
                    dto.getName().toLowerCase()
                            .replace(" ", "-")
                            + "-"
                            + UUID.randomUUID().toString().substring(0, 5)
            );
        }

        // =========================
        // VARIANTS HANDLING (SAFE VERSION)
        // =========================
        if (dto.getVariants() != null && !dto.getVariants().isEmpty()) {

            product.setHasVariants(true);

            // IMPORTANT: replace only, not destroy blindly logic
            List<ProductVariant> newVariants =
                    mapper.toVariantEntityList(dto.getVariants());

            newVariants.forEach(v -> {
                v.setSku("SKU-" + UUID.randomUUID().toString().substring(0, 8));
                v.setProduct(product);
            });

            product.setPrice(null);
            product.setVariants(newVariants);

        } else {
            product.setHasVariants(false);

            // optional: keep old variants OR clear if business says simple product
            product.getVariants().clear();
        }

        Product saved = repository.save(product);

        return mapper.toDto(saved);
    }

    // DELETE
    @CacheEvict(value = "product", key = "#productId")
    @Transactional
    public void delete(UUID productId) {

        Product product = repository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        repository.delete(product);
    }



}