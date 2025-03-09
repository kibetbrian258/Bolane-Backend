package com.bolane.backend.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingUpdateDTO {
    @NotBlank(message = "Location is required!")
    private String location;

    @NotNull(message = "Time is required!")
    private LocalTime time;

    @NotNull(message = "Date is required!")
    private LocalDate date;

    @NotBlank(message = "Service field is required!")
    private String serviceRequired;
}