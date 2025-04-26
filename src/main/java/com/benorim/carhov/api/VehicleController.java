package com.benorim.carhov.api;

import com.benorim.carhov.dto.vehicle.CreateVehicleDTO;
import com.benorim.carhov.dto.vehicle.UpdateVehicleDTO;
import com.benorim.carhov.dto.vehicle.VehicleDTO;
import com.benorim.carhov.entity.Vehicle;
import com.benorim.carhov.mapper.VehicleMapper;
import com.benorim.carhov.service.VehicleService;
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
        return new ResponseEntity<>(VehicleMapper.toDTO(createVehicle), HttpStatus.CREATED);
    }

    @PutMapping("/{vehicleId}")
    public ResponseEntity<VehicleDTO> updateVehicle(@Validated @RequestBody UpdateVehicleDTO updateVehicleDTO, @PathVariable Long vehicleId) {
        log.info("Received request to update vehicle: {}", updateVehicleDTO);
        Vehicle updateVehicle = vehicleService.updateVehicle(vehicleId, updateVehicleDTO);
        return new ResponseEntity<>(VehicleMapper.toDTO(updateVehicle), HttpStatus.OK);
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long vehicleId) {
        log.info("Received request to delete vehicle: {}", vehicleId);
        vehicleService.deleteVehicle(vehicleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<VehicleDTO> getVehicle(@PathVariable Long vehicleId) {
        log.info("Received request to get vehicle: {}", vehicleId);
        Vehicle vehicle = vehicleService.findVehicleById(vehicleId);
        return new ResponseEntity<>(VehicleMapper.toDTO(vehicle), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<VehicleDTO>> getVehiclesByUserId(@PathVariable Long userId) {
        log.info("Received request to get vehicles by user: {}", userId);
        List<Vehicle> vehicles = vehicleService.findVehiclesByUserId(userId);
        List<VehicleDTO> vehicleDTOs = vehicles.stream().map(VehicleMapper::toDTO).toList();
        return new ResponseEntity<>(vehicleDTOs, HttpStatus.OK);
    }
}

