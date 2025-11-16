package com.project.supplychain.mappers;

import com.project.supplychain.DTOs.carrierDTOs.CarrierDTO;
import com.project.supplychain.models.Carrier;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CarrierMapperTest {

    private final CarrierMapper mapper = new CarrierMapper();

    @Test
    void toEntity_and_toDTO_basic() {
        Carrier c = new Carrier();
        UUID id = UUID.randomUUID();
        c.setId(id);
        c.setCode("C1");
        c.setName("Carrier");
    c.setBaseShippingRate("10.0");

        CarrierDTO dto = mapper.toDTO(c);
        assertThat(dto).isNotNull();
        assertThat(dto.getCode()).isEqualTo("C1");

        CarrierDTO input = CarrierDTO.builder().id(id).code("C2").name("X").build();
        Carrier out = mapper.toEntity(input);
        assertThat(out).isNotNull();
        assertThat(out.getCode()).isEqualTo("C2");
    }
}
