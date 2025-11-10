package com.project.supplychain.repositories;

import com.project.supplychain.enums.OrderStatus;
import com.project.supplychain.models.Product;
import com.project.supplychain.models.SalesOrder;
import com.project.supplychain.models.SalesOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SalesOrderLineRepository extends JpaRepository<SalesOrderLine, UUID> {
    List<SalesOrderLine> findBySalesOrderId(UUID salesOrderId);

    Integer countByProduct_SkuAndSalesOrder_Status(String product_sku, OrderStatus salesOrder_status);
    List<SalesOrderLine> getByProduct_SkuAndSalesOrder_Status(String product_sku, OrderStatus salesOrder_status);
}
