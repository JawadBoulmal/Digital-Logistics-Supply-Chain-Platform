package com.project.supplychain.services;

import com.project.supplychain.DTOs.salesOrderDTOs.SalesOrderDTO;
import com.project.supplychain.mappers.SalesOrderMapper;
import com.project.supplychain.models.SalesOrder;
import com.project.supplychain.models.user.Client;
import com.project.supplychain.models.user.User;
import com.project.supplychain.models.Warehouse;
import com.project.supplychain.repositories.InventoryRepository;
import com.project.supplychain.repositories.SalesOrderRepository;
import com.project.supplychain.repositories.UserRepository;
import com.project.supplychain.repositories.WarehouseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalesOrderServiceUnitTest {

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private SalesOrderMapper salesOrderMapper;

    @InjectMocks
    private SalesOrderService salesOrderService;

    @Test
    void createSalesOrder_success() {
        UUID clientId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();

        SalesOrderDTO dto = new SalesOrderDTO();
        dto.setClientId(clientId);
        dto.setWarehouseId(warehouseId);
        dto.setCreatedAt(LocalDateTime.now());

        Client client = new Client();
        client.setId(clientId);

        SalesOrder toSave = new SalesOrder();

        SalesOrder saved = new SalesOrder();
        saved.setId(UUID.randomUUID());

        when(userRepository.findById(clientId)).thenReturn(Optional.of((User) client));
        when(warehouseRepository.getById(warehouseId)).thenReturn(new Warehouse());
        when(salesOrderMapper.toEntity(dto)).thenReturn(toSave);
        when(salesOrderRepository.save(any(SalesOrder.class))).thenReturn(saved);
        when(salesOrderMapper.toDTO(saved)).thenReturn(dto);

        var res = salesOrderService.create(dto);
        assertThat(res).containsKey("message");
        assertThat(res).containsKey("salesOrder");
    }
}
