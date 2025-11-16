package com.project.supplychain.services;

import com.project.supplychain.DTOs.productDTOs.ProductDTO;
import com.project.supplychain.mappers.ProductMapper;
import com.project.supplychain.models.Product;
import com.project.supplychain.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProduct_shouldReturnCreatedProduct() {
        ProductDTO dto = new ProductDTO();
        dto.setSku("SKU-1");
        dto.setName("Test Product");
        dto.setOriginalPrice(BigDecimal.valueOf(10));

        Product toSave = new Product();
        toSave.setSku(dto.getSku());
        toSave.setName(dto.getName());
        toSave.setOriginalPrice(dto.getOriginalPrice());

        Product saved = new Product();
        saved.setId(UUID.randomUUID());
        saved.setSku(dto.getSku());
        saved.setName(dto.getName());
        saved.setOriginalPrice(dto.getOriginalPrice());

        ProductDTO outDto = new ProductDTO();
        outDto.setSku(dto.getSku());
        outDto.setName(dto.getName());
        outDto.setOriginalPrice(dto.getOriginalPrice());

        when(productMapper.toEntity(dto)).thenReturn(toSave);
        when(productRepository.save(any(Product.class))).thenReturn(saved);
        when(productMapper.toDTO(saved)).thenReturn(outDto);

        HashMap<String, Object> result = productService.createProduct(dto);

        assertThat(result).containsKey("message");
        assertThat(result).containsKey("product");
        assertThat(result.get("product")).isEqualTo(outDto);
    }
}
