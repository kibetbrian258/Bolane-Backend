package com.bolane.backend.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LocationDTO {

    @NotBlank(message = "Location name is required!")
    private String name;

    private String address;

    private String description;

    private boolean active = true;
}
