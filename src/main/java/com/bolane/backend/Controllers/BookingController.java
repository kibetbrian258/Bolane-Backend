package com.bolane.backend.Controllers;

import com.bolane.backend.DTOs.BookingRequestDTO;
import com.bolane.backend.Entities.Booking;
import com.bolane.backend.Services.BookingService;
import com.bolane.backend.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final UserService userService;

    public BookingController(BookingService bookingService, UserService userService) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(@Valid @RequestBody BookingRequestDTO request) {
        // Get user ID from current authenticated user
        int userId = userService.getCurrentUser().getId();
        Booking booking = bookingService.createBooking(request, userId);
        return ResponseEntity.ok(booking);
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getUserBookings() {
        // Get user ID from current authenticated user
        int userId = userService.getCurrentUser().getId();
        List<Booking> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> cancelUserBooking(@PathVariable Integer bookingId) {
        // Get user ID from current authenticated user
        int userId = userService.getCurrentUser().getId();
        bookingService.cancelUserBooking(bookingId, userId);
        return ResponseEntity.noContent().build();
    }
}