package com.bolane.backend.Services;

import com.bolane.backend.DTOs.BookingRequestDTO;
import com.bolane.backend.Entities.Booking;
import com.bolane.backend.Entities.ServiceAvailability;
import com.bolane.backend.Entities.ServiceEntity;
import com.bolane.backend.Entities.TimeSlot;
import com.bolane.backend.Entities.User;
import com.bolane.backend.Exceptions.ResourceNotFoundException;
import com.bolane.backend.Exceptions.UnauthorizedException;
import com.bolane.backend.Repositories.BookingRepository;
import com.bolane.backend.Repositories.ServiceAvailabilityRepository;
import com.bolane.backend.Repositories.ServiceRepository;
import com.bolane.backend.Repositories.TimeSlotRepository;
import com.bolane.backend.Repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final ServiceAvailabilityRepository serviceAvailabilityRepository;
    private final TimeSlotRepository timeSlotRepository;

    public BookingService(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            ServiceRepository serviceRepository,
            ServiceAvailabilityRepository serviceAvailabilityRepository,
            TimeSlotRepository timeSlotRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.serviceAvailabilityRepository = serviceAvailabilityRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    @Transactional
    public Booking createBooking(BookingRequestDTO request, int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // Find the service by name
        ServiceEntity service = serviceRepository.findByName(request.getServiceRequired())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found: " + request.getServiceRequired()));

        // Validate service availability for the specific date
        ServiceAvailability availability = serviceAvailabilityRepository
                .findByServiceAndDate(service, request.getDate())
                .orElseThrow(() -> new IllegalStateException("Service is not available on selected date"));

        if (!availability.isAvailable()) {
            throw new IllegalStateException("Service is not available on selected date");
        }

        // Check if the requested time is within an available time slot
        LocalTime requestedTime = request.getTime();
        Optional<TimeSlot> availableTimeSlot = availability.getTimeSlots().stream()
                .filter(ts -> ts.isAvailable() &&
                        (requestedTime.equals(ts.getStartTime()) ||
                                (requestedTime.isAfter(ts.getStartTime()) && requestedTime.isBefore(ts.getEndTime()))))
                .findFirst();

        if (availableTimeSlot.isEmpty()) {
            throw new IllegalStateException("Selected time is not available for booking");
        }

        TimeSlot timeSlot = availableTimeSlot.get();

        if (timeSlot.getCurrentBookings() >= timeSlot.getMaxBookings()) {
            throw new IllegalStateException("Maximum bookings reached for this time slot. Please select another time");
        }

        // Create booking
        Booking booking = new Booking();
        booking.setLocation(request.getLocation());
        booking.setTime(request.getTime());
        booking.setDate(request.getDate());
        booking.setServiceRequired(request.getServiceRequired());
        booking.setUser(user);

        // Save the time slot ID in the booking
        // Note: We need to add a timeSlotId field to the Booking entity
        // booking.setTimeSlotId(timeSlot.getId());

        // Increment booking count for the specific time slot
        timeSlot.setCurrentBookings(timeSlot.getCurrentBookings() + 1);
        timeSlotRepository.save(timeSlot);

        // Also increment the overall availability counter
        availability.setCurrentBookings(availability.getCurrentBookings() + 1);
        serviceAvailabilityRepository.save(availability);

        return bookingRepository.save(booking);
    }

    public List<Booking> getUserBookings(Integer userId) {
        return bookingRepository.findByUserId(userId);
    }

    // Admin Methods

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(Integer id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
    }

    public List<Booking> getBookingsByDate(LocalDate date) {
        return bookingRepository.findByDate(date);
    }

    @Transactional
    public void cancelBooking(Integer id) {
        Booking booking = getBookingById(id);

        // Find the service by name
        ServiceEntity service = serviceRepository.findByName(booking.getServiceRequired())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found: " + booking.getServiceRequired()));

        // Find service availability for this specific date
        ServiceAvailability availability = serviceAvailabilityRepository
                .findByServiceAndDate(service, booking.getDate())
                .orElse(null);

        if (availability != null) {
            // Find the relevant time slot
            LocalTime bookingTime = booking.getTime();
            Optional<TimeSlot> timeSlot = availability.getTimeSlots().stream()
                    .filter(ts -> bookingTime.equals(ts.getStartTime()) ||
                            (bookingTime.isAfter(ts.getStartTime()) && bookingTime.isBefore(ts.getEndTime())))
                    .findFirst();

            // Update time slot booking count if found
            timeSlot.ifPresent(ts -> {
                if (ts.getCurrentBookings() > 0) {
                    ts.setCurrentBookings(ts.getCurrentBookings() - 1);
                    timeSlotRepository.save(ts);
                }
            });

            // Update overall availability counter
            if (availability.getCurrentBookings() > 0) {
                availability.setCurrentBookings(availability.getCurrentBookings() - 1);
                serviceAvailabilityRepository.save(availability);
            }
        }

        bookingRepository.delete(booking);
    }

    @Transactional
    public void cancelUserBooking(Integer bookingId, int userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        // Verify that the booking belongs to the user
        if (booking.getUser().getId() != userId) {
            throw new UnauthorizedException("You are not authorized to cancel this booking");
        }

        // Find the service and update time slot availability
        try {
            ServiceEntity service = serviceRepository.findByName(booking.getServiceRequired())
                    .orElseThrow(() -> new ResourceNotFoundException("Service not found: " + booking.getServiceRequired()));

            ServiceAvailability availability = serviceAvailabilityRepository
                    .findByServiceAndDate(service, booking.getDate())
                    .orElse(null);

            if (availability != null) {
                LocalTime bookingTime = booking.getTime();
                Optional<TimeSlot> timeSlot = availability.getTimeSlots().stream()
                        .filter(ts -> bookingTime.equals(ts.getStartTime()) ||
                                (bookingTime.isAfter(ts.getStartTime()) && bookingTime.isBefore(ts.getEndTime())))
                        .findFirst();

                // Update time slot booking count if found
                timeSlot.ifPresent(ts -> {
                    if (ts.getCurrentBookings() > 0) {
                        ts.setCurrentBookings(ts.getCurrentBookings() - 1);
                        timeSlotRepository.save(ts);
                    }
                });

                // Update overall availability counter
                if (availability.getCurrentBookings() > 0) {
                    availability.setCurrentBookings(availability.getCurrentBookings() - 1);
                    serviceAvailabilityRepository.save(availability);
                }
            }
        } catch (Exception e) {
            // Log error but continue with deletion
            System.err.println("Error updating service availability on booking cancellation: " + e.getMessage());
        }

        bookingRepository.delete(booking);
    }
}