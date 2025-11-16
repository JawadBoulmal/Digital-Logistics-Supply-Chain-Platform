package com.project.supplychain.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBadRequest_returns_bad_request_response() {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getRequestURI()).thenReturn("/test");

        BadRequestException ex = new BadRequestException("bad things");

        ResponseEntity<ErrorResponse> resp = handler.handleBadRequest(ex, req);

    assertThat(resp.getStatusCode().value()).isEqualTo(400);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getMessage()).isEqualTo("bad things");
        assertThat(resp.getBody().getPath()).isEqualTo("/test");
    }

    @Test
    void handleValidationError_returns_field_message() {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getRequestURI()).thenReturn("/validate");

        MethodArgumentNotValidException ex = Mockito.mock(MethodArgumentNotValidException.class);
        BindingResult binding = Mockito.mock(BindingResult.class);
        FieldError fe = new FieldError("obj", "field", "must be present");

        Mockito.when(ex.getBindingResult()).thenReturn(binding);
        Mockito.when(binding.getFieldError()).thenReturn(fe);

        ResponseEntity<ErrorResponse> resp = handler.handleValidationError(ex, req);

    assertThat(resp.getStatusCode().value()).isEqualTo(400);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getMessage()).isEqualTo("must be present");
        assertThat(resp.getBody().getPath()).isEqualTo("/validate");
    }

    @Test
    void handleGeneralError_returns_500() {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getRequestURI()).thenReturn("/boom");

        Exception ex = new Exception("boom");

        ResponseEntity<ErrorResponse> resp = handler.handleGeneralError(ex, req);

    assertThat(resp.getStatusCode().value()).isEqualTo(500);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getMessage()).isEqualTo("boom");
    }
}
