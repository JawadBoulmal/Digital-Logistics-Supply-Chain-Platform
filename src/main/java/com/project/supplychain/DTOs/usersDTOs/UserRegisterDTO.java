package com.project.supplychain.DTOs.usersDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.supplychain.enums.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDTO {
    @NotBlank(message = "Email Cannot be empty")
    @Email(message = "Enter a valid email")
    private String email;

    @NotBlank(message = "Password Cannot be empty")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 3,message = "The name must be over then 3 chars .")
    private String name;

    @NotBlank(message = "Telephone cannot be empty")
    @Pattern(regexp = "^(\\+212|0)(6|7)\\d{8}$", message = "Telephone must be a valid Moroccan number")
    private String telephone;

    @NotBlank(message = "Address cannot be empty")
    private String address;

    private Roles role;
}
