package com.bolane.backend.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRoleDTO {

    @NotBlank(message = "Role is required!")
    private String role;
}
