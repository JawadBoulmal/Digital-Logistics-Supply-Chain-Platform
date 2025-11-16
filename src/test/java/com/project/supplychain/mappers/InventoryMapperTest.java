package com.project.supplychain.mappers;

import com.project.supplychain.DTOs.inventoryDTOs.InventoryDTO;
import com.project.supplychain.models.Inventory;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryMapperTest {

    private final InventoryMapper mapper = new InventoryMapper();

    @Test
    void toEntity_and_toDTO_map_basic_fields() {
        Inventory e = new Inventory();
        UUID id = UUID.randomUUID();
        e.setId(id);
        e.setQtyOnHand(10);
        e.setQtyReserved(2);

        InventoryDTO dto = mapper.toDTO(e);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getQtyOnHand()).isEqualTo(10);

        InventoryDTO input = InventoryDTO.builder().id(id).qtyOnHand(5).qtyReserved(1).build();
        Inventory out = mapper.toEntity(input);
        assertThat(out).isNotNull();
        assertThat(out.getQtyOnHand()).isEqualTo(5);
    }
}
