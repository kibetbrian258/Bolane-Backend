package com.bolane.backend.Controllers;

import com.bolane.backend.Entities.ServiceEntity;
import com.bolane.backend.Services.ServiceEntityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {
    private final ServiceEntityService serviceEntityService;

    public ServiceController(ServiceEntityService serviceEntityService) {
        this.serviceEntityService = serviceEntityService;
    }

    @GetMapping
    public ResponseEntity<List<ServiceEntity>> getActiveServices() {
        return ResponseEntity.ok(serviceEntityService.getActiveServices());
    }
}
