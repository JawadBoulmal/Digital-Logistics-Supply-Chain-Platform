package com.project.supplychain.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    UserController controller;

    @Test
    void hello_returnsHelloWorld() {
        ResponseEntity<?> resp = controller.hello();
        assertThat(resp.getStatusCodeValue()).isEqualTo(200);
        Object body = resp.getBody();
        assertThat(body).isInstanceOf(Map.class);
        Map<?, ?> map = (Map<?, ?>) body;
        assertThat(map.get("message")).isEqualTo("Hello World");
    }
}
