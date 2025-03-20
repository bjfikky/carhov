package com.benorim.carhov.api;

import com.benorim.carhov.dto.rideSchedule.CreateRideScheduleDTO;
import com.benorim.carhov.dto.rideSchedule.RideScheduleDTO;
import com.benorim.carhov.dto.vehicle.CreateVehicleDTO;
import com.benorim.carhov.dto.vehicle.VehicleDTO;
import com.benorim.carhov.entity.RideSchedule;
import com.benorim.carhov.entity.Vehicle;
import com.benorim.carhov.mapper.CreateVehicleMapper;
import com.benorim.carhov.mapper.RideScheduleMapper;
import com.benorim.carhov.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@Slf4j
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<VehicleDTO> createVehicle(@Validated @RequestBody CreateVehicleDTO createVehicleDTO) {
        log.info("Received request to create vehicle: {}", createVehicleDTO);
        Vehicle createVehicle = vehicleService.createVehicle(createVehicleDTO);
        return new ResponseEntity<>(CreateVehicleMapper.toDTO(createVehicle), HttpStatus.CREATED);
    }

}

