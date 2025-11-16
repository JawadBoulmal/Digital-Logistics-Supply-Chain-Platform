package com.project.supplychain.mappers;

import com.project.supplychain.DTOs.productDTOs.ProductDTO;
import com.project.supplychain.models.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {

    private final ProductMapper mapper = new ProductMapper();

    @Test
    void toEntity_and_back_should_map_fields() {
        UUID id = UUID.randomUUID();
        ProductDTO dto = ProductDTO.builder()
                .id(id)
                .sku("SKU-123")
                .name("Test Product")
                .category("CAT")
                .active(true)
                .originalPrice(new BigDecimal("12.50"))
                .profit(new BigDecimal("2.50"))
                .build();

        Product entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getSku()).isEqualTo("SKU-123");
        assertThat(entity.getName()).isEqualTo("Test Product");
        assertThat(entity.getCategory()).isEqualTo("CAT");
        assertThat(entity.isActive()).isTrue();
        assertThat(entity.getOriginalPrice()).isEqualByComparingTo(new BigDecimal("12.50"));
        assertThat(entity.getProfit()).isEqualByComparingTo(new BigDecimal("2.50"));

        ProductDTO dto2 = mapper.toDTO(entity);
        assertThat(dto2).isNotNull();
        assertThat(dto2.getId()).isEqualTo(id);
        assertThat(dto2.getSku()).isEqualTo("SKU-123");
    }
}
