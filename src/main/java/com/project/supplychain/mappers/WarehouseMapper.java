package com.project.supplychain.mappers;

import com.project.supplychain.DTOs.warehouseDTOs.WarehouseDTO;
import com.project.supplychain.models.Warehouse;
import org.springframework.stereotype.Component;

@Component
public class WarehouseMapper {

    public WarehouseDTO toDTO(Warehouse entity) {
        if (entity == null) return null;
        return WarehouseDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .active(entity.isActive())
                .warehouseManagerId(entity.getWarehouseManager() != null ? entity.getWarehouseManager().getId() : null)
                .build();
    }

    public Warehouse toEntity(WarehouseDTO dto) {
        if (dto == null) return null;
        Warehouse entity = new Warehouse();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setActive(dto.isActive());
        return entity;
    }
}
