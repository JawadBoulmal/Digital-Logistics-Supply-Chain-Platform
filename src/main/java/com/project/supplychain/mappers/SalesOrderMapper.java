package com.project.supplychain.mappers;

import com.project.supplychain.DTOs.salesOrderDTOs.SalesOrderDTO;
import com.project.supplychain.models.SalesOrder;
import org.springframework.stereotype.Component;

@Component
public class SalesOrderMapper {

    public SalesOrderDTO toDTO(SalesOrder entity) {
        if (entity == null) return null;
        return SalesOrderDTO.builder()
                .id(entity.getId())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .reservedAt(entity.getReservedAt())
                .shippedAt(entity.getShippedAt())
                .deliveredAt(entity.getDeliveredAt())
                .shipmentId(entity.getShipment() != null ? entity.getShipment().getId() : null)
                .clientId(entity.getClient() != null ? entity.getClient().getId() : null)
                .warehouseId(entity.getWarehouse() != null ? entity.getWarehouse().getId() : null)
                .build();
    }

    public SalesOrder toEntity(SalesOrderDTO dto) {
        if (dto == null) return null;
        SalesOrder entity = new SalesOrder();
        entity.setId(dto.getId());
        entity.setStatus(dto.getStatus());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setReservedAt(dto.getReservedAt());
        entity.setShippedAt(dto.getShippedAt());
        entity.setDeliveredAt(dto.getDeliveredAt());
        return entity;
    }
}
