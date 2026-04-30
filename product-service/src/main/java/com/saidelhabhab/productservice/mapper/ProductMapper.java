package com.saidelhabhab.productservice.mapper;

import com.saidelhabhab.productservice.dto.ProductRequestDTO;
import com.saidelhabhab.productservice.dto.ProductResponseDTO;
import com.saidelhabhab.productservice.dto.ProductVariantDTO;
import com.saidelhabhab.productservice.entity.Product;
import com.saidelhabhab.productservice.entity.ProductImage;
import com.saidelhabhab.productservice.entity.ProductVariant;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // =========================
    // PRODUCT
    // =========================

    // DTO → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "imagePath", ignore = true)
    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "images", ignore = true)
    Product toEntity(ProductRequestDTO dto);


    // =========================
    // ENTITY → DTO (MAIN)
    // =========================

    @Mapping(source = "imagePath", target = "imageUrl")
    @Mapping(source = "variants", target = "variants")
    @Mapping(target = "effectivePrice", expression = "java(entity.getEffectivePrice())")
    @Mapping(target = "totalQuantity", expression = "java(entity.getTotalQuantity())")
    @Mapping(target = "status", expression = "java(entity.getStatus() != null ? entity.getStatus().name() : null)")
    @Mapping(target = "images", expression = "java(mapImages(entity.getImages()))")
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)")
    @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)")
    ProductResponseDTO toDto(Product entity);


    List<ProductResponseDTO> toDtoList(List<Product> list);


    // =========================
    // UPDATE
    // =========================

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "imagePath", ignore = true)
    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "images", ignore = true)
    void update(ProductRequestDTO dto, @MappingTarget Product entity);


    // =========================
    // VARIANT
    // =========================

    @Mapping(target = "sku", ignore = true)
    @Mapping(target = "product", ignore = true)
    ProductVariant toVariantEntity(ProductVariantDTO dto);

    List<ProductVariant> toVariantEntityList(List<ProductVariantDTO> list);

    ProductVariantDTO toVariantDTO(ProductVariant entity);

    List<ProductVariantDTO> toVariantDTOList(List<ProductVariant> list);


    // =========================
    // CUSTOM METHODS
    // =========================

    /**
     * تحويل images → List<String>
     */
    default List<String> mapImages(List<ProductImage> images) {
        if (images == null) return List.of();

        return images.stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());
    }
}