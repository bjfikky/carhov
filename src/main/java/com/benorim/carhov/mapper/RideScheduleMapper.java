package com.benorim.carhov.mapper;

import com.benorim.carhov.dto.rideSchedule.CreateRideScheduleDTO;
import com.benorim.carhov.dto.rideSchedule.RideScheduleDTO;
import com.benorim.carhov.dto.rideSchedule.RideScheduleSearchResultDTO;
import com.benorim.carhov.dto.rideSchedule.SearchRideScheduleDTO;
import com.benorim.carhov.dto.rideSchedule.UpdateRideScheduleDTO;
import com.benorim.carhov.entity.CarHovUser;
import com.benorim.carhov.entity.RideSchedule;
import com.benorim.carhov.entity.Vehicle;
import com.benorim.carhov.util.GeoUtils;

public class RideScheduleMapper {

    public static RideSchedule toEntity(CreateRideScheduleDTO dto, CarHovUser user, Vehicle vehicle) {
        RideSchedule rideSchedule = new RideSchedule();
        
        rideSchedule.setUser(user);
        rideSchedule.setVehicle(vehicle);
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
        RideScheduleDTO rideScheduleDTO = new RideScheduleDTO();

        rideScheduleDTO.setId(rideSchedule.getId());
        rideScheduleDTO.setUserId(rideSchedule.getUser().getId());
        rideScheduleDTO.setVehicleId(rideSchedule.getVehicle().getId());
        rideScheduleDTO.setUserDisplayName(rideSchedule.getUser().getDisplayName());
        rideScheduleDTO.setStartLatitude(rideSchedule.getStartLatitude());
        rideScheduleDTO.setStartLongitude(rideSchedule.getStartLongitude());
        rideScheduleDTO.setEndLatitude(rideSchedule.getEndLatitude());
        rideScheduleDTO.setEndLongitude(rideSchedule.getEndLongitude());
        rideScheduleDTO.setDayList(rideSchedule.getDayList());
        rideScheduleDTO.setDepartureTime(rideSchedule.getDepartureTime());
        rideScheduleDTO.setAvailableSeats(rideSchedule.getAvailableSeats());
        rideScheduleDTO.setAvailable(rideSchedule.isAvailable());
        rideScheduleDTO.setCreatedAt(rideSchedule.getCreatedAt());
        rideScheduleDTO.setUpdatedAt(rideSchedule.getUpdatedAt());

        return rideScheduleDTO;
    }

    /**
     * Convert a RideSchedule to a RideScheduleSearchResultDTO including distance information
     *
     * @param rideSchedule The ride schedule entity
     * @param searchCriteria The search criteria for calculating distances
     * @return RideScheduleSearchResultDTO with distance information
     */
    public static RideScheduleSearchResultDTO toSearchResultDTO(RideSchedule rideSchedule, SearchRideScheduleDTO searchCriteria) {
        RideScheduleSearchResultDTO rideScheduleSearchResultDTO = new RideScheduleSearchResultDTO();

        rideScheduleSearchResultDTO.setId(rideSchedule.getId());
        rideScheduleSearchResultDTO.setUserId(rideSchedule.getUser().getId());
        rideScheduleSearchResultDTO.setVehicleId(rideSchedule.getVehicle().getId());
        rideScheduleSearchResultDTO.setUserDisplayName(rideSchedule.getUser().getDisplayName());
        rideScheduleSearchResultDTO.setStartLatitude(rideSchedule.getStartLatitude());
        rideScheduleSearchResultDTO.setStartLongitude(rideSchedule.getStartLongitude());
        rideScheduleSearchResultDTO.setEndLatitude(rideSchedule.getEndLatitude());
        rideScheduleSearchResultDTO.setEndLongitude(rideSchedule.getEndLongitude());
        rideScheduleSearchResultDTO.setDayList(rideSchedule.getDayList());
        rideScheduleSearchResultDTO.setDepartureTime(rideSchedule.getDepartureTime());
        rideScheduleSearchResultDTO.setAvailableSeats(rideSchedule.getAvailableSeats());
        rideScheduleSearchResultDTO.setAvailable(rideSchedule.isAvailable());
        rideScheduleSearchResultDTO.setCreatedAt(rideSchedule.getCreatedAt());
        rideScheduleSearchResultDTO.setUpdatedAt(rideSchedule.getUpdatedAt());
        
        // Calculate and set distance information
        double startPointDistance = GeoUtils.calculateDistanceInMiles(
                searchCriteria.getStartLatitude(), searchCriteria.getStartLongitude(),
                rideSchedule.getStartLatitude(), rideSchedule.getStartLongitude()
        );
        
        double endPointDistance = GeoUtils.calculateDistanceInMiles(
                searchCriteria.getEndLatitude(), searchCriteria.getEndLongitude(),
                rideSchedule.getEndLatitude(), rideSchedule.getEndLongitude()
        );
        
        rideScheduleSearchResultDTO.setStartPointDistanceInMiles(startPointDistance);
        rideScheduleSearchResultDTO.setEndPointDistanceInMiles(endPointDistance);
        rideScheduleSearchResultDTO.setTotalDistanceInMiles(startPointDistance + endPointDistance);
        
        return rideScheduleSearchResultDTO;
    }
}