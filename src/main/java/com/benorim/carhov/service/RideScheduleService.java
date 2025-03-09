package com.benorim.carhov.service;

import com.benorim.carhov.dto.rideSchedule.CreateRideScheduleDTO;
import com.benorim.carhov.dto.rideSchedule.RideScheduleSearchResultDTO;
import com.benorim.carhov.dto.rideSchedule.SearchRideScheduleDTO;
import com.benorim.carhov.entity.CarHovUser;
import com.benorim.carhov.entity.RideSchedule;
import com.benorim.carhov.mapper.RideScheduleMapper;
import com.benorim.carhov.repository.CarHovUserRepository;
import com.benorim.carhov.repository.RideScheduleRepository;
import com.benorim.carhov.util.GeoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RideScheduleService {

    private final RideScheduleRepository rideScheduleRepository;
    private final CarHovUserRepository carHovUserRepository;

    public RideSchedule createRideSchedule(CreateRideScheduleDTO createRideScheduleDTO) {
        log.info("Creating new ride schedule for user ID: {}", createRideScheduleDTO.getUserId());
        
        CarHovUser user = carHovUserRepository.findById(createRideScheduleDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + createRideScheduleDTO.getUserId()));

        RideSchedule rideSchedule = new RideSchedule();
        rideSchedule.setUser(user);
        rideSchedule.setStartLatitude(createRideScheduleDTO.getStartLatitude());
        rideSchedule.setStartLongitude(createRideScheduleDTO.getStartLongitude());
        rideSchedule.setEndLatitude(createRideScheduleDTO.getEndLatitude());
        rideSchedule.setEndLongitude(createRideScheduleDTO.getEndLongitude());
        rideSchedule.setDayList(createRideScheduleDTO.getDayList());
        rideSchedule.setDepartureTime(createRideScheduleDTO.getDepartureTime());
        rideSchedule.setAvailableSeats(createRideScheduleDTO.getAvailableSeats());
        rideSchedule.setAvailable(createRideScheduleDTO.getAvailable());
        
        return rideScheduleRepository.save(rideSchedule);
    }
    
    public Optional<RideSchedule> findRideScheduleById(Long rideScheduleId) {
        log.info("Finding ride schedule with ID: {}", rideScheduleId);
        return rideScheduleRepository.findById(rideScheduleId);
    }
    
    public List<RideSchedule> findAllRideSchedules() {
        log.info("Finding all ride schedules");
        return rideScheduleRepository.findAll();
    }
    
    public Optional<RideSchedule> updateRideSchedule(Long rideScheduleId, RideSchedule updatedRideSchedule) {
        log.info("Updating ride schedule with ID: {}", rideScheduleId);
        return rideScheduleRepository.findById(rideScheduleId)
                .map(existingRideSchedule -> {
                    // Only update fields that are provided
                    if (updatedRideSchedule.getStartLatitude() != 0) {
                        existingRideSchedule.setStartLatitude(updatedRideSchedule.getStartLatitude());
                    }
                    if (updatedRideSchedule.getStartLongitude() != 0) {
                        existingRideSchedule.setStartLongitude(updatedRideSchedule.getStartLongitude());
                    }
                    if (updatedRideSchedule.getEndLatitude() != 0) {
                        existingRideSchedule.setEndLatitude(updatedRideSchedule.getEndLatitude());
                    }
                    if (updatedRideSchedule.getEndLongitude() != 0) {
                        existingRideSchedule.setEndLongitude(updatedRideSchedule.getEndLongitude());
                    }
                    if (updatedRideSchedule.getDayList() != null && !updatedRideSchedule.getDayList().isEmpty()) {
                        existingRideSchedule.setDayList(updatedRideSchedule.getDayList());
                    }
                    if (updatedRideSchedule.getDepartureTime() != null) {
                        existingRideSchedule.setDepartureTime(updatedRideSchedule.getDepartureTime());
                    }
                    if (updatedRideSchedule.getAvailableSeats() > 0) {
                        existingRideSchedule.setAvailableSeats(updatedRideSchedule.getAvailableSeats());
                    }
                    // Check if 'available' field is explicitly set in the update object
                    if (updatedRideSchedule.isAvailable() != existingRideSchedule.isAvailable()) {
                        existingRideSchedule.setAvailable(updatedRideSchedule.isAvailable());
                    }
                    
                    return rideScheduleRepository.save(existingRideSchedule);
                });
    }
    
    public boolean deleteRideSchedule(Long rideScheduleId) {
        log.info("Deleting ride schedule with ID: {}", rideScheduleId);
        return rideScheduleRepository.findById(rideScheduleId)
                .map(rideSchedule -> {
                    rideScheduleRepository.delete(rideSchedule);
                    return true;
                })
                .orElse(false);
    }
    
    public List<RideSchedule> findRideSchedulesByUserId(Long userId) {
        log.info("Finding ride schedules for user ID: {}", userId);
        return rideScheduleRepository.findByUserId(userId);
    }
    
    /**
     * Search for ride schedules based on start and end coordinates within a specified radius.
     * 
     * @param searchCriteria The search criteria containing start/end coordinates and search radius
     * @return List of ride schedules that match the search criteria
     */
    protected List<RideSchedule> searchRideSchedules(SearchRideScheduleDTO searchCriteria) {
        log.info("Searching for ride schedules near start: ({}, {}) and end: ({}, {}), radius: {} miles",
                searchCriteria.getStartLatitude(), searchCriteria.getStartLongitude(),
                searchCriteria.getEndLatitude(), searchCriteria.getEndLongitude(),
                searchCriteria.getRadiusInMiles());
        
        // Get all available ride schedules
        List<RideSchedule> allRideSchedules = rideScheduleRepository.findByAvailableTrue();
        
        // Filter ride schedules based on proximity to start and end points
        return allRideSchedules.stream()
                .filter(rideSchedule -> 
                    // Check if ride schedule start point is within radius of search start point
                    GeoUtils.isWithinRadius(
                        searchCriteria.getStartLatitude(), searchCriteria.getStartLongitude(),
                        rideSchedule.getStartLatitude(), rideSchedule.getStartLongitude(),
                        searchCriteria.getRadiusInMiles()
                    ) &&
                    // Check if ride schedule end point is within radius of search end point
                    GeoUtils.isWithinRadius(
                        searchCriteria.getEndLatitude(), searchCriteria.getEndLongitude(),
                        rideSchedule.getEndLatitude(), rideSchedule.getEndLongitude(),
                        searchCriteria.getRadiusInMiles()
                    )
                )
                .collect(Collectors.toList());
    }
    
    /**
     * Search for ride schedules and return detailed results with distance information.
     *
     * @param searchCriteria The search criteria containing start/end coordinates and search radius
     * @return List of ride schedule search results with distance information
     */
    public List<RideScheduleSearchResultDTO> searchRideSchedulesWithDetails(SearchRideScheduleDTO searchCriteria) {
        log.info("Searching for ride schedules with details near start: ({}, {}) and end: ({}, {}), radius: {} miles",
                searchCriteria.getStartLatitude(), searchCriteria.getStartLongitude(),
                searchCriteria.getEndLatitude(), searchCriteria.getEndLongitude(),
                searchCriteria.getRadiusInMiles());
        
        List<RideSchedule> matchingRideSchedules = searchRideSchedules(searchCriteria);
        
        return matchingRideSchedules.stream()
                .map(rideSchedule -> RideScheduleMapper.toSearchResultDTO(rideSchedule, searchCriteria))
                .sorted((r1, r2) -> Double.compare(r1.getTotalDistanceInMiles(), r2.getTotalDistanceInMiles())) // Sort by total distance
                .collect(Collectors.toList());
    }
}