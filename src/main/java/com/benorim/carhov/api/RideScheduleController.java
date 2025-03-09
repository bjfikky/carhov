package com.benorim.carhov.api;

import com.benorim.carhov.dto.rideSchedule.CreateRideScheduleDTO;
import com.benorim.carhov.dto.rideSchedule.RideScheduleDTO;
import com.benorim.carhov.dto.rideSchedule.RideScheduleSearchResultDTO;
import com.benorim.carhov.dto.rideSchedule.SearchRideScheduleDTO;
import com.benorim.carhov.dto.rideSchedule.UpdateRideScheduleDTO;
import com.benorim.carhov.entity.RideSchedule;
import com.benorim.carhov.mapper.RideScheduleMapper;
import com.benorim.carhov.service.RideScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ride-schedules")
@RequiredArgsConstructor
@Slf4j
public class RideScheduleController {

    private final RideScheduleService rideScheduleService;

    @PostMapping
    public ResponseEntity<RideScheduleDTO> createRideSchedule(@Validated @RequestBody CreateRideScheduleDTO createRideScheduleDTO) {
        log.info("Received request to create ride schedule: {}", createRideScheduleDTO);
        RideSchedule createdRideSchedule = rideScheduleService.createRideSchedule(createRideScheduleDTO);
        return new ResponseEntity<>(RideScheduleMapper.toDTO(createdRideSchedule), HttpStatus.CREATED);
    }

    @GetMapping("/{rideScheduleId}")
    public ResponseEntity<RideScheduleDTO> getRideSchedule(@PathVariable Long rideScheduleId) {
        log.info("Received request to get ride schedule with ID: {}", rideScheduleId);
        return rideScheduleService.findRideScheduleById(rideScheduleId)
                .map(rideSchedule -> new ResponseEntity<>(RideScheduleMapper.toDTO(rideSchedule), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<RideScheduleDTO>> getAllRideSchedules() {
        log.info("Received request to get all ride schedules");
        List<RideScheduleDTO> rideSchedules = rideScheduleService.findAllRideSchedules().stream()
                .map(RideScheduleMapper::toDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(rideSchedules, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RideScheduleDTO>> getRideSchedulesByUserId(@PathVariable Long userId) {
        log.info("Received request to get ride schedules for user with ID: {}", userId);
        List<RideScheduleDTO> rideSchedules = rideScheduleService.findRideSchedulesByUserId(userId).stream()
                .map(RideScheduleMapper::toDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(rideSchedules, HttpStatus.OK);
    }

    @PutMapping("/{rideScheduleId}")
    public ResponseEntity<RideScheduleDTO> updateRideSchedule(@PathVariable Long rideScheduleId, @RequestBody UpdateRideScheduleDTO updateRideScheduleDTO) {
        log.info("Received request to update ride schedule with ID: {}", rideScheduleId);
        RideSchedule rideSchedule = RideScheduleMapper.toEntity(updateRideScheduleDTO);
        return rideScheduleService.updateRideSchedule(rideScheduleId, rideSchedule)
                .map(updatedRideSchedule -> new ResponseEntity<>(RideScheduleMapper.toDTO(updatedRideSchedule), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{rideScheduleId}")
    public ResponseEntity<Void> deleteRideSchedule(@PathVariable Long rideScheduleId) {
        log.info("Received request to delete ride schedule with ID: {}", rideScheduleId);
        if (rideScheduleService.deleteRideSchedule(rideScheduleId)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping("/search")
    public ResponseEntity<List<RideScheduleSearchResultDTO>> searchRideSchedules(@Validated @RequestBody SearchRideScheduleDTO searchCriteria) {
        log.info("Received request to search for ride schedules: {}", searchCriteria);
        List<RideScheduleSearchResultDTO> searchResults = rideScheduleService.searchRideSchedulesWithDetails(searchCriteria);
        return new ResponseEntity<>(searchResults, HttpStatus.OK);
    }
}