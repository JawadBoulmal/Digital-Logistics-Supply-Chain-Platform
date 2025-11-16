package com.project.supplychain.services;

import com.project.supplychain.DTOs.warehouseDTOs.WarehouseDTO;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.mappers.WarehouseMapper;
import com.project.supplychain.models.Warehouse;
import com.project.supplychain.models.user.WarehouseManager;
import com.project.supplychain.repositories.UserRepository;
import com.project.supplychain.repositories.WarehouseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WarehouseMapper warehouseMapper;

    @InjectMocks
    private WarehouseService service;

    @Test
    void create_success_savesAndReturnsDto() {
        UUID mgrId = UUID.randomUUID();
        WarehouseDTO dto = new WarehouseDTO(); dto.setCode("W1"); dto.setName("W"); dto.setWarehouseManagerId(mgrId);

        WarehouseManager mgr = new WarehouseManager(); mgr.setId(mgrId);
        Warehouse entity = new Warehouse();
        Warehouse saved = new Warehouse(); saved.setId(UUID.randomUUID());

        when(userRepository.findById(mgrId)).thenReturn(Optional.of(mgr));
        when(warehouseRepository.existsByCode(dto.getCode())).thenReturn(false);
        when(warehouseMapper.toEntity(dto)).thenReturn(entity);
        when(warehouseRepository.save(entity)).thenReturn(saved);
        when(warehouseMapper.toDTO(saved)).thenReturn(dto);

        var res = service.create(dto);
        assertThat(res).containsKey("message");
        assertThat(res.get("warehouse")).isEqualTo(dto);
        verify(userRepository).save(mgr);
    }

    @Test
    void create_missingManager_throws() {
        WarehouseDTO dto = new WarehouseDTO(); dto.setCode("W1"); dto.setName("W"); dto.setWarehouseManagerId(null);
        assertThatThrownBy(() -> service.create(dto)).isInstanceOf(BadRequestException.class).hasMessageContaining("warehouse manager is required");
    }

    @Test
    void create_duplicateCode_throws() {
        UUID mgrId = UUID.randomUUID();
        WarehouseDTO dto = new WarehouseDTO(); dto.setCode("W1"); dto.setWarehouseManagerId(mgrId);
        WarehouseManager mgr = new WarehouseManager(); mgr.setId(mgrId);
        when(userRepository.findById(mgrId)).thenReturn(Optional.of(mgr));
        when(warehouseRepository.existsByCode(dto.getCode())).thenReturn(true);
        assertThatThrownBy(() -> service.create(dto)).isInstanceOf(BadRequestException.class).hasMessageContaining("warehouse code is already exist");
    }

    @Test
    void delete_existing_deletesSuccessfully() {
        UUID id = UUID.randomUUID();
        Warehouse existing = new Warehouse(); existing.setId(id);
        when(warehouseRepository.findById(id)).thenReturn(Optional.of(existing));
        var res = service.delete(id);
        assertThat(res).containsEntry("message", "Warehouse deleted successfully");
        verify(warehouseRepository).delete(existing);
    }
}

