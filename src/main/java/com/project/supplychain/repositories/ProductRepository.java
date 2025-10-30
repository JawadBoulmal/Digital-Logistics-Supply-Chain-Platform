package com.project.supplychain.repositories;

import com.project.supplychain.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product , UUID> {
}
