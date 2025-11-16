package com.project.supplychain.services;

import com.project.supplychain.DTOs.salesOrderLineDTOs.SalesOrderLineDTO;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.mappers.SalesOrderLineMapper;
import com.project.supplychain.models.*;
import com.project.supplychain.repositories.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalesOrderLineServiceTest {

    @Mock
    private SalesOrderLineRepository salesOrderLineRepository;

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryMovementRepository inventoryMovementRepository;

    @Mock
    private SalesOrderLineMapper mapper;

    @InjectMocks
    private SalesOrderLineService service;

    @Test
    void create_fullReserve_reservesAndReturnsDto() {
        UUID soId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        SalesOrderLineDTO dto = SalesOrderLineDTO.builder()
                .salesOrderId(soId)
                .productId(productId)
                .quantity(3)
                .build();

        SalesOrder so = new SalesOrder();
        so.setId(soId);
        Warehouse wh = new Warehouse();
        wh.setId(UUID.randomUUID());
        so.setWarehouse(wh);

        Product product = new Product();
        product.setId(productId);

        Inventory inv = new Inventory();
        inv.setId(UUID.randomUUID());
        inv.setProduct(product);
        inv.setWarehouse(wh);
        inv.setQtyOnHand(10);
        inv.setQtyReserved(1);

        SalesOrderLine entity = new SalesOrderLine();
        entity.setQuantity(3);

        SalesOrderLine saved = new SalesOrderLine();
        saved.setId(UUID.randomUUID());
        saved.setQuantity(3);

        when(salesOrderRepository.findById(soId)).thenReturn(Optional.of(so));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(inventoryRepository.getByQtyOnHandGreaterThanAndProduct(0, product)).thenReturn(List.of(inv));
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(salesOrderLineRepository.save(entity)).thenReturn(saved);
        when(mapper.toDTO(saved)).thenReturn(dto);

        var res = service.create(dto);

        assertThat(res).containsKey("message");
        assertThat(res.get("salesOrderLine")).isEqualTo(dto);

        // qtyReserved should have been incremented by 3 -> was 1 now 4
        ArgumentCaptor<Inventory> cap = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository).save(cap.capture());
        Inventory captured = cap.getValue();
        assertThat(captured.getQtyReserved()).isEqualTo(4);
    }

    @Test
    void create_noInventory_throws() {
        UUID soId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        SalesOrderLineDTO dto = SalesOrderLineDTO.builder()
                .salesOrderId(soId)
                .productId(productId)
                .quantity(2)
                .build();

        SalesOrder so = new SalesOrder();
        so.setId(soId);
        Warehouse wh = new Warehouse();
        wh.setId(UUID.randomUUID());
        so.setWarehouse(wh);

        Product product = new Product();
        product.setId(productId);

        when(salesOrderRepository.findById(soId)).thenReturn(Optional.of(so));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(inventoryRepository.getByQtyOnHandGreaterThanAndProduct(0, product)).thenReturn(List.of());

        assertThatThrownBy(() -> service.create(dto)).isInstanceOf(BadRequestException.class).hasMessageContaining("No inventory available");
    }

    @Test
    void update_adjustsReservations_whenWasReserved_andCanReserveNew() {
        UUID lineId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        SalesOrderLine existing = new SalesOrderLine();
        existing.setId(lineId);
        existing.setQuantity(2);
        existing.setBackorder(false);

        SalesOrder so = new SalesOrder();
        so.setId(UUID.randomUUID());
        Warehouse wh = new Warehouse(); wh.setId(UUID.randomUUID()); so.setWarehouse(wh);
        existing.setSalesOrder(so);

        Product oldProduct = new Product(); oldProduct.setId(UUID.randomUUID()); existing.setProduct(oldProduct);

        // old inventory
        Inventory oldInv = new Inventory(); oldInv.setProduct(oldProduct); oldInv.setWarehouse(wh); oldInv.setQtyReserved(5);

        // new product + inventory
        Product newProduct = new Product(); newProduct.setId(productId);
        Inventory newInv = new Inventory(); newInv.setProduct(newProduct); newInv.setWarehouse(wh); newInv.setQtyOnHand(10); newInv.setQtyReserved(1);

        SalesOrderLineDTO dto = SalesOrderLineDTO.builder().quantity(3).productId(productId).build();

        when(salesOrderLineRepository.findById(lineId)).thenReturn(Optional.of(existing));
        when(productRepository.findById(productId)).thenReturn(Optional.of(newProduct));
        when(inventoryRepository.findByProduct_IdAndWarehouse_Id(oldProduct.getId(), wh.getId())).thenReturn(Optional.of(oldInv));
        when(inventoryRepository.findByProduct_IdAndWarehouse_Id(newProduct.getId(), wh.getId())).thenReturn(Optional.of(newInv));
        when(salesOrderLineRepository.save(existing)).thenReturn(existing);
        when(mapper.toDTO(existing)).thenReturn(dto);

        var res = service.update(lineId, dto);

        assertThat(res).containsKey("message");
        assertThat(res.get("message")).isEqualTo("Sales order line updated and reserved");

        // oldInv reserved decreased by oldQty (2) -> was 5 now 3
        verify(inventoryRepository).save(oldInv);
        assertThat(oldInv.getQtyReserved()).isEqualTo(3);

        // newInv reserved increased by newQty (3) -> was 1 now 4
        verify(inventoryRepository, atLeastOnce()).save(newInv);
        assertThat(newInv.getQtyReserved()).isEqualTo(4);
    }

    @Test
    void delete_adjustsReservation_andDeletes() {
        UUID lineId = UUID.randomUUID();

        SalesOrderLine existing = new SalesOrderLine();
        existing.setId(lineId);
        existing.setQuantity(2);
        existing.setBackorder(false);

        SalesOrder so = new SalesOrder();
        Warehouse wh = new Warehouse(); wh.setId(UUID.randomUUID()); so.setWarehouse(wh);
        existing.setSalesOrder(so);

        Product p = new Product(); p.setId(UUID.randomUUID()); existing.setProduct(p);

        Inventory inv = new Inventory(); inv.setProduct(p); inv.setWarehouse(wh); inv.setQtyReserved(5);

        when(salesOrderLineRepository.findById(lineId)).thenReturn(Optional.of(existing));
        when(inventoryRepository.findByProduct_IdAndWarehouse_Id(p.getId(), wh.getId())).thenReturn(Optional.of(inv));

        var res = service.delete(lineId);

        assertThat(res).containsEntry("message", "Sales order line deleted and reservation adjusted");
        // reservation reduced by 2 -> 5 -> 3
        verify(inventoryRepository).save(inv);
        verify(salesOrderLineRepository).delete(existing);
        assertThat(inv.getQtyReserved()).isEqualTo(3);
    }
}

