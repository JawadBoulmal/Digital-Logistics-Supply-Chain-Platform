package com.project.supplychain.mappers.usersMappers;

import com.project.supplychain.DTOs.usersDTOs.UserRegisterDTO;
import com.project.supplychain.models.user.Admin;
import com.project.supplychain.models.user.Client;
import com.project.supplychain.models.user.User;
import com.project.supplychain.models.user.WarehouseManager;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(UserRegisterDTO dto) {
        return switch (dto.getRole()) {
            case CLIENT -> Client.builder()
                    .email(dto.getEmail())
                    .password(dto.getPassword())
                    .name(dto.getName())
                    .telephone(dto.getTelephone())
                    .address(dto.getAddress())
                    .role(dto.getRole())
                    .build();

            case ADMIN -> Admin.builder()
                    .email(dto.getEmail())
                    .password(dto.getPassword())
                    .active(true)
                    .role(dto.getRole())
                    .build();

            case WAREHOUSE_MANAGER -> WarehouseManager.builder()
                    .email(dto.getEmail())
                    .password(dto.getPassword())
                    .active(true)
                    .role(dto.getRole())
                    .build();
        };
    }
}
