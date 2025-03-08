package com.benorim.carhov.dto.rideSchedule;

import com.benorim.carhov.enums.DayOfWeek;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class RideScheduleDTO {
    private Long id;
    private Long userId;
    private String userDisplayName;
    private Double startLatitude;
    private Double startLongitude;
    private Double endLatitude;
    private Double endLongitude;
    private List<DayOfWeek> dayList;
    private LocalTime departureTime;
    private Integer availableSeats;
    private Boolean available;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}