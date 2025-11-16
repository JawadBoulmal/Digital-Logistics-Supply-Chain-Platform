package com.project.supplychain.mappers;

import com.project.supplychain.DTOs.purchaseOrderDTOs.PurchaseOrderDTO;
import com.project.supplychain.models.PurchaseOrder;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PurchaseOrderMapperTest {

    private final PurchaseOrderMapper mapper = new PurchaseOrderMapper();

    @Test
    void toEntity_and_toDTO_basic_fields() {
        PurchaseOrder p = new PurchaseOrder();
        UUID id = UUID.randomUUID();
        p.setId(id);
        p.setCreatedAt(LocalDateTime.now());

        PurchaseOrderDTO dto = mapper.toDTO(p);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(id);

        PurchaseOrderDTO input = PurchaseOrderDTO.builder().id(id).build();
        PurchaseOrder out = mapper.toEntity(input);
        assertThat(out).isNotNull();
        assertThat(out.getId()).isEqualTo(id);
    }
}
