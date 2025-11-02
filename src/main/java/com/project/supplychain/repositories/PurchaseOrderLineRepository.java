package com.project.supplychain.repositories;

import com.project.supplychain.models.PurchaseOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PurchaseOrderLineRepository extends JpaRepository<PurchaseOrderLine, UUID> {
}
