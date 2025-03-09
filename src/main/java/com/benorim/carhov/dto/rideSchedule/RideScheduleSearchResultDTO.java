package com.benorim.carhov.dto.rideSchedule;

import com.benorim.carhov.enums.DayOfWeek;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class RideScheduleSearchResultDTO {
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
    
    // Search result specific fields
    private Double startPointDistanceInMiles;
    private Double endPointDistanceInMiles;
    private Double totalDistanceInMiles;
}