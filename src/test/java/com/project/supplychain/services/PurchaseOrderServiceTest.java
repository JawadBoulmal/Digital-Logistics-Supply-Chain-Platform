package com.project.supplychain.services;

import com.project.supplychain.DTOs.purchaseOrderDTOs.PurchaseOrderDTO;
import com.project.supplychain.enums.PurchaseOrderStatus;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.mappers.PurchaseOrderLineMapper;
import com.project.supplychain.mappers.PurchaseOrderMapper;
import com.project.supplychain.models.*;
import com.project.supplychain.models.user.Client;
import com.project.supplychain.models.user.WarehouseManager;
import com.project.supplychain.repositories.PurchaseOrderRepository;
import com.project.supplychain.repositories.SupplierRepository;
import com.project.supplychain.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PurchaseOrderMapper purchaseOrderMapper;

    @Mock
    private PurchaseOrderLineMapper purchaseOrderLineMapper;

    @Mock
    private InventoryMovementService inventoryMovementService;

    @InjectMocks
    private PurchaseOrderService service;

    @Test
    void create_success_setsDefaultsAndReturnsDto() {
        UUID supplierId = UUID.randomUUID();
        UUID wmId = UUID.randomUUID();

        PurchaseOrderDTO dto = PurchaseOrderDTO.builder()
                .supplierId(supplierId)
                .warehouseManagerId(wmId)
                .expectedDelivery(LocalDateTime.now().plusDays(1))
                .build();

        Supplier supplier = new Supplier(); supplier.setId(supplierId);
        WarehouseManager wm = new WarehouseManager(); wm.setId(wmId);

        PurchaseOrder poEntity = new PurchaseOrder();
        poEntity.setExpectedDelivery(dto.getExpectedDelivery());

        PurchaseOrder saved = new PurchaseOrder(); saved.setId(UUID.randomUUID()); saved.setStatus(PurchaseOrderStatus.CREATED);

        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));
        when(userRepository.findById(wmId)).thenReturn(Optional.of(wm));
        when(purchaseOrderMapper.toEntity(dto)).thenReturn(poEntity);
        when(purchaseOrderRepository.save(poEntity)).thenReturn(saved);
        when(purchaseOrderMapper.toDTO(saved)).thenReturn(dto);

        var res = service.create(dto);

        assertThat(res).containsEntry("message", "Purchase order created successfully");
        assertThat(res).containsKey("purchaseOrder");
    }

    @Test
    void create_supplierNotFound_throws() {
        UUID supplierId = UUID.randomUUID();
        UUID wmId = UUID.randomUUID();
        PurchaseOrderDTO dto = PurchaseOrderDTO.builder().supplierId(supplierId).warehouseManagerId(wmId).build();
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.create(dto)).isInstanceOf(BadRequestException.class).hasMessageContaining("Supplier not found");
    }

    @Test
    void create_userNotWarehouseManager_throws() {
        UUID supplierId = UUID.randomUUID();
        UUID wmId = UUID.randomUUID();
        PurchaseOrderDTO dto = PurchaseOrderDTO.builder().supplierId(supplierId).warehouseManagerId(wmId).build();
        Supplier supplier = new Supplier(); supplier.setId(supplierId);
        Client user = new Client(); // not a WarehouseManager
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));
        when(userRepository.findById(wmId)).thenReturn(Optional.of(user));
        assertThatThrownBy(() -> service.create(dto)).isInstanceOf(BadRequestException.class).hasMessageContaining("Provided user is not a WarehouseManager");
    }

    @Test
    void approve_transitionsFromCreatedToApproved() {
        UUID id = UUID.randomUUID();
        PurchaseOrder po = new PurchaseOrder(); po.setId(id); po.setStatus(PurchaseOrderStatus.CREATED);
        when(purchaseOrderRepository.findById(id)).thenReturn(Optional.of(po));
        when(purchaseOrderRepository.save(po)).thenReturn(po);

        var res = service.approve(id);
        assertThat(res).containsEntry("message", "Purchase order approved");
    }

    @Test
    void receive_callsInventoryMovement_andMarksReceived() {
        UUID id = UUID.randomUUID();
        PurchaseOrder po = new PurchaseOrder(); po.setId(id); po.setStatus(PurchaseOrderStatus.APPROVED);
        PurchaseOrderLine line = new PurchaseOrderLine();
        Inventory inv = new Inventory(); inv.setId(UUID.randomUUID()); line.setInventory(inv); line.setQuantity(2);
        po.setPurchaseOrderLines(List.of(line));

        when(purchaseOrderRepository.findById(id)).thenReturn(Optional.of(po));
        when(purchaseOrderRepository.save(po)).thenReturn(po);

        var res = service.receive(id);

        verify(inventoryMovementService).create(any());
        assertThat(res).containsEntry("message", "Purchase order received");
    }

    @Test
    void update_whenReceived_throws() {
        UUID id = UUID.randomUUID();
        PurchaseOrder po = new PurchaseOrder(); po.setId(id); po.setStatus(PurchaseOrderStatus.RECEIVED);
        when(purchaseOrderRepository.findById(id)).thenReturn(Optional.of(po));
        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        assertThatThrownBy(() -> service.update(id, dto)).isInstanceOf(BadRequestException.class).hasMessageContaining("Cannot update a received");
    }

    @Test
    void delete_whenReceived_throws() {
        UUID id = UUID.randomUUID();
        PurchaseOrder po = new PurchaseOrder(); po.setId(id); po.setStatus(PurchaseOrderStatus.RECEIVED);
        when(purchaseOrderRepository.findById(id)).thenReturn(Optional.of(po));
        assertThatThrownBy(() -> service.delete(id)).isInstanceOf(BadRequestException.class).hasMessageContaining("Cannot delete a received");
    }
}
