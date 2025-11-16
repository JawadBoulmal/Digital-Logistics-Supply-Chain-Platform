package com.project.supplychain.controller;

import com.project.supplychain.DTOs.usersDTOs.UserLoginDTO;
import com.project.supplychain.DTOs.usersDTOs.UserRegisterDTO;
import com.project.supplychain.mappers.usersMappers.UserMapper;
import com.project.supplychain.services.Auth.AuthService;
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
class AuthControllerTest {

    @Mock
    AuthService authService;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    AuthController controller;

    @Test
    void register_returnsOk() {
        UserRegisterDTO dto = new UserRegisterDTO();
        HashMap<String, Object> map = new HashMap<>();
        map.put("ok", true);
        when(authService.register(any())).thenReturn(map);

        ResponseEntity<?> resp = controller.register(dto);

        assertThat(resp.getStatusCodeValue()).isEqualTo(200);
        assertThat(resp.getBody()).isEqualTo(map);
    }

    @Test
    void login_returnsOk() {
        UserLoginDTO dto = new UserLoginDTO();
        HashMap<String, Object> map = new HashMap<>();
        map.put("token", "abc");
        when(authService.login(any())).thenReturn(map);

        ResponseEntity<?> resp = controller.login(dto);

        assertThat(resp.getStatusCodeValue()).isEqualTo(200);
        assertThat(resp.getBody()).isEqualTo(map);
    }

    @Test
    void checkJWT_returnsOk() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("user", "u");
        when(authService.checkTheUser(any())).thenReturn(map);

        ResponseEntity<?> resp = controller.checkJWT("token");

        assertThat(resp.getStatusCodeValue()).isEqualTo(200);
        assertThat(resp.getBody()).isEqualTo(map);
    }
}
