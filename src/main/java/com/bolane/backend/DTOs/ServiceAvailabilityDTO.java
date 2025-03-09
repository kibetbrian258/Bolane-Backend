package com.bolane.backend.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class ServiceAvailabilityDTO {

    @NotNull(message = "Service ID is required")
    private Integer serviceId;

    @NotNull(message = "Service ID is required")
    private LocalDate date;

    private boolean available = true;

    @Min(value = 1, message = "Maximum bookings must be at least 1")
    private int maxBookings = 5;

    private List<TimeSlotDTO> timeSlots = new ArrayList<>();

}
