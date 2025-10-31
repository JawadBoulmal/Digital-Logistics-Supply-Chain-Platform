package com.project.supplychain.repositories;

import com.project.supplychain.models.SalesOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SalesOrderLineRepository extends JpaRepository<SalesOrderLine, UUID> {
}
