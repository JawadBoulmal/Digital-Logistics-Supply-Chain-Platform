package com.project.supplychain.models;

import com.project.supplychain.enums.MovementType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class InventoryMovement {
    @Id
    @GeneratedValue
    private UUID id;
    private MovementType type;
    private Integer quantity;
    private LocalDateTime occurredAt;
    private String referenceDocument;
    private String description;

    @ManyToOne
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;
}
