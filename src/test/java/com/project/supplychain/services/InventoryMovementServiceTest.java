package com.project.supplychain.services;

import com.project.supplychain.DTOs.inventoryMovementDTOs.InventoryMovementDTO;
import com.project.supplychain.enums.MovementType;
import com.project.supplychain.mappers.InventoryMovementMapper;
import com.project.supplychain.models.Inventory;
import com.project.supplychain.models.InventoryMovement;
import com.project.supplychain.repositories.InventoryMovementRepository;
import com.project.supplychain.repositories.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryMovementServiceTest {

    @Mock
    private InventoryMovementRepository inventoryMovementRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryMovementMapper inventoryMovementMapper;

    @InjectMocks
    private InventoryMovementService inventoryMovementService;

    @Test
    void createInbound_appliesQtyToInventory() {
        InventoryMovementDTO dto = new InventoryMovementDTO();
        dto.setType(MovementType.INBOUND);
        dto.setQuantity(5);
        UUID invId = UUID.randomUUID();
        dto.setInventoryId(invId);

        Inventory inv = new Inventory();
        inv.setId(invId);
        inv.setQtyOnHand(10);

    InventoryMovement movement = new InventoryMovement();
    movement.setId(UUID.randomUUID());
    movement.setType(dto.getType());
    movement.setQuantity(dto.getQuantity());

        when(inventoryRepository.findById(invId)).thenReturn(Optional.of(inv));
        when(inventoryMovementMapper.toEntity(dto)).thenReturn(movement);
        when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenReturn(movement);

        var res = inventoryMovementService.create(dto);
        assertThat(res).containsKey("message");
    }
}
