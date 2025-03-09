package com.bolane.backend.Controllers;

import com.bolane.backend.Entities.ServiceAvailability;
import com.bolane.backend.Services.ServiceAvailabilityService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/service-availability")
public class ServiceAvailabilityController {
    private final ServiceAvailabilityService serviceAvailabilityService;

    public ServiceAvailabilityController(ServiceAvailabilityService serviceAvailabilityService) {
        this.serviceAvailabilityService = serviceAvailabilityService;
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
}
