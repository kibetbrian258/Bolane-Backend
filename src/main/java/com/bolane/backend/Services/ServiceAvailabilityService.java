package com.bolane.backend.Services;

import com.bolane.backend.DTOs.ServiceAvailabilityDTO;
import com.bolane.backend.DTOs.TimeSlotDTO;
import com.bolane.backend.Entities.ServiceAvailability;
import com.bolane.backend.Entities.ServiceEntity;
import com.bolane.backend.Entities.TimeSlot;
import com.bolane.backend.Exceptions.ResourceNotFoundException;
import com.bolane.backend.Repositories.ServiceAvailabilityRepository;
import com.bolane.backend.Repositories.ServiceRepository;
import com.bolane.backend.Repositories.TimeSlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceAvailabilityService {
    private final ServiceAvailabilityRepository serviceAvailabilityRepository;
    private final ServiceRepository serviceRepository;
    private final TimeSlotRepository timeSlotRepository;

    public ServiceAvailabilityService(
            ServiceAvailabilityRepository serviceAvailabilityRepository,
            ServiceRepository serviceRepository,
            TimeSlotRepository timeSlotRepository) {
        this.serviceAvailabilityRepository = serviceAvailabilityRepository;
        this.serviceRepository = serviceRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    public List<ServiceAvailability> getAllServiceAvailabilities() {
        return serviceAvailabilityRepository.findAll();
    }

    public List<ServiceAvailability> getAvailableServicesForDate(LocalDate date) {
        return serviceAvailabilityRepository.findByDateAndAvailableTrue(date);
    }

    public List<ServiceAvailability> getAvailableDatesForService(int serviceId) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceId));
        return serviceAvailabilityRepository.findByServiceAndAvailableTrue(service);
    }

    public ServiceAvailability getServiceAvailabilityById(int id) {
        return serviceAvailabilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service availability not found with id: " + id));
    }

    @Transactional
    public ServiceAvailability createServiceAvailability(ServiceAvailabilityDTO dto) {
        ServiceEntity service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + dto.getServiceId()));

        // Check if entry already exists for this service and date
        serviceAvailabilityRepository.findByServiceAndDate(service, dto.getDate()).ifPresent(availability -> {
            throw new IllegalStateException("Service availability already exists for this date");
        });

        ServiceAvailability availability = new ServiceAvailability();
        availability.setService(service);
        availability.setDate(dto.getDate());
        availability.setAvailable(dto.isAvailable());
        availability.setMaxBookings(dto.getMaxBookings());
        availability.setCurrentBookings(0);

        // Save the availability first to get an ID
        availability = serviceAvailabilityRepository.save(availability);

        // Add time slots if provided
        if (dto.getTimeSlots() != null && !dto.getTimeSlots().isEmpty()) {
            for (TimeSlotDTO timeSlotDTO : dto.getTimeSlots()) {
                TimeSlot timeSlot = new TimeSlot();
                timeSlot.setServiceAvailability(availability);
                timeSlot.setStartTime(timeSlotDTO.getStartTime());
                timeSlot.setEndTime(timeSlotDTO.getEndTime());
                timeSlot.setMaxBookings(timeSlotDTO.getMaxBookings());
                timeSlot.setAvailable(timeSlotDTO.isAvailable());
                timeSlot.setCurrentBookings(0);

                timeSlotRepository.save(timeSlot);
            }
        }

        // Reload the availability with time slots
        return serviceAvailabilityRepository.findById(availability.getId()).orElse(availability);
    }

    @Transactional
    public ServiceAvailability updateServiceAvailability(int id, ServiceAvailabilityDTO dto) {
        ServiceAvailability availability = getServiceAvailabilityById(id);
        ServiceEntity service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + dto.getServiceId()));

        availability.setService(service);
        availability.setDate(dto.getDate());
        availability.setAvailable(dto.isAvailable());
        availability.setMaxBookings(dto.getMaxBookings());

        // Save updated availability
        availability = serviceAvailabilityRepository.save(availability);

        // Handle time slots update
        if (dto.getTimeSlots() != null) {
            // Get existing time slots
            List<TimeSlot> existingTimeSlots = timeSlotRepository.findByServiceAvailabilityId(id);

            // Keep track of processed time slots to determine which ones to remove
            List<Integer> processedTimeSlotIds = new ArrayList<>();

            // Update existing or add new time slots
            for (TimeSlotDTO timeSlotDTO : dto.getTimeSlots()) {
                if (timeSlotDTO.getId() != null) {
                    // Update existing time slot
                    TimeSlot existingTimeSlot = existingTimeSlots.stream()
                            .filter(ts -> ts.getId() == timeSlotDTO.getId())
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Time slot not found with id: " + timeSlotDTO.getId()));

                    existingTimeSlot.setStartTime(timeSlotDTO.getStartTime());
                    existingTimeSlot.setEndTime(timeSlotDTO.getEndTime());
                    existingTimeSlot.setMaxBookings(timeSlotDTO.getMaxBookings());
                    existingTimeSlot.setAvailable(timeSlotDTO.isAvailable());

                    timeSlotRepository.save(existingTimeSlot);
                    processedTimeSlotIds.add(existingTimeSlot.getId());
                } else {
                    // Add new time slot
                    TimeSlot newTimeSlot = new TimeSlot();
                    newTimeSlot.setServiceAvailability(availability);
                    newTimeSlot.setStartTime(timeSlotDTO.getStartTime());
                    newTimeSlot.setEndTime(timeSlotDTO.getEndTime());
                    newTimeSlot.setMaxBookings(timeSlotDTO.getMaxBookings());
                    newTimeSlot.setAvailable(timeSlotDTO.isAvailable());
                    newTimeSlot.setCurrentBookings(0);

                    TimeSlot savedTimeSlot = timeSlotRepository.save(newTimeSlot);
                    processedTimeSlotIds.add(savedTimeSlot.getId());
                }
            }

            // Remove time slots that were not processed (i.e., they were removed in the update)
            existingTimeSlots.stream()
                    .filter(ts -> !processedTimeSlotIds.contains(ts.getId()))
                    .forEach(timeSlotRepository::delete);
        }

        // Reload the availability with updated time slots
        return serviceAvailabilityRepository.findById(id).orElse(availability);
    }

    @Transactional
    public void deleteServiceAvailability(int id) {
        ServiceAvailability availability = getServiceAvailabilityById(id);

        // Delete all associated time slots first
        List<TimeSlot> timeSlots = timeSlotRepository.findByServiceAvailabilityId(id);
        timeSlotRepository.deleteAll(timeSlots);

        // Then delete the availability
        serviceAvailabilityRepository.delete(availability);
    }

    @Transactional
    public ServiceAvailability toggleAvailability(int id) {
        ServiceAvailability availability = getServiceAvailabilityById(id);
        availability.setAvailable(!availability.isAvailable());
        return serviceAvailabilityRepository.save(availability);
    }

    @Transactional
    public TimeSlot addTimeSlot(int availabilityId, TimeSlotDTO timeSlotDTO) {
        ServiceAvailability availability = getServiceAvailabilityById(availabilityId);

        // Check for overlapping time slots
        List<TimeSlot> existingTimeSlots = timeSlotRepository.findByServiceAvailabilityId(availabilityId);
        for (TimeSlot existingSlot : existingTimeSlots) {
            if (isTimeSlotOverlapping(existingSlot, timeSlotDTO.getStartTime(), timeSlotDTO.getEndTime())) {
                throw new IllegalStateException("Time slot overlaps with an existing time slot");
            }
        }

        // Ensure the start time comes before the end time
        if (timeSlotDTO.getStartTime().isAfter(timeSlotDTO.getEndTime())) {
            throw new IllegalStateException("Start time must be before end time");
        }

        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setServiceAvailability(availability);
        timeSlot.setStartTime(timeSlotDTO.getStartTime());
        timeSlot.setEndTime(timeSlotDTO.getEndTime());
        timeSlot.setMaxBookings(timeSlotDTO.getMaxBookings());

        // Explicitly set the available field to prevent null values
        timeSlot.setAvailable(timeSlotDTO.isAvailable());
        timeSlot.setCurrentBookings(0);

        return timeSlotRepository.save(timeSlot);
    }

    @Transactional
    public TimeSlot updateTimeSlot(int timeSlotId, TimeSlotDTO timeSlotDTO) {
        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new ResourceNotFoundException("Time slot not found with id: " + timeSlotId));

        // Check for overlapping time slots (excluding the current one)
        List<TimeSlot> existingTimeSlots = timeSlotRepository.findByServiceAvailabilityId(
                timeSlot.getServiceAvailability().getId());

        for (TimeSlot existingSlot : existingTimeSlots) {
            if (existingSlot.getId() != timeSlotId &&
                    isTimeSlotOverlapping(existingSlot, timeSlotDTO.getStartTime(), timeSlotDTO.getEndTime())) {
                throw new IllegalStateException("Time slot overlaps with an existing time slot");
            }
        }

        timeSlot.setStartTime(timeSlotDTO.getStartTime());
        timeSlot.setEndTime(timeSlotDTO.getEndTime());
        timeSlot.setMaxBookings(timeSlotDTO.getMaxBookings());
        timeSlot.setAvailable(timeSlotDTO.isAvailable());

        return timeSlotRepository.save(timeSlot);
    }

    @Transactional
    public void deleteTimeSlot(int timeSlotId) {
        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new ResourceNotFoundException("Time slot not found with id: " + timeSlotId));

        if (timeSlot.getCurrentBookings() > 0) {
            throw new IllegalStateException("Cannot delete time slot with existing bookings");
        }

        timeSlotRepository.delete(timeSlot);
    }

    @Transactional
    public TimeSlot toggleTimeSlotAvailability(int timeSlotId) {
        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new ResourceNotFoundException("Time slot not found with id: " + timeSlotId));

        timeSlot.setAvailable(!timeSlot.isAvailable());
        return timeSlotRepository.save(timeSlot);
    }

    // Utility method to check if a time slot overlaps with another
    private boolean isTimeSlotOverlapping(TimeSlot existingSlot, LocalTime startTime, LocalTime endTime) {
        return (startTime.isBefore(existingSlot.getEndTime()) && endTime.isAfter(existingSlot.getStartTime()));
    }

    // Method to increment booking count for a specific time slot
    @Transactional
    public boolean incrementTimeSlotBookingCount(int timeSlotId) {
        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new ResourceNotFoundException("Time slot not found with id: " + timeSlotId));

        if (!timeSlot.isAvailable()) {
            return false;
        }

        if (timeSlot.getCurrentBookings() >= timeSlot.getMaxBookings()) {
            return false;
        }

        timeSlot.setCurrentBookings(timeSlot.getCurrentBookings() + 1);

        // Also increment the parent service availability counter
        ServiceAvailability availability = timeSlot.getServiceAvailability();
        availability.setCurrentBookings(availability.getCurrentBookings() + 1);

        timeSlotRepository.save(timeSlot);
        serviceAvailabilityRepository.save(availability);

        return true;
    }

    // Method to decrement booking count for a specific time slot
    @Transactional
    public boolean decrementTimeSlotBookingCount(int timeSlotId) {
        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new ResourceNotFoundException("Time slot not found with id: " + timeSlotId));

        if (timeSlot.getCurrentBookings() <= 0) {
            return false;
        }

        timeSlot.setCurrentBookings(timeSlot.getCurrentBookings() - 1);

        // Also decrement the parent service availability counter
        ServiceAvailability availability = timeSlot.getServiceAvailability();
        if (availability.getCurrentBookings() > 0) {
            availability.setCurrentBookings(availability.getCurrentBookings() - 1);
        }

        timeSlotRepository.save(timeSlot);
        serviceAvailabilityRepository.save(availability);

        return true;
    }
}