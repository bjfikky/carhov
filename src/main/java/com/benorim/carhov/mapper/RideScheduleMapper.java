package com.benorim.carhov.mapper;

import com.benorim.carhov.dto.rideSchedule.CreateRideScheduleDTO;
import com.benorim.carhov.dto.rideSchedule.RideScheduleDTO;
import com.benorim.carhov.dto.rideSchedule.RideScheduleSearchResultDTO;
import com.benorim.carhov.dto.rideSchedule.SearchRideScheduleDTO;
import com.benorim.carhov.dto.rideSchedule.UpdateRideScheduleDTO;
import com.benorim.carhov.util.GeoUtils;
import com.benorim.carhov.entity.CarHovUser;
import com.benorim.carhov.entity.RideSchedule;

public class RideScheduleMapper {

    public static RideSchedule toEntity(CreateRideScheduleDTO dto, CarHovUser user) {
        RideSchedule rideSchedule = new RideSchedule();
        
        rideSchedule.setUser(user);
        rideSchedule.setStartLatitude(dto.getStartLatitude());
        rideSchedule.setStartLongitude(dto.getStartLongitude());
        rideSchedule.setEndLatitude(dto.getEndLatitude());
        rideSchedule.setEndLongitude(dto.getEndLongitude());
        rideSchedule.setDayList(dto.getDayList());
        rideSchedule.setDepartureTime(dto.getDepartureTime());
        rideSchedule.setAvailableSeats(dto.getAvailableSeats());
        rideSchedule.setAvailable(dto.getAvailable());
        
        return rideSchedule;
    }
    
    public static RideSchedule toEntity(UpdateRideScheduleDTO dto) {
        RideSchedule rideSchedule = new RideSchedule();
        
        if (dto.getStartLatitude() != null) {
            rideSchedule.setStartLatitude(dto.getStartLatitude());
        }
        if (dto.getStartLongitude() != null) {
            rideSchedule.setStartLongitude(dto.getStartLongitude());
        }
        if (dto.getEndLatitude() != null) {
            rideSchedule.setEndLatitude(dto.getEndLatitude());
        }
        if (dto.getEndLongitude() != null) {
            rideSchedule.setEndLongitude(dto.getEndLongitude());
        }
        if (dto.getDayList() != null) {
            rideSchedule.setDayList(dto.getDayList());
        }
        if (dto.getDepartureTime() != null) {
            rideSchedule.setDepartureTime(dto.getDepartureTime());
        }
        if (dto.getAvailableSeats() != null) {
            rideSchedule.setAvailableSeats(dto.getAvailableSeats());
        }
        if (dto.getAvailable() != null) {
            rideSchedule.setAvailable(dto.getAvailable());
        }
        
        return rideSchedule;
    }
    
    public static RideScheduleDTO toDTO(RideSchedule rideSchedule) {
        RideScheduleDTO dto = new RideScheduleDTO();
        
        dto.setId(rideSchedule.getId());
        dto.setUserId(rideSchedule.getUser().getId());
        dto.setUserDisplayName(rideSchedule.getUser().getDisplayName());
        dto.setStartLatitude(rideSchedule.getStartLatitude());
        dto.setStartLongitude(rideSchedule.getStartLongitude());
        dto.setEndLatitude(rideSchedule.getEndLatitude());
        dto.setEndLongitude(rideSchedule.getEndLongitude());
        dto.setDayList(rideSchedule.getDayList());
        dto.setDepartureTime(rideSchedule.getDepartureTime());
        dto.setAvailableSeats(rideSchedule.getAvailableSeats());
        dto.setAvailable(rideSchedule.isAvailable());
        dto.setCreatedAt(rideSchedule.getCreatedAt());
        dto.setUpdatedAt(rideSchedule.getUpdatedAt());
        
        return dto;
    }
    
    /**
     * Convert a RideSchedule to a RideScheduleSearchResultDTO including distance information
     *
     * @param rideSchedule The ride schedule entity
     * @param searchCriteria The search criteria for calculating distances
     * @return RideScheduleSearchResultDTO with distance information
     */
    public static RideScheduleSearchResultDTO toSearchResultDTO(RideSchedule rideSchedule, SearchRideScheduleDTO searchCriteria) {
        RideScheduleSearchResultDTO dto = new RideScheduleSearchResultDTO();
        
        // Copy all the basic fields
        dto.setId(rideSchedule.getId());
        dto.setUserId(rideSchedule.getUser().getId());
        dto.setUserDisplayName(rideSchedule.getUser().getDisplayName());
        dto.setStartLatitude(rideSchedule.getStartLatitude());
        dto.setStartLongitude(rideSchedule.getStartLongitude());
        dto.setEndLatitude(rideSchedule.getEndLatitude());
        dto.setEndLongitude(rideSchedule.getEndLongitude());
        dto.setDayList(rideSchedule.getDayList());
        dto.setDepartureTime(rideSchedule.getDepartureTime());
        dto.setAvailableSeats(rideSchedule.getAvailableSeats());
        dto.setAvailable(rideSchedule.isAvailable());
        dto.setCreatedAt(rideSchedule.getCreatedAt());
        dto.setUpdatedAt(rideSchedule.getUpdatedAt());
        
        // Calculate and set distance information
        double startPointDistance = GeoUtils.calculateDistanceInMiles(
                searchCriteria.getStartLatitude(), searchCriteria.getStartLongitude(),
                rideSchedule.getStartLatitude(), rideSchedule.getStartLongitude()
        );
        
        double endPointDistance = GeoUtils.calculateDistanceInMiles(
                searchCriteria.getEndLatitude(), searchCriteria.getEndLongitude(),
                rideSchedule.getEndLatitude(), rideSchedule.getEndLongitude()
        );
        
        dto.setStartPointDistanceInMiles(startPointDistance);
        dto.setEndPointDistanceInMiles(endPointDistance);
        dto.setTotalDistanceInMiles(startPointDistance + endPointDistance);
        
        return dto;
    }
}