
package com.project.supplychain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.supplychain.DTOs.productDTOs.ProductDTO;
import com.project.supplychain.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest {

    private MockMvc mockMvc;
    private ProductService productService;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        productService = Mockito.mock(ProductService.class);
        ProductController controller = new ProductController();
        ReflectionTestUtils.setField(controller, "productService", productService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new com.project.supplychain.exceptions.GlobalExceptionHandler())
                .build();
    }

    @Test
    void create_returns_ok() throws Exception {
        ProductDTO dto = ProductDTO.builder()
                .sku("S1")
                .name("N")
                .category("C")
                .originalPrice(new BigDecimal("1.0"))
                .profit(new BigDecimal("0.5"))
                .active(true)
                .build();

        java.util.HashMap<String, Object> resp = new java.util.HashMap<>();
        resp.put("created", true);

        Mockito.when(productService.createProduct(any())).thenReturn(resp);

        mockMvc.perform(post("/api/products")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.created", is(true)));
    }

    @Test
    void list_and_get_return_ok() throws Exception {
        java.util.HashMap<String, Object> listResp = new java.util.HashMap<>();
        listResp.put("count", 0);
        Mockito.when(productService.listProducts()).thenReturn(listResp);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(0)));

        UUID id = UUID.randomUUID();
        java.util.HashMap<String, Object> getResp = new java.util.HashMap<>();
        getResp.put("id", id.toString());
        Mockito.when(productService.getProduct(eq(id))).thenReturn(getResp);

        mockMvc.perform(get("/api/products/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())));
    }

}
