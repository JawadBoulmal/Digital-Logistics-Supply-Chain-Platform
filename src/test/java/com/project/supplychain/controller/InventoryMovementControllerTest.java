package com.project.supplychain.controller;

import com.project.supplychain.DTOs.inventoryMovementDTOs.InventoryMovementDTO;
import com.project.supplychain.services.InventoryMovementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryMovementControllerTest {

    @Mock
    InventoryMovementService service;

    @InjectMocks
    InventoryMovementController controller;

    @Test
    void create_list_get_delete() {
        when(service.create(any())).thenReturn(new HashMap<>());
        when(service.list()).thenReturn(new HashMap<>());
        when(service.get(any())).thenReturn(new HashMap<>());
        when(service.delete(any())).thenReturn(new HashMap<>());

        ResponseEntity<?> r1 = controller.create(new InventoryMovementDTO());
        ResponseEntity<?> r2 = controller.list();
        ResponseEntity<?> r3 = controller.get(UUID.randomUUID());
        ResponseEntity<?> r4 = controller.delete(UUID.randomUUID());

        assertThat(r1.getStatusCodeValue()).isEqualTo(200);
        assertThat(r2.getStatusCodeValue()).isEqualTo(200);
        assertThat(r3.getStatusCodeValue()).isEqualTo(200);
        assertThat(r4.getStatusCodeValue()).isEqualTo(200);
    }
}
