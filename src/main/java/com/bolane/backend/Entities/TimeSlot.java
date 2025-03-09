package com.bolane.backend.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;

@Entity
@Table(name = "time_slots")
@Data
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "service_availability_id", nullable = false)
    private ServiceAvailability serviceAvailability;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private int maxBookings = 3;

    @Column(nullable = false)
    private int currentBookings = 0;

    // This field maps to the "active" column
    @Column(name = "active", nullable = true)
    private boolean active = true;

    // Add this field to map to the "available" column in the database
    @Column(nullable = false)
    private boolean available = true;

    // Override the getter/setter to keep both fields in sync
    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
        this.active = available; // Keep active in sync with available
    }
}