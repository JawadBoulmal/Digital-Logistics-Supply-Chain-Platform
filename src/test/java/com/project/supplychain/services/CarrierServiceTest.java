package com.project.supplychain.services;

import com.project.supplychain.DTOs.carrierDTOs.CarrierDTO;
import com.project.supplychain.enums.CarrierStatus;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.mappers.CarrierMapper;
import com.project.supplychain.models.Carrier;
import com.project.supplychain.repositories.CarrierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarrierServiceTest {

    @Mock
    private CarrierRepository carrierRepository;

    @Mock
    private CarrierMapper carrierMapper;

    @InjectMocks
    private CarrierService carrierService;

    @Test
    void create_setsDefaultsAndReturnsDto() {
        CarrierDTO dto = CarrierDTO.builder()
                .code("C-001")
                .name("Carrier One")
                .maxDailyCapacity(10)
                .build();

        Carrier entity = new Carrier();
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setMaxDailyCapacity(dto.getMaxDailyCapacity());
        entity.setStatus(null);
        entity.setCurrentDailyShipments(null);

        Carrier saved = new Carrier();
        saved.setId(UUID.randomUUID());
        saved.setCode(dto.getCode());
        saved.setName(dto.getName());
        saved.setMaxDailyCapacity(dto.getMaxDailyCapacity());
        saved.setStatus(CarrierStatus.ACTIVE);
        saved.setCurrentDailyShipments(0);

        CarrierDTO outDto = CarrierDTO.builder().id(saved.getId()).code(saved.getCode()).name(saved.getName()).maxDailyCapacity(saved.getMaxDailyCapacity()).status(saved.getStatus()).currentDailyShipments(saved.getCurrentDailyShipments()).build();

        when(carrierMapper.toEntity(dto)).thenReturn(entity);
        when(carrierRepository.save(entity)).thenReturn(saved);
        when(carrierMapper.toDTO(saved)).thenReturn(outDto);

        var res = carrierService.create(dto);
        assertThat(res).containsKey("message");
        assertThat(res).containsKey("carrier");
        assertThat(res.get("carrier")).isEqualTo(outDto);
    }

    @Test
    void get_whenNotFound_throws() {
        UUID id = UUID.randomUUID();
        when(carrierRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> carrierService.get(id)).isInstanceOf(BadRequestException.class);
    }

    @Test
    void ensureCanShip_throwsWhenNotActive() {
        Carrier c = new Carrier();
        c.setStatus(CarrierStatus.SUSPENDED);
        assertThatThrownBy(() -> carrierService.ensureCanShip(c)).isInstanceOf(BadRequestException.class).hasMessageContaining("Carrier is not ACTIVE");
    }

    @Test
    void ensureCanShip_throwsWhenCapacityReached() {
        Carrier c = new Carrier();
        c.setStatus(CarrierStatus.ACTIVE);
        c.setMaxDailyCapacity(5);
        c.setCurrentDailyShipments(5);
        assertThatThrownBy(() -> carrierService.ensureCanShip(c)).isInstanceOf(BadRequestException.class).hasMessageContaining("Carrier has reached max daily capacity");
    }

    @Test
    void ensureCanShip_throwsWhenCutOffPassed() {
        Carrier c = new Carrier();
        c.setStatus(CarrierStatus.ACTIVE);
        c.setCutOffTime(LocalDateTime.now().minusHours(1));
        assertThatThrownBy(() -> carrierService.ensureCanShip(c)).isInstanceOf(BadRequestException.class).hasMessageContaining("cut-off time has passed");
    }

    @Test
    void incrementDailyShipments_incrementsAndSaves() {
        Carrier c = new Carrier();
        c.setCurrentDailyShipments(null);

        when(carrierRepository.save(any(Carrier.class))).thenAnswer(i -> i.getArgument(0));

        carrierService.incrementDailyShipments(c);

        verify(carrierRepository).save(c);
        assertThat(c.getCurrentDailyShipments()).isEqualTo(1);
    }
}
