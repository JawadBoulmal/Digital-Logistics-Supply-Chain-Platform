package com.project.supplychain.DTOs.salesOrderLineDTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderLineDTO {
    private UUID id;

    @NotNull(message = "quantity is required")
    @Min(value = 1, message = "quantity must be >= 1")
    private Integer quantity;

    @Min(value = 0, message = "unitPrice must be >= 0")
    private BigDecimal unitPrice;

    private boolean backorder;

    @NotNull(message = "productId is required")
    private UUID productId;

    @NotNull(message = "salesOrderId is required")
    private UUID salesOrderId;
}
