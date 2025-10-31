package com.project.supplychain.repositories;

import com.project.supplychain.models.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {
    boolean existsByCode(String code);
}
