package com.project.supplychain.DTOs.usersDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.supplychain.enums.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String email;
    private String name;
    private String telephone;
    private String address;
    private Roles role;
}
