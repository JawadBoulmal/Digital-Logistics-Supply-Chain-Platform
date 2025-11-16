package com.project.supplychain.services;

import com.project.supplychain.DTOs.salesOrderLineDTOs.SalesOrderLineDTO;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.mappers.SalesOrderLineMapper;
import com.project.supplychain.models.*;
import com.project.supplychain.repositories.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalesOrderLineServiceCreateTest {

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

    private Inventory makeInventory(UUID productId, UUID warehouseId, Integer onHand, Integer reserved) {
        Inventory inv = new Inventory();
        inv.setId(UUID.randomUUID());
        Product p = new Product();
        p.setId(productId);
        inv.setProduct(p);
        Warehouse w = new Warehouse();
        w.setId(warehouseId);
        inv.setWarehouse(w);
        inv.setQtyOnHand(onHand);
        inv.setQtyReserved(reserved);
        return inv;
    }

    @Test
    void create_fullReserve_success() {
        UUID soId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();

        SalesOrderLineDTO dto = new SalesOrderLineDTO();
        dto.setSalesOrderId(soId);
        dto.setProductId(productId);
        dto.setQuantity(5);

        SalesOrder order = new SalesOrder();
        Warehouse w = new Warehouse();
        w.setId(warehouseId);
        order.setWarehouse(w);

        Product product = new Product();
        product.setId(productId);
        product.setOriginalPrice(new BigDecimal("10"));
        product.setProfit(new BigDecimal("2"));

        Inventory inv = makeInventory(productId, warehouseId, 10, 0);

        SalesOrderLine entity = new SalesOrderLine();
        entity.setQuantity(5);
        entity.setUnitPrice(null);

        SalesOrderLine saved = new SalesOrderLine();
        saved.setId(UUID.randomUUID());

        when(salesOrderRepository.findById(soId)).thenReturn(Optional.of(order));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(inventoryRepository.getByQtyOnHandGreaterThanAndProduct(0, product)).thenReturn(List.of(inv));
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(salesOrderLineRepository.save(entity)).thenReturn(saved);
        when(mapper.toDTO(saved)).thenReturn(new com.project.supplychain.DTOs.salesOrderLineDTOs.SalesOrderLineDTO());

        var res = service.create(dto);

        assertThat(res).containsKey("message");
        assertThat(res).containsKey("salesOrderLine");
        // inventory reserved increased by 5
        assertThat(inv.getQtyReserved()).isEqualTo(5);
        verify(inventoryRepository, atLeastOnce()).save(inv);
        verify(salesOrderLineRepository).save(entity);
    }

    @Test
    void create_partialReserve_and_backorder() {
        UUID soId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();

        SalesOrderLineDTO dto = new SalesOrderLineDTO();
        dto.setSalesOrderId(soId);
        dto.setProductId(productId);
        dto.setQuantity(5);

        SalesOrder order = new SalesOrder();
        Warehouse w = new Warehouse();
        w.setId(warehouseId);
        order.setWarehouse(w);

        Product product = new Product();
        product.setId(productId);
        product.setOriginalPrice(new BigDecimal("5"));
        product.setProfit(new BigDecimal("1"));

        Inventory inv1 = makeInventory(productId, warehouseId, 3, 0);
        Inventory inv2 = makeInventory(productId, warehouseId, 1, 0);

        SalesOrderLine entity = new SalesOrderLine();
        entity.setQuantity(5);

        SalesOrderLine saved = new SalesOrderLine();
        saved.setId(UUID.randomUUID());

        when(salesOrderRepository.findById(soId)).thenReturn(Optional.of(order));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(inventoryRepository.getByQtyOnHandGreaterThanAndProduct(0, product)).thenReturn(List.of(inv1, inv2));
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(salesOrderLineRepository.save(entity)).thenReturn(saved);
        when(mapper.toDTO(saved)).thenReturn(new com.project.supplychain.DTOs.salesOrderLineDTOs.SalesOrderLineDTO());

        var res = service.create(dto);

        assertThat(res).containsKey("message");
        assertThat(res.get("message")).isEqualTo("Sales order line created (partial stock reserved, backorder for remaining)");
        assertThat(inv1.getQtyReserved()).isEqualTo(3);
        assertThat(inv2.getQtyReserved()).isEqualTo(1);
        verify(inventoryRepository, atLeast(2)).save(any(Inventory.class));
    }

    @Test
    void create_noInventory_throws() {
        UUID soId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        SalesOrderLineDTO dto = new SalesOrderLineDTO();
        dto.setSalesOrderId(soId);
        dto.setProductId(productId);
        dto.setQuantity(2);

        SalesOrder order = new SalesOrder();
        Warehouse w = new Warehouse();
        w.setId(UUID.randomUUID());
        order.setWarehouse(w);

        Product product = new Product();
        product.setId(productId);

        when(salesOrderRepository.findById(soId)).thenReturn(Optional.of(order));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(inventoryRepository.getByQtyOnHandGreaterThanAndProduct(0, product)).thenReturn(List.of());

        assertThatThrownBy(() -> service.create(dto)).isInstanceOf(BadRequestException.class).hasMessageContaining("No inventory available for this product");
    }

    @Test
    void create_missingSalesOrder_orProduct_throws() {
        UUID soId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        SalesOrderLineDTO dto = new SalesOrderLineDTO();
        dto.setSalesOrderId(soId);
        dto.setProductId(productId);
        dto.setQuantity(1);

        when(salesOrderRepository.findById(soId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.create(dto)).isInstanceOf(BadRequestException.class).hasMessageContaining("Sales order not found");

        when(salesOrderRepository.findById(soId)).thenReturn(Optional.of(new SalesOrder()));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.create(dto)).isInstanceOf(BadRequestException.class).hasMessageContaining("Product not found");
    }
}
