package com.project.supplychain.mappers;

import com.project.supplychain.DTOs.salesOrderDTOs.SalesOrderDTO;
import com.project.supplychain.models.SalesOrder;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SalesOrderMapperTest {

    private final SalesOrderMapper mapper = new SalesOrderMapper();

    @Test
    void toEntity_and_toDTO_basic() {
        SalesOrder s = new SalesOrder();
        UUID id = UUID.randomUUID();
        s.setId(id);
        s.setCreatedAt(LocalDateTime.now());

        SalesOrderDTO dto = mapper.toDTO(s);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(id);

        SalesOrderDTO input = SalesOrderDTO.builder().id(id).build();
        SalesOrder out = mapper.toEntity(input);
        assertThat(out).isNotNull();
        assertThat(out.getId()).isEqualTo(id);
    }
}
