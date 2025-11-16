package com.project.supplychain.mappers;

import com.project.supplychain.DTOs.supplierDTOs.SupplierDTO;
import com.project.supplychain.models.Supplier;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SupplierMapperTest {

    private final SupplierMapper mapper = new SupplierMapper();

    @Test
    void toEntity_and_toDTO_map_fields() {
        UUID id = UUID.randomUUID();
        Supplier entity = new Supplier();
        entity.setId(id);
        entity.setName("Acme");
        entity.setContactInfo("acme@example.com");

        SupplierDTO dto = mapper.toDTO(entity);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getName()).isEqualTo("Acme");

        SupplierDTO input = SupplierDTO.builder().id(id).name("B").contactInfo("b@x").build();
        Supplier out = mapper.toEntity(input);
        assertThat(out).isNotNull();
        assertThat(out.getName()).isEqualTo("B");
    }
}
