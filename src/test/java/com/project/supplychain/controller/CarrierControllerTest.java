package com.project.supplychain.controller;

import com.project.supplychain.DTOs.carrierDTOs.CarrierDTO;
import com.project.supplychain.services.CarrierService;
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
class CarrierControllerTest {

    @Mock
    CarrierService service;

    @InjectMocks
    CarrierController controller;

    @Test
    void create_list_get_update_activate_suspend_reset_delete() {
        when(service.create(any())).thenReturn(new HashMap<>());
        when(service.list()).thenReturn(new HashMap<>());
        when(service.get(any())).thenReturn(new HashMap<>());
        when(service.update(any(), any())).thenReturn(new HashMap<>());
        when(service.activate(any())).thenReturn(new HashMap<>());
        when(service.suspend(any())).thenReturn(new HashMap<>());
        when(service.resetDailyCount(any())).thenReturn(new HashMap<>());
        when(service.delete(any())).thenReturn(new HashMap<>());

        ResponseEntity<?> r1 = controller.create(new CarrierDTO());
        ResponseEntity<?> r2 = controller.list();
        ResponseEntity<?> r3 = controller.get(UUID.randomUUID());
        ResponseEntity<?> r4 = controller.update(UUID.randomUUID(), new CarrierDTO());
        ResponseEntity<?> r5 = controller.activate(UUID.randomUUID());
        ResponseEntity<?> r6 = controller.suspend(UUID.randomUUID());
        ResponseEntity<?> r7 = controller.resetDailyCount(UUID.randomUUID());
        ResponseEntity<?> r8 = controller.delete(UUID.randomUUID());

        assertThat(r1.getStatusCodeValue()).isEqualTo(200);
        assertThat(r2.getStatusCodeValue()).isEqualTo(200);
        assertThat(r3.getStatusCodeValue()).isEqualTo(200);
        assertThat(r4.getStatusCodeValue()).isEqualTo(200);
        assertThat(r5.getStatusCodeValue()).isEqualTo(200);
        assertThat(r6.getStatusCodeValue()).isEqualTo(200);
        assertThat(r7.getStatusCodeValue()).isEqualTo(200);
        assertThat(r8.getStatusCodeValue()).isEqualTo(200);
    }
}
