package com.project.supplychain.DTOs.usersDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO {
    @NotBlank(message = "Email Cannot be empty")
    @Email(message = "Enter a valid email")
    private String email;

    @NotBlank(message = "Password Cannot be empty")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
