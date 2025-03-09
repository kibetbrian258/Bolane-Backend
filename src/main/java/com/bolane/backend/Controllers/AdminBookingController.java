package com.bolane.backend.Controllers;

import com.bolane.backend.Entities.Booking;
import com.bolane.backend.Repositories.ServiceAvailabilityRepository;
import com.bolane.backend.Repositories.ServiceRepository;
import com.bolane.backend.Services.BookingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/bookings")
@PreAuthorize("hasRole('ADMIN')")  // Additional security at controller level
public class AdminBookingController {
    private final BookingService bookingService;
    private final ServiceRepository serviceRepository;
    private final ServiceAvailabilityRepository serviceAvailabilityRepository;

    public AdminBookingController(
            BookingService bookingService,
            ServiceRepository serviceRepository,
            ServiceAvailabilityRepository serviceAvailabilityRepository) {
        this.bookingService = bookingService;
        this.serviceRepository = serviceRepository;
        this.serviceAvailabilityRepository = serviceAvailabilityRepository;
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Integer id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<Booking>> getBookingsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(bookingService.getBookingsByDate(date));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Integer id) {
        // Call the service method which handles both the booking deletion and
        // the decrement of the service availability count
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }
}