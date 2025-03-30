package com.benorim.carhov.dto.rideSchedule;

import com.benorim.carhov.enums.DayOfWeek;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class CreateRideScheduleDTO {
    
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;
    
    @NotNull(message = "Start latitude is required")
    private Double startLatitude;
    
    @NotNull(message = "Start longitude is required")
    private Double startLongitude;
    
    @NotNull(message = "End latitude is required")
    private Double endLatitude;
    
    @NotNull(message = "End longitude is required")
    private Double endLongitude;
    
    @NotEmpty(message = "At least one day of week must be selected")
    private List<DayOfWeek> dayList;
    
    @NotNull(message = "Departure time is required")
    private LocalTime departureTime;
    
    @NotNull(message = "Available seats is required")
    @Min(value = 1, message = "Available seats must be at least 1")
    private Integer availableSeats;
    
    private Boolean available = true;
}