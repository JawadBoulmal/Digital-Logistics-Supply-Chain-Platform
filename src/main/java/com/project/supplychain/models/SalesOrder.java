package com.project.supplychain.models;

import com.project.supplychain.enums.OrderStatus;
import com.project.supplychain.models.user.Client;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class SalesOrder {
    @Id
    @GeneratedValue
    private UUID id;

    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime reservedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;

    @OneToMany(mappedBy = "salesOrder", cascade = {CascadeType.ALL,CascadeType.MERGE}, orphanRemoval = true)
    private ArrayList<SalesOrderLine> salesOrderLines = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;
}
