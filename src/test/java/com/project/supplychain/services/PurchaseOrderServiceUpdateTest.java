package com.project.supplychain.services;

import com.project.supplychain.DTOs.purchaseOrderDTOs.PurchaseOrderDTO;
import com.project.supplychain.enums.PurchaseOrderStatus;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.mappers.PurchaseOrderMapper;
import com.project.supplychain.mappers.PurchaseOrderLineMapper;
import com.project.supplychain.models.PurchaseOrder;
import com.project.supplychain.models.Supplier;
import com.project.supplychain.models.user.User;
import com.project.supplychain.models.user.WarehouseManager;
import com.project.supplychain.models.user.Client;
import com.project.supplychain.repositories.PurchaseOrderRepository;
import com.project.supplychain.repositories.SupplierRepository;
import com.project.supplychain.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceUpdateTest {

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

    @InjectMocks
    private PurchaseOrderService purchaseOrderService;

    private PurchaseOrder makePO(UUID id, PurchaseOrderStatus status, UUID supplierId, UUID wmId) {
        PurchaseOrder po = new PurchaseOrder();
        po.setId(id);
        po.setStatus(status);
        Supplier s = null;
        if (supplierId != null) {
            s = new Supplier();
            s.setId(supplierId);
            po.setSupplier(s);
        }
        if (wmId != null) {
            WarehouseManager wm = new WarehouseManager();
            wm.setId(wmId);
            po.setWarehouseManager(wm);
        }
        po.setExpectedDelivery(LocalDateTime.now().plusDays(5));
        return po;
    }

    @Test
    void update_success_changes_expectedDelivery_and_supplier_and_manager_when_needed() {
        UUID poId = UUID.randomUUID();
        UUID oldSupplierId = UUID.randomUUID();
        UUID newSupplierId = UUID.randomUUID();
        UUID oldWmId = UUID.randomUUID();
        UUID newWmId = UUID.randomUUID();

        PurchaseOrder existing = makePO(poId, PurchaseOrderStatus.CREATED, oldSupplierId, oldWmId);

        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        dto.setExpectedDelivery(LocalDateTime.now().plusDays(10));
        dto.setSupplierId(newSupplierId);
        dto.setWarehouseManagerId(newWmId);

        Supplier newSupplier = new Supplier();
        newSupplier.setId(newSupplierId);

        WarehouseManager newWm = new WarehouseManager();
        newWm.setId(newWmId);

        PurchaseOrder saved = makePO(poId, PurchaseOrderStatus.CREATED, newSupplierId, newWmId);

        when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.of(existing));
        when(supplierRepository.findById(newSupplierId)).thenReturn(Optional.of(newSupplier));
        when(userRepository.findById(newWmId)).thenReturn(Optional.of((User) newWm));
        when(purchaseOrderRepository.save(existing)).thenReturn(saved);
        when(purchaseOrderMapper.toDTO(saved)).thenReturn(new com.project.supplychain.DTOs.purchaseOrderDTOs.PurchaseOrderDTO());

        var res = purchaseOrderService.update(poId, dto);

        assertThat(res).containsKey("message");
        assertThat(res).containsKey("purchaseOrder");
    }

    @Test
    void update_throws_when_supplier_not_found() {
        UUID poId = UUID.randomUUID();
        UUID newSupplierId = UUID.randomUUID();

        PurchaseOrder existing = makePO(poId, PurchaseOrderStatus.CREATED, UUID.randomUUID(), UUID.randomUUID());

        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        dto.setSupplierId(newSupplierId);

        when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.of(existing));
        when(supplierRepository.findById(newSupplierId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> purchaseOrderService.update(poId, dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Supplier not found");
    }

    @Test
    void update_throws_when_user_not_warehouseManager() {
        UUID poId = UUID.randomUUID();
        UUID newWmId = UUID.randomUUID();

        PurchaseOrder existing = makePO(poId, PurchaseOrderStatus.CREATED, UUID.randomUUID(), UUID.randomUUID());

        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        dto.setWarehouseManagerId(newWmId);

    Client notWm = new Client();
    notWm.setId(newWmId);

        when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.of(existing));
        when(userRepository.findById(newWmId)).thenReturn(Optional.of(notWm));

        assertThatThrownBy(() -> purchaseOrderService.update(poId, dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Provided user is not a WarehouseManager");
    }

    @Test
    void update_throws_when_status_received_or_canceled() {
        UUID poId1 = UUID.randomUUID();
        PurchaseOrder received = makePO(poId1, PurchaseOrderStatus.RECEIVED, null, null);
        when(purchaseOrderRepository.findById(poId1)).thenReturn(Optional.of(received));

        assertThatThrownBy(() -> purchaseOrderService.update(poId1, new PurchaseOrderDTO()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Cannot update a received purchase order");

        UUID poId2 = UUID.randomUUID();
        PurchaseOrder canceled = makePO(poId2, PurchaseOrderStatus.CANCELED, null, null);
        when(purchaseOrderRepository.findById(poId2)).thenReturn(Optional.of(canceled));

        assertThatThrownBy(() -> purchaseOrderService.update(poId2, new PurchaseOrderDTO()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Cannot update a canceled purchase order");
    }
}
