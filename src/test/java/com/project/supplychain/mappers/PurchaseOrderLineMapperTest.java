package com.project.supplychain.mappers;

import com.project.supplychain.DTOs.purchaseOrderLineDTOs.PurchaseOrderLineDTO;
import com.project.supplychain.models.PurchaseOrderLine;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PurchaseOrderLineMapperTest {

    private final PurchaseOrderLineMapper mapper = new PurchaseOrderLineMapper();

    @Test
    void toEntity_and_toDTO_basic() {
    PurchaseOrderLine l = new PurchaseOrderLine();
        UUID id = UUID.randomUUID();
        l.setId(id);
        l.setQuantity(3);
        l.setUnitPrice(new BigDecimal("5.0"));
    com.project.supplychain.models.Inventory inv = new com.project.supplychain.models.Inventory();
    java.util.UUID invId = java.util.UUID.randomUUID();
    inv.setId(invId);
    l.setInventory(inv);

        PurchaseOrderLineDTO dto = mapper.toDTO(l);
        assertThat(dto).isNotNull();
        assertThat(dto.getQuantity()).isEqualTo(3);

        PurchaseOrderLineDTO input = PurchaseOrderLineDTO.builder().id(id).quantity(2).unitPrice(new BigDecimal("2.5")).build();
        PurchaseOrderLine out = mapper.toEntity(input);
        assertThat(out).isNotNull();
        assertThat(out.getQuantity()).isEqualTo(2);
    }
}
