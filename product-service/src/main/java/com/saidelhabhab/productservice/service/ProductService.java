package com.saidelhabhab.productservice.service;

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
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    // CREATE
    @CacheEvict(value = "products", allEntries = true)
    @Transactional
    public ProductResponseDTO create(ProductRequestDTO dto) {

        if (dto.getBarcode() != null && repository.existsByBarcode(dto.getBarcode())) {
            throw new RuntimeException("Barcode already exists");
        }

        Product product = mapper.toEntity(dto);

        product.setProductId(UUID.randomUUID());

        // ✅ slug unique
        product.setSlug(
                dto.getName().toLowerCase().replace(" ", "-") + "-" +
                        UUID.randomUUID().toString().substring(0,5)
        );

        // ✅ variants
        if (dto.getVariants() != null && !dto.getVariants().isEmpty()) {

            product.setHasVariants(true);
            product.setPrice(null); // ✅ مهم

            List<ProductVariant> variants = mapper.toVariantEntityList(dto.getVariants());

            variants.forEach(v -> {
                v.setSku("SKU-" + UUID.randomUUID().toString().substring(0, 8)); // ✅ SKU
                v.setProduct(product);
            });

            product.setVariants(variants);

        } else {
            product.setHasVariants(false);
        }

        return mapper.toDto(repository.save(product));
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
    @CacheEvict(value = "product", key = "#productId")
    @Transactional
    public ProductResponseDTO update(UUID productId, ProductRequestDTO dto) {

        Product product = repository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        mapper.update(dto, product);

        // clear old variants
        if (product.getVariants() != null) {
            product.getVariants().clear();
        }

        if (!product.getName().equals(dto.getName())) {
            product.setSlug(
                    dto.getName().toLowerCase().replace(" ", "-") + "-" +
                            UUID.randomUUID().toString().substring(0,5)
            );
        }

        if (dto.getBarcode() != null &&
                !dto.getBarcode().equals(product.getBarcode()) &&
                repository.existsByBarcode(dto.getBarcode())) {

            throw new RuntimeException("Barcode already exists");
        }

        // new variants
        if (dto.getVariants() != null && !dto.getVariants().isEmpty()) {

            product.setHasVariants(true);

            List<ProductVariant> variants = mapper.toVariantEntityList(dto.getVariants());

            variants.forEach(v -> {
                v.setSku("SKU-" + UUID.randomUUID().toString().substring(0, 8)); // ✅ هنا
                v.setProduct(product);
            });

            product.setPrice(null);


            product.setVariants(variants);

        } else {
            product.setHasVariants(false);
        }

        return mapper.toDto(repository.save(product));
    }

    // DELETE
    @CacheEvict(value = "product", key = "#productId")
    @Transactional
    public void delete(UUID productId) {

        Product product = repository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        repository.delete(product);
    }


    // =========================
    // 🔥 DECREASE STOCK
    // =========================
    @Transactional
    public void decreaseStock(UUID productId, Long variantId, int quantity) {

        // ✅ VALIDATION
        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        Product product = repository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // =========================
        // 🟢 NO VARIANT
        // =========================
        if (variantId == null) {

            Integer stock = product.getQuantity();

            if (stock == null || stock < quantity) {
                throw new RuntimeException("Not enough stock for product");
            }

            product.setQuantity(stock - quantity);
        }

        // =========================
        // 🟡 WITH VARIANT
        // =========================
        else {

            if (product.getVariants() == null || product.getVariants().isEmpty()) {
                throw new RuntimeException("Product has no variants");
            }

            ProductVariant variant = product.getVariants().stream()
                    .filter(v -> Objects.equals(v.getId(), variantId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Variant not found"));

            Integer stock = variant.getQuantity();

            if (stock == null || stock < quantity) {
                throw new RuntimeException("Not enough stock for variant");
            }

            variant.setQuantity(stock - quantity);
        }

        repository.save(product);
    }
}