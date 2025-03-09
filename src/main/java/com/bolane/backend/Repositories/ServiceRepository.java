package com.bolane.backend.Repositories;

import com.bolane.backend.Entities.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Integer> {
    List<ServiceEntity> findByActiveTrue();
    Optional<ServiceEntity> findByNameAndActiveTrue(String name);
    Optional<ServiceEntity> findByName(String name);
}
