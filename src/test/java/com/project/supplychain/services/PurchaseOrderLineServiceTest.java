package com.project.supplychain.services;

import com.project.supplychain.DTOs.purchaseOrderLineDTOs.PurchaseOrderLineDTO;
import com.project.supplychain.enums.PurchaseOrderStatus;
import com.project.supplychain.mappers.PurchaseOrderLineMapper;
import com.project.supplychain.models.Inventory;
import com.project.supplychain.models.Product;
import com.project.supplychain.models.PurchaseOrder;
import com.project.supplychain.models.PurchaseOrderLine;
import com.project.supplychain.repositories.InventoryRepository;
import com.project.supplychain.repositories.ProductRepository;
import com.project.supplychain.repositories.PurchaseOrderLineRepository;
import com.project.supplychain.repositories.PurchaseOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderLineServiceTest {

    @Mock
    private PurchaseOrderLineRepository purchaseOrderLineRepository;

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurchaseOrderLineMapper purchaseOrderLineMapper;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private PurchaseOrderLineService purchaseOrderLineService;

    @Test
    void createLine_whenOrderCreated_shouldSucceed() {
        UUID poId = UUID.randomUUID();
        UUID invId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        PurchaseOrderLineDTO dto = new PurchaseOrderLineDTO();
        dto.setPurchaseOrderId(poId);
        dto.setInventoryId(invId);
        dto.setProductId(productId);
        dto.setQuantity(3);

        PurchaseOrder po = new PurchaseOrder();
        po.setId(poId);
        po.setStatus(PurchaseOrderStatus.CREATED);

        Inventory inv = new Inventory();
        inv.setId(invId);

        Product product = new Product();
        product.setId(productId);
        product.setOriginalPrice(BigDecimal.valueOf(12));

        PurchaseOrderLine toSave = new PurchaseOrderLine();
        toSave.setQuantity(3);

        PurchaseOrderLine saved = new PurchaseOrderLine();
        saved.setId(UUID.randomUUID());

        when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.of(po));
        when(inventoryRepository.findById(invId)).thenReturn(Optional.of(inv));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(purchaseOrderLineMapper.toEntity(dto)).thenReturn(toSave);
        when(purchaseOrderLineRepository.save(any(PurchaseOrderLine.class))).thenReturn(saved);

        var res = purchaseOrderLineService.create(dto);
        assertThat(res).containsKey("message");
        assertThat(res).containsKey("purchaseOrderLine");
    }
}
