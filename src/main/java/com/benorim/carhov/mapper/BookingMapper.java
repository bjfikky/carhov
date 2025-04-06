package com.benorim.carhov.mapper;

import com.benorim.carhov.dto.booking.BookingDTO;
import com.benorim.carhov.entity.Booking;
import com.benorim.carhov.entity.RideSchedule;
import com.benorim.carhov.enums.DayOfWeek;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Booking entity and BookingDTO
 */
public class BookingMapper {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    /**
     * Convert a Booking entity to a BookingDTO
     *
     * @param booking The booking entity to convert
     * @return The resulting BookingDTO
     */
    public static BookingDTO toDTO(Booking booking) {
        if (booking == null) {
            return null;
        }
        
        RideSchedule rideSchedule = booking.getRideSchedule();
        
        String startLocation = String.format("%.6f, %.6f", 
                rideSchedule.getStartLatitude(), 
                rideSchedule.getStartLongitude());
        
        String endLocation = String.format("%.6f, %.6f", 
                rideSchedule.getEndLatitude(), 
                rideSchedule.getEndLongitude());
        
        String departureTime = rideSchedule.getDepartureTime() != null 
                ? rideSchedule.getDepartureTime().format(TIME_FORMATTER) 
                : "";
        
        // Convert bookedDays string to enum list
        List<DayOfWeek> bookedDays = convertStringToDayList(booking.getBookedDays());
        
        // Get available days from the ride schedule
        List<DayOfWeek> availableDays = rideSchedule.getDayList();
        
        return BookingDTO.builder()
                .id(booking.getId())
                .rideScheduleId(booking.getRideSchedule().getId())
                .userId(booking.getCarHovUser().getId())
                .seatsBooked(booking.getSeatsBooked())
                .bookedDays(bookedDays)
                .status(booking.getStatus())
                .statusUpdateDate(booking.getStatusUpdateDate())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .rideOwnerName(rideSchedule.getUser().getDisplayName())
                .departureTime(departureTime)
                .availableDays(availableDays)
                .startLocation(startLocation)
                .endLocation(endLocation)
                .build();
    }
    
    /**
     * Convert a list of DayOfWeek enums to a comma-separated string
     *
     * @param days List of DayOfWeek enums
     * @return Comma-separated string of day names
     */
    public static String convertDayListToString(List<DayOfWeek> days) {
        if (days == null || days.isEmpty()) {
            return "";
        }
        return days.stream()
                .map(DayOfWeek::name)
                .collect(Collectors.joining(","));
    }
    
    /**
     * Convert a comma-separated string of day names to a list of DayOfWeek enums
     *
     * @param daysString Comma-separated string of day names
     * @return List of DayOfWeek enums
     */
    public static List<DayOfWeek> convertStringToDayList(String daysString) {
        if (daysString == null || daysString.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(daysString.split(","))
                .map(DayOfWeek::valueOf)
                .collect(Collectors.toList());
    }
}