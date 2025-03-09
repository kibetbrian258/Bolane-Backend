package com.bolane.backend.Repositories;

import com.bolane.backend.Entities.ServiceAvailability;
import com.bolane.backend.Entities.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {
    List<TimeSlot> findByServiceAvailabilityAndAvailableTrue(ServiceAvailability serviceAvailability);

    Optional<TimeSlot> findByServiceAvailabilityAndStartTimeAndEndTime(
            ServiceAvailability serviceAvailability,
            LocalTime startTime,
            LocalTime endTime
    );

    List<TimeSlot> findByServiceAvailabilityId(int serviceAvailabilityId);
}