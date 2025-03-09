package com.bolane.backend.Repositories;

import com.bolane.backend.Entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Integer> {
    List<Location> findByActiveTrue();
}
