package com.project.supplychain.models.user;

import com.project.supplychain.enums.Roles;
import com.project.supplychain.models.Warehouse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;

@AllArgsConstructor
@Getter
@Setter
@Entity
@SuperBuilder
public class WarehouseManager extends User{

    @Enumerated(EnumType.STRING)
    private Roles role;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @OneToMany(mappedBy = "warehouseManager", cascade = {CascadeType.ALL,CascadeType.MERGE}, orphanRemoval = true)
    private ArrayList<Warehouse> warehouses;

    public WarehouseManager() {
        this.role = Roles.WAREHOUSE_MANAGER;
    }
}
