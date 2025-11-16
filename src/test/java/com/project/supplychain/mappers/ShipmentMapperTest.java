package com.project.supplychain.mappers;

import com.project.supplychain.DTOs.shipmentDTOs.ShipmentDTO;
import com.project.supplychain.models.Shipment;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ShipmentMapperTest {

    private final ShipmentMapper mapper = new ShipmentMapper();

    @Test
    void toEntity_and_toDTO_basic() {
        Shipment s = new Shipment();
        UUID id = UUID.randomUUID();
        s.setId(id);
        s.setTrackingNumber("TRK1");

        ShipmentDTO dto = mapper.toDTO(s);
        assertThat(dto).isNotNull();
        assertThat(dto.getTrackingNumber()).isEqualTo("TRK1");

        ShipmentDTO input = ShipmentDTO.builder().id(id).trackingNumber("X").build();
        Shipment out = mapper.toEntity(input);
        assertThat(out).isNotNull();
        assertThat(out.getTrackingNumber()).isEqualTo("X");
    }
}
