
package com.project.supplychain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.supplychain.DTOs.purchaseOrderDTOs.PurchaseOrderDTO;
import com.project.supplychain.services.PurchaseOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PurchaseOrderControllerTest {

    private MockMvc mockMvc;
    private PurchaseOrderService service;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        service = Mockito.mock(PurchaseOrderService.class);
        PurchaseOrderController controller = new PurchaseOrderController();
        ReflectionTestUtils.setField(controller, "purchaseOrderService", service);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new com.project.supplychain.exceptions.GlobalExceptionHandler())
                .build();
    }

    @Test
    void create_and_list_and_get() throws Exception {
    PurchaseOrderDTO dto = PurchaseOrderDTO.builder()
        .supplierId(UUID.randomUUID())
        .warehouseManagerId(UUID.randomUUID())
        .build();

        HashMap<String, Object> resp = new HashMap<>();
        resp.put("ok", true);
        Mockito.when(service.create(any())).thenReturn(resp);

        mockMvc.perform(post("/api/purchaseOrders").contentType("application/json").content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ok", is(true)));

        HashMap<String, Object> listResp = new HashMap<>();
        listResp.put("count", 0);
        Mockito.when(service.list()).thenReturn(listResp);

        mockMvc.perform(get("/api/purchaseOrders")).andExpect(status().isOk()).andExpect(jsonPath("$.count", is(0)));

        UUID id = UUID.randomUUID();
        HashMap<String, Object> getResp = new HashMap<>();
        getResp.put("id", id.toString());
        Mockito.when(service.get(eq(id))).thenReturn(getResp);

        mockMvc.perform(get("/api/purchaseOrders/" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(id.toString())));
    }
}
