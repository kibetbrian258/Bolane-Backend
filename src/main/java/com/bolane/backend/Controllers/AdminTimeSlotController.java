package com.bolane.backend.Controllers;

import com.bolane.backend.DTOs.TimeSlotDTO;
import com.bolane.backend.Entities.TimeSlot;
import com.bolane.backend.Services.ServiceAvailabilityService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/time-slots")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTimeSlotController {
    private final ServiceAvailabilityService serviceAvailabilityService;

    public AdminTimeSlotController(ServiceAvailabilityService serviceAvailabilityService) {
        this.serviceAvailabilityService = serviceAvailabilityService;
    }

    @PostMapping("/{availabilityId}")
    public ResponseEntity<TimeSlot> addTimeSlot(
            @PathVariable int availabilityId,
            @Valid @RequestBody TimeSlotDTO timeSlotDTO) {
        return ResponseEntity.ok(serviceAvailabilityService.addTimeSlot(availabilityId, timeSlotDTO));
    }

    @PutMapping("/{timeSlotId}")
    public ResponseEntity<TimeSlot> updateTimeSlot(
            @PathVariable int timeSlotId,
            @Valid @RequestBody TimeSlotDTO timeSlotDTO) {
        return ResponseEntity.ok(serviceAvailabilityService.updateTimeSlot(timeSlotId, timeSlotDTO));
    }

    @DeleteMapping("/{timeSlotId}")
    public ResponseEntity<Void> deleteTimeSlot(@PathVariable int timeSlotId) {
        serviceAvailabilityService.deleteTimeSlot(timeSlotId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{timeSlotId}/toggle")
    public ResponseEntity<TimeSlot> toggleTimeSlotAvailability(@PathVariable int timeSlotId) {
        return ResponseEntity.ok(serviceAvailabilityService.toggleTimeSlotAvailability(timeSlotId));
    }
}