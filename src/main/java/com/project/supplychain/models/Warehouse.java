package com.project.supplychain.models;

import com.project.supplychain.models.user.WarehouseManager;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class Warehouse {
    @Id
    @GeneratedValue
    private UUID id;
    private String code;
    private String name;
    private boolean active;

    @OneToMany(mappedBy = "warehouse", cascade = {CascadeType.ALL,CascadeType.MERGE}, orphanRemoval = true)
    private ArrayList<Inventory> inventories;

    @OneToMany(mappedBy = "warehouse", cascade = {CascadeType.ALL,CascadeType.MERGE}, orphanRemoval = true)
    private ArrayList<SalesOrder> salesOrders;

    @ManyToOne
    @JoinColumn(name = "warehouse_manager_id")
    private WarehouseManager warehouseManager;


}
