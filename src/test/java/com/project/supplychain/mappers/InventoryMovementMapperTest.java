package com.project.supplychain.mappers;

import com.project.supplychain.DTOs.inventoryMovementDTOs.InventoryMovementDTO;
import com.project.supplychain.enums.MovementType;
import com.project.supplychain.models.InventoryMovement;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryMovementMapperTest {

    private final InventoryMovementMapper mapper = new InventoryMovementMapper();

    @Test
    void toEntity_and_toDTO_basic() {
        InventoryMovement m = new InventoryMovement();
        UUID id = UUID.randomUUID();
        m.setId(id);
        m.setType(MovementType.INBOUND);
        m.setQuantity(5);
        m.setOccurredAt(LocalDateTime.now());

        InventoryMovementDTO dto = mapper.toDTO(m);
        assertThat(dto).isNotNull();
        assertThat(dto.getType()).isEqualTo(MovementType.INBOUND);

        InventoryMovementDTO input = InventoryMovementDTO.builder().id(id).type(MovementType.OUTBOUND).quantity(1).build();
        InventoryMovement out = mapper.toEntity(input);
        assertThat(out).isNotNull();
        assertThat(out.getType()).isEqualTo(MovementType.OUTBOUND);
    }
}
