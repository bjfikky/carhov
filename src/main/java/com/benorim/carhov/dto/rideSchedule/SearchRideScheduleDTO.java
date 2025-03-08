package com.benorim.carhov.dto.rideSchedule;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SearchRideScheduleDTO {
    
    @NotNull(message = "Start latitude is required")
    private Double startLatitude;
    
    @NotNull(message = "Start longitude is required")
    private Double startLongitude;
    
    @NotNull(message = "End latitude is required")
    private Double endLatitude;
    
    @NotNull(message = "End longitude is required")
    private Double endLongitude;
    
    @NotNull(message = "Search radius in miles is required")
    @Positive(message = "Search radius must be positive")
    private Double radiusInMiles = 5.0; // Default 5 miles radius
}