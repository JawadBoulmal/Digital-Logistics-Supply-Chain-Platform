package com.project.supplychain.services;

import com.project.supplychain.DTOs.inventoryMovementDTOs.InventoryMovementDTO;
import com.project.supplychain.DTOs.purchaseOrderDTOs.PurchaseOrderDTO;
import com.project.supplychain.DTOs.purchaseOrderLineDTOs.PurchaseOrderLineDTO;
import com.project.supplychain.enums.MovementType;
import com.project.supplychain.enums.PurchaseOrderStatus;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.mappers.PurchaseOrderMapper;
import com.project.supplychain.mappers.PurchaseOrderLineMapper;
import com.project.supplychain.models.PurchaseOrder;
import com.project.supplychain.models.PurchaseOrderLine;
import com.project.supplychain.models.Supplier;
import com.project.supplychain.models.Inventory;
import com.project.supplychain.models.user.WarehouseManager;
import com.project.supplychain.models.user.Client;
import com.project.supplychain.repositories.PurchaseOrderRepository;
import com.project.supplychain.repositories.SupplierRepository;
import com.project.supplychain.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class PurchaseOrderServiceFullTest {

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
    private PurchaseOrderService purchaseOrderService;

    private PurchaseOrder makePO(UUID id, PurchaseOrderStatus status) {
        PurchaseOrder po = new PurchaseOrder();
        po.setId(id);
        po.setStatus(status);
        po.setCreatedAt(LocalDateTime.now());
        po.setExpectedDelivery(LocalDateTime.now().plusDays(3));
        return po;
    }

    @Test
    void create_success_sets_fields_and_returns_dto() {
        UUID supplierId = UUID.randomUUID();
        UUID wmId = UUID.randomUUID();

        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        dto.setSupplierId(supplierId);
        dto.setWarehouseManagerId(wmId);
        dto.setExpectedDelivery(LocalDateTime.now().plusDays(7));

        Supplier supplier = new Supplier();
        supplier.setId(supplierId);

        WarehouseManager wm = new WarehouseManager();
        wm.setId(wmId);

        PurchaseOrder toSave = new PurchaseOrder();
        PurchaseOrder saved = makePO(UUID.randomUUID(), PurchaseOrderStatus.CREATED);

        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));
    when(userRepository.findById(wmId)).thenReturn(Optional.of((com.project.supplychain.models.user.User) wm));
        // userRepository returns a WarehouseManager instance; cast is fine on runtime
        when(purchaseOrderMapper.toEntity(dto)).thenReturn(toSave);
        when(purchaseOrderRepository.save(toSave)).thenReturn(saved);
        when(purchaseOrderMapper.toDTO(saved)).thenReturn(dto);

        var res = purchaseOrderService.create(dto);

        assertThat(res).containsKey("message");
        assertThat(res).containsKey("purchaseOrder");
        verify(purchaseOrderRepository).save(toSave);
    }

    @Test
    void create_throws_when_supplier_missing_or_user_missing_or_user_not_wm() {
        UUID supplierId = UUID.randomUUID();
        UUID wmId = UUID.randomUUID();

        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        dto.setSupplierId(supplierId);
        dto.setWarehouseManagerId(wmId);

        when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> purchaseOrderService.create(dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Supplier not found");

        // supplier found but user missing
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(new Supplier()));
        when(userRepository.findById(wmId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> purchaseOrderService.create(dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Warehouse manager not found");

        // user found but not WarehouseManager
    when(userRepository.findById(wmId)).thenReturn(Optional.of((com.project.supplychain.models.user.User) new Client()));
        assertThatThrownBy(() -> purchaseOrderService.create(dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Provided user is not a WarehouseManager");
    }

    @Test
    void get_and_getOPLinesById_and_list_return_expected_structures() {
        UUID poId = UUID.randomUUID();
        PurchaseOrder po = makePO(poId, PurchaseOrderStatus.CREATED);

        PurchaseOrderLine line = new PurchaseOrderLine();
        line.setId(UUID.randomUUID());
        Inventory inv = new Inventory();
        inv.setId(UUID.randomUUID());
        line.setInventory(inv);
        po.setPurchaseOrderLines(List.of(line));

        PurchaseOrderDTO dto = new PurchaseOrderDTO();

        when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.of(po));
        when(purchaseOrderMapper.toDTO(po)).thenReturn(dto);
        when(purchaseOrderLineMapper.toDTO(line)).thenReturn(new PurchaseOrderLineDTO());

        var resGet = purchaseOrderService.get(poId);
        assertThat(resGet).containsKeys("purchaseOrder", "POLines");

        var resLines = purchaseOrderService.getOPLinesById(poId);
        assertThat(resLines).containsKey("POLines");

        // list()
        when(purchaseOrderRepository.findAll()).thenReturn(List.of(po));
        var resList = purchaseOrderService.list();
        assertThat(resList).containsKey("purchaseOrders");
    }

    @Test
    void delete_approve_cancel_receive_behaviors() {
        UUID poDelete = UUID.randomUUID();
        PurchaseOrder deletable = makePO(poDelete, PurchaseOrderStatus.CREATED);
        when(purchaseOrderRepository.findById(poDelete)).thenReturn(Optional.of(deletable));

        var delRes = purchaseOrderService.delete(poDelete);
        assertThat(delRes).containsKey("message");
        verify(purchaseOrderRepository).delete(deletable);

        UUID poApprove = UUID.randomUUID();
        PurchaseOrder toApprove = makePO(poApprove, PurchaseOrderStatus.CREATED);
        when(purchaseOrderRepository.findById(poApprove)).thenReturn(Optional.of(toApprove));
        when(purchaseOrderRepository.save(toApprove)).thenReturn(toApprove);
        when(purchaseOrderMapper.toDTO(toApprove)).thenReturn(new PurchaseOrderDTO());

        var appRes = purchaseOrderService.approve(poApprove);
        assertThat(appRes).containsKey("message");

        UUID poCancel = UUID.randomUUID();
        PurchaseOrder toCancel = makePO(poCancel, PurchaseOrderStatus.CREATED);
        when(purchaseOrderRepository.findById(poCancel)).thenReturn(Optional.of(toCancel));
        when(purchaseOrderRepository.save(toCancel)).thenReturn(toCancel);
        when(purchaseOrderMapper.toDTO(toCancel)).thenReturn(new PurchaseOrderDTO());

        var canRes = purchaseOrderService.cancel(poCancel);
        assertThat(canRes).containsKey("message");

        // receive: prepare approved PO with lines, ensure InventoryMovementService called for each line
        UUID poReceive = UUID.randomUUID();
        PurchaseOrder toReceive = makePO(poReceive, PurchaseOrderStatus.APPROVED);
        PurchaseOrderLine pol = new PurchaseOrderLine();
        Inventory inv = new Inventory();
        inv.setId(UUID.randomUUID());
        pol.setInventory(inv);
        pol.setQuantity(5);
        toReceive.setPurchaseOrderLines(List.of(pol));

        when(purchaseOrderRepository.findById(poReceive)).thenReturn(Optional.of(toReceive));
        when(purchaseOrderRepository.save(toReceive)).thenReturn(toReceive);
        when(purchaseOrderMapper.toDTO(toReceive)).thenReturn(new PurchaseOrderDTO());

        var r = purchaseOrderService.receive(poReceive);
        assertThat(r).containsKey("message");

        ArgumentCaptor<InventoryMovementDTO> captor = ArgumentCaptor.forClass(InventoryMovementDTO.class);
        verify(inventoryMovementService, times(1)).create(captor.capture());
        InventoryMovementDTO sent = captor.getValue();
        assertThat(sent.getType()).isEqualTo(MovementType.INBOUND);
        assertThat(sent.getQuantity()).isEqualTo(5);
    }

    @Test
    void delete_approve_cancel_receive_errors_on_bad_states() {
        UUID rcvId = UUID.randomUUID();
        PurchaseOrder received = makePO(rcvId, PurchaseOrderStatus.RECEIVED);
        when(purchaseOrderRepository.findById(rcvId)).thenReturn(Optional.of(received));

        assertThatThrownBy(() -> purchaseOrderService.delete(rcvId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Cannot delete a received purchase order");

        UUID apId = UUID.randomUUID();
        PurchaseOrder notCreated = makePO(apId, PurchaseOrderStatus.APPROVED);
        when(purchaseOrderRepository.findById(apId)).thenReturn(Optional.of(notCreated));
        assertThatThrownBy(() -> purchaseOrderService.approve(apId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Only CREATED orders can be approved");

        UUID cancelId = UUID.randomUUID();
        when(purchaseOrderRepository.findById(cancelId)).thenReturn(Optional.of(received));
        assertThatThrownBy(() -> purchaseOrderService.cancel(cancelId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Cannot cancel a received order");

        UUID recId2 = UUID.randomUUID();
        when(purchaseOrderRepository.findById(recId2)).thenReturn(Optional.of(makePO(recId2, PurchaseOrderStatus.CREATED)));
        assertThatThrownBy(() -> purchaseOrderService.receive(recId2))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Only APPROVED orders can be received");
    }
}
