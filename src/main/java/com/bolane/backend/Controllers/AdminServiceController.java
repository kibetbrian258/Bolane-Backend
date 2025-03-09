package com.bolane.backend.Controllers;

import com.bolane.backend.DTOs.ServiceDTO;
import com.bolane.backend.Entities.ServiceEntity;
import com.bolane.backend.Services.ServiceEntityService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/services")
@PreAuthorize("hasRole('ADMIN')") // Additional security at controller level
public class AdminServiceController {

    private final ServiceEntityService serviceEntityService;

    public AdminServiceController(ServiceEntityService serviceEntityService) {
        this.serviceEntityService = serviceEntityService;
    }

    @GetMapping
    public ResponseEntity<List<ServiceEntity>> getAllServices() {
        return ResponseEntity.ok(serviceEntityService.getAllServices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceEntity> getServiceById(@PathVariable int id) {
        return ResponseEntity.ok(serviceEntityService.getServiceById(id));
    }

    @PostMapping
    public ResponseEntity<ServiceEntity> createService(@Valid @RequestBody ServiceDTO request) {
        return ResponseEntity.ok(serviceEntityService.createService(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceEntity> updateService(@PathVariable int id, @Valid @RequestBody ServiceDTO request) {
        return ResponseEntity.ok(serviceEntityService.updateService(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable int id) {
        serviceEntityService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}