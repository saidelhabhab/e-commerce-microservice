package com.saidelhabhab.inventoryservice.mapper;


import com.saidelhabhab.inventoryservice.dto.InventoryRequestDTO;
import com.saidelhabhab.inventoryservice.dto.InventoryResponseDTO;
import com.saidelhabhab.inventoryservice.entity.Inventory;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    Inventory toEntity(InventoryRequestDTO dto);

    InventoryResponseDTO toDto(Inventory inventory);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(InventoryRequestDTO dto, @MappingTarget Inventory inventory);
}