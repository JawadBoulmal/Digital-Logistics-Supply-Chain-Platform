package com.project.supplychain.DTOs.salesOrderDTOs;

import com.project.supplychain.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderDTO {
    private UUID id;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime reservedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;

    private UUID shipmentId;

    @NotNull(message = "clientId is required")
    private UUID clientId;

    private UUID warehouseId;
}
