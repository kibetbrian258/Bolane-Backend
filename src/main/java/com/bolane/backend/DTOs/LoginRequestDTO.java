package com.bolane.backend.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "Email is required!")
    @Email(message = "Please provide a valid email address!")
    private String email;

    @NotBlank(message = "Password is required!")
    private String password;
}
