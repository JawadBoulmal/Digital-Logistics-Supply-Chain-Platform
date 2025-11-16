package com.project.supplychain.services;

import com.project.supplychain.DTOs.inventoryDTOs.InventoryDTO;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.mappers.InventoryMapper;
import com.project.supplychain.models.Inventory;
import com.project.supplychain.models.Product;
import com.project.supplychain.models.Warehouse;
import com.project.supplychain.repositories.InventoryRepository;
import com.project.supplychain.repositories.ProductRepository;
import com.project.supplychain.repositories.WarehouseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private InventoryMapper inventoryMapper;

    @InjectMocks
    private InventoryService service;

    @Test
    void create_success_returnsDto() {
        UUID productId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();
        InventoryDTO dto = new InventoryDTO(); dto.setProductId(productId); dto.setWarehouseId(warehouseId); dto.setQtyOnHand(10); dto.setQtyReserved(2);

        Product p = new Product(); p.setId(productId);
        Warehouse w = new Warehouse(); w.setId(warehouseId);
        Inventory entity = new Inventory();
        Inventory saved = new Inventory(); saved.setId(UUID.randomUUID()); saved.setQtyOnHand(10); saved.setQtyReserved(2);

        when(productRepository.findById(productId)).thenReturn(Optional.of(p));
        when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(w));
        when(inventoryMapper.toEntity(dto)).thenReturn(entity);
        when(inventoryRepository.save(entity)).thenReturn(saved);
        when(inventoryMapper.toDTO(saved)).thenReturn(dto);

        var res = service.create(dto);
        assertThat(res).containsKey("message");
        assertThat(res.get("inventory")).isEqualTo(dto);
    }

    @Test
    void create_invalidQuantities_throws() {
        InventoryDTO dto = new InventoryDTO(); dto.setQtyOnHand(1); dto.setQtyReserved(5);
        assertThatThrownBy(() -> service.create(dto)).isInstanceOf(BadRequestException.class).hasMessageContaining("qtyReserved cannot exceed qtyOnHand");
    }

    @Test
    void get_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(inventoryRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.get(id)).isInstanceOf(BadRequestException.class).hasMessageContaining("Inventory not found");
    }

    @Test
    void update_changesProductAndWarehouse_andSaves() {
        UUID id = UUID.randomUUID();
        UUID newProductId = UUID.randomUUID();
        UUID newWarehouseId = UUID.randomUUID();

        Inventory existing = new Inventory(); existing.setId(id); existing.setQtyOnHand(10); existing.setQtyReserved(2);
        InventoryDTO dto = new InventoryDTO(); dto.setQtyOnHand(7); dto.setQtyReserved(1); dto.setProductId(newProductId); dto.setWarehouseId(newWarehouseId);

        Product p = new Product(); p.setId(newProductId);
        Warehouse w = new Warehouse(); w.setId(newWarehouseId);

        when(inventoryRepository.findById(id)).thenReturn(Optional.of(existing));
        when(productRepository.findById(newProductId)).thenReturn(Optional.of(p));
        when(warehouseRepository.findById(newWarehouseId)).thenReturn(Optional.of(w));
        when(inventoryRepository.save(existing)).thenReturn(existing);
        when(inventoryMapper.toDTO(existing)).thenReturn(dto);

        var res = service.update(id, dto);
        assertThat(res).containsEntry("message", "Inventory updated successfully");
        assertThat(res.get("inventory")).isEqualTo(dto);
    }
}

