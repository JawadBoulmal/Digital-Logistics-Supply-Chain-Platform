package com.project.supplychain.mappers;

import com.project.supplychain.DTOs.purchaseOrderLineDTOs.PurchaseOrderLineDTO;
import com.project.supplychain.models.PurchaseOrderLine;
import org.springframework.stereotype.Component;

@Component
public class PurchaseOrderLineMapper {

    public PurchaseOrderLineDTO toDTO(PurchaseOrderLine entity) {
        if (entity == null) return null;
        return PurchaseOrderLineDTO.builder()
                .id(entity.getId())
                .purchaseOrderId(entity.getPurchaseOrder() != null ? entity.getPurchaseOrder().getId() : null)
                .productId(entity.getProduct() != null ? entity.getProduct().getId() : null)
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .build();
    }

    public PurchaseOrderLine toEntity(PurchaseOrderLineDTO dto) {
        if (dto == null) return null;
        PurchaseOrderLine entity = new PurchaseOrderLine();
        entity.setId(dto.getId());
        entity.setQuantity(dto.getQuantity());
        entity.setUnitPrice(dto.getUnitPrice());
        // relations set in service
        return entity;
    }
}
