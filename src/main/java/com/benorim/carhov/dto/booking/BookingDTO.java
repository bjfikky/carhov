package com.benorim.carhov.dto.booking;

import com.benorim.carhov.enums.BookingStatus;
import com.benorim.carhov.enums.DayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private Long id;
    private Long rideScheduleId;
    private Long userId;
    private int seatsBooked;
    private List<DayOfWeek> bookedDays;
    private BookingStatus status;
    private LocalDateTime statusUpdateDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional useful information
    private String rideOwnerName;
    private String departureTime;
    private List<DayOfWeek> availableDays;
    private String startLocation;
    private String endLocation;
}