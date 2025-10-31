package com.project.supplychain.mappers;

import com.project.supplychain.DTOs.salesOrderLineDTOs.SalesOrderLineDTO;
import com.project.supplychain.models.SalesOrderLine;
import org.springframework.stereotype.Component;

@Component
public class SalesOrderLineMapper {

    public SalesOrderLineDTO toDTO(SalesOrderLine entity) {
        if (entity == null) return null;
        return SalesOrderLineDTO.builder()
                .id(entity.getId())
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .backorder(entity.isBackorder())
                .productId(entity.getProduct() != null ? entity.getProduct().getId() : null)
                .salesOrderId(entity.getSalesOrder() != null ? entity.getSalesOrder().getId() : null)
                .build();
    }

    public SalesOrderLine toEntity(SalesOrderLineDTO dto) {
        if (dto == null) return null;
        SalesOrderLine entity = new SalesOrderLine();
        entity.setId(dto.getId());
        entity.setQuantity(dto.getQuantity());
        entity.setUnitPrice(dto.getUnitPrice());
        entity.setBackorder(dto.isBackorder());
        return entity;
    }
}
