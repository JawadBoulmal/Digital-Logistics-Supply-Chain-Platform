package com.project.supplychain.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class Product {
    @Id
    @GeneratedValue
    private UUID id;
    private String sku;
    private String name;
    private String category;
    private boolean active;
    private BigDecimal originalPrice;
    private BigDecimal profit;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.ALL,CascadeType.MERGE}, orphanRemoval = true)
    private ArrayList<Inventory> inventories;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.ALL,CascadeType.MERGE}, orphanRemoval = true)
    private ArrayList<SalesOrderLine> salesOrderLines;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.ALL,CascadeType.MERGE}, orphanRemoval = true)
    private ArrayList<PurchaseOrderLine> purchaseOrderLines;


}
