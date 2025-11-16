package com.project.supplychain.controller;

import com.project.supplychain.DTOs.paymentDTOs.PaymentDto;
import com.project.supplychain.services.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    PaymentService service;

    @InjectMocks
    Payment controller;

    @Test
    void makePayment_returns_expected_map() {
        when(service.makePayment(any())).thenReturn(new HashMap<>());

        HashMap<String, Object> resp = controller.makePayment(new PaymentDto());

        assertThat(resp).isNotNull();
    }
}
