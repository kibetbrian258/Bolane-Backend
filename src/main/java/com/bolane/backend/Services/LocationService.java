package com.bolane.backend.Services;


import com.bolane.backend.DTOs.LocationDTO;
import com.bolane.backend.Entities.Location;
import com.bolane.backend.Exceptions.ResourceNotFoundException;
import com.bolane.backend.Repositories.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LocationService {
    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public List<Location> getActiveLocations() {
        return locationRepository.findByActiveTrue();
    }

    public Location getLocationById(int id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
    }

    @Transactional
    public Location createLocation(LocationDTO locationDTO) {
        Location location = new Location();
        location.setName(locationDTO.getName());
        location.setAddress(locationDTO.getAddress());
        location.setDescription(locationDTO.getDescription());
        location.setActive(locationDTO.isActive());

        return locationRepository.save(location);
    }

    @Transactional
    public Location updateLocation(int id, LocationDTO locationDTO) {
        Location location = getLocationById(id);

        location.setName(locationDTO.getName());
        location.setAddress(locationDTO.getAddress());
        location.setDescription(locationDTO.getDescription());
        location.setActive(locationDTO.isActive());

        return locationRepository.save(location);
    }

    @Transactional
    public void deleteLocation(int id) {
        Location location = getLocationById(id);
        locationRepository.delete(location);
    }
}
