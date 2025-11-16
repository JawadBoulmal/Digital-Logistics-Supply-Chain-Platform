package com.project.supplychain.mappers;

import com.project.supplychain.DTOs.warehouseDTOs.WarehouseDTO;
import com.project.supplychain.models.Warehouse;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class WarehouseMapperTest {

    private final WarehouseMapper mapper = new WarehouseMapper();

    @Test
    void toEntity_and_toDTO_basic() {
        Warehouse w = new Warehouse();
        UUID id = UUID.randomUUID();
        w.setId(id);
        w.setCode("W1");
        w.setName("Main");

        WarehouseDTO dto = mapper.toDTO(w);
        assertThat(dto).isNotNull();
        assertThat(dto.getCode()).isEqualTo("W1");

        WarehouseDTO input = WarehouseDTO.builder().id(id).code("W2").name("Second").active(true).build();
        Warehouse out = mapper.toEntity(input);
        assertThat(out).isNotNull();
        assertThat(out.getCode()).isEqualTo("W2");
    }
}
