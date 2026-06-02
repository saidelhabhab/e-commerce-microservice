package com.saidelhabhab.inventoryservice.mapper;

import com.saidelhabhab.inventoryservice.dto.InventoryRequestDTO;
import com.saidelhabhab.inventoryservice.dto.InventoryResponseDTO;
import com.saidelhabhab.inventoryservice.entity.Inventory;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target = "effectiveStock", expression = "java(inventory.getEffectiveStock())")
    InventoryResponseDTO toDto(Inventory inventory);

    Inventory toEntity(InventoryRequestDTO dto);
}