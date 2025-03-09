package com.bolane.backend.Controllers;

import com.bolane.backend.DTOs.ServiceAvailabilityDTO;
import com.bolane.backend.Entities.ServiceAvailability;
import com.bolane.backend.Services.ServiceAvailabilityService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/service-availability")
@PreAuthorize("hasRole('ADMIN')")
public class AdminServiceAvailabilityController {
    private final ServiceAvailabilityService serviceAvailabilityService;

    public AdminServiceAvailabilityController(ServiceAvailabilityService serviceAvailabilityService) {
        this.serviceAvailabilityService = serviceAvailabilityService;
    }

    @GetMapping
    public ResponseEntity<List<ServiceAvailability>> getAllServiceAvailabilities() {
        return ResponseEntity.ok(serviceAvailabilityService.getAllServiceAvailabilities());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceAvailability> getServiceAvailabilityById(@PathVariable int id) {
        return ResponseEntity.ok(serviceAvailabilityService.getServiceAvailabilityById(id));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<ServiceAvailability>> getAvailableServicesForDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(serviceAvailabilityService.getAvailableServicesForDate(date));
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<ServiceAvailability>> getAvailableDatesForService(@PathVariable int serviceId) {
        return ResponseEntity.ok(serviceAvailabilityService.getAvailableDatesForService(serviceId));
    }

    @PostMapping
    public ResponseEntity<ServiceAvailability> createServiceAvailability(@Valid @RequestBody ServiceAvailabilityDTO request) {
        return ResponseEntity.ok(serviceAvailabilityService.createServiceAvailability(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceAvailability> updateServiceAvailability(
            @PathVariable int id, @Valid @RequestBody ServiceAvailabilityDTO request) {
        return ResponseEntity.ok(serviceAvailabilityService.updateServiceAvailability(id, request));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<ServiceAvailability> toggleAvailability(@PathVariable int id) {
        return ResponseEntity.ok(serviceAvailabilityService.toggleAvailability(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServiceAvailability(@PathVariable int id) {
        serviceAvailabilityService.deleteServiceAvailability(id);
        return ResponseEntity.noContent().build();
    }
}
