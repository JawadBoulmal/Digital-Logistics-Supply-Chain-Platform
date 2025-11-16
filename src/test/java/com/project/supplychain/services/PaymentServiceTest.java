package com.project.supplychain.services;

import com.project.supplychain.DTOs.paymentDTOs.PaymentDto;
import com.project.supplychain.enums.OrderStatus;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.models.SalesOrder;
import com.project.supplychain.repositories.SalesOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void makePayment_success_transitionsToShipped() {
        UUID id = UUID.randomUUID();
        PaymentDto dto = PaymentDto.builder().salesOrderId(id.toString()).build();

        SalesOrder order = new SalesOrder();
        order.setId(id);
        order.setStatus(OrderStatus.RESERVED);

        when(salesOrderRepository.findById(id)).thenReturn(Optional.of(order));
        when(salesOrderRepository.save(order)).thenReturn(order);

        var res = paymentService.makePayment(dto);

        assertThat(res).containsEntry("message", "The order has been paid successfully");
        assertThat(res).containsEntry("status", "SHIPPED");
        verify(salesOrderRepository).save(order);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    void makePayment_orderNotFound_throws() {
        UUID id = UUID.randomUUID();
        PaymentDto dto = PaymentDto.builder().salesOrderId(id.toString()).build();
        when(salesOrderRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> paymentService.makePayment(dto)).isInstanceOf(BadRequestException.class).hasMessageContaining("Sales order not found");
    }

    @Test
    void makePayment_whenOrderNotReserved_throws() {
        UUID id = UUID.randomUUID();
        PaymentDto dto = PaymentDto.builder().salesOrderId(id.toString()).build();

        SalesOrder order = new SalesOrder();
        order.setId(id);
        order.setStatus(OrderStatus.CREATED);

        when(salesOrderRepository.findById(id)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> paymentService.makePayment(dto)).isInstanceOf(BadRequestException.class).hasMessageContaining("Order is not in RESERVED status");
    }
}
