package com.bolane.backend.Controllers;

import com.bolane.backend.DTOs.BookingRequestDTO;
import com.bolane.backend.DTOs.BookingUpdateDTO;
import com.bolane.backend.Entities.Booking;
import com.bolane.backend.Services.BookingService;
import com.bolane.backend.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

    @PutMapping("/{bookingId}")
    public ResponseEntity<Booking> updateBooking(
            @PathVariable Integer bookingId,
            @Valid @RequestBody BookingUpdateDTO request) {
        // Get user ID from current authenticated user
        int userId = userService.getCurrentUser().getId();
        Booking updatedBooking = bookingService.updateBooking(bookingId, request, userId);
        return ResponseEntity.ok(updatedBooking);
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getUserBookings() {
        // Get user ID from current authenticated user
        int userId = userService.getCurrentUser().getId();
        List<Booking> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Integer bookingId) {
        // Get user ID from current authenticated user
        int userId = userService.getCurrentUser().getId();

        // Get the booking - this will verify the booking exists
        Booking booking = bookingService.getBookingById(bookingId);

        // Verify that the booking belongs to the current user
        if (booking.getUser().getId() != userId) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        return ResponseEntity.ok(booking);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> cancelUserBooking(@PathVariable Integer bookingId) {
        // Get user ID from current authenticated user
        int userId = userService.getCurrentUser().getId();
        bookingService.cancelUserBooking(bookingId, userId);
        return ResponseEntity.noContent().build();
    }
}