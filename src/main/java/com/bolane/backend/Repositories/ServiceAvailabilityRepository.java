package com.bolane.backend.Repositories;

import com.bolane.backend.Entities.ServiceAvailability;
import com.bolane.backend.Entities.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ServiceAvailabilityRepository extends JpaRepository<ServiceAvailability, Integer> {
    List<ServiceAvailability> findByDateAndAvailableTrue(LocalDate date);
    List<ServiceAvailability> findByServiceAndAvailableTrue(ServiceEntity service);
    Optional<ServiceAvailability> findByServiceAndDate(ServiceEntity service, LocalDate date);
    List<ServiceAvailability> findByDateGreaterThanEqualAndAvailableTrue(LocalDate date);
}
