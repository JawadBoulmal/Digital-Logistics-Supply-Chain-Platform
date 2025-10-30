package com.project.supplychain.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class SalesOrderLine {
    @Id
    @GeneratedValue
    private UUID id;
    private Integer quantity;
    private BigDecimal unitPrice;
    private boolean backorder;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "sales_order_id")
    private SalesOrder salesOrder;


}
