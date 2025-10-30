package com.project.supplychain.models;

import com.project.supplychain.enums.ShipmentStatus;
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
public class Shipment {
    @Id
    @GeneratedValue
    private UUID id;
    private String trackingNumber;
    private ShipmentStatus status;
    private LocalDateTime plannedDate;
    private LocalDateTime shippedDate;
    private LocalDateTime deliveredDate;

    @ManyToOne
    @JoinColumn(name = "sales_order_id")
    private SalesOrder salesOrder;

    @ManyToOne
    @JoinColumn(name = "carrier_id")
    private Carrier carrier;

}
