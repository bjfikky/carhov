package com.benorim.carhov.dto.booking;

import com.benorim.carhov.enums.DayOfWeek;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingDTO {
    @NotNull(message = "Ride schedule ID is required")
    private Long rideScheduleId;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Number of seats is required")
    @Min(value = 1, message = "At least one seat must be booked")
    private Integer seatsBooked;
    
    @NotEmpty(message = "At least one day must be selected")
    private List<DayOfWeek> bookedDays;
}