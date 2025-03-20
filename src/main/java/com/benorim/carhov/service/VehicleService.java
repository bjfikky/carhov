package com.benorim.carhov.service;

import com.benorim.carhov.dto.vehicle.CreateVehicleDTO;
import com.benorim.carhov.entity.CarHovUser;
import com.benorim.carhov.entity.Vehicle;
import com.benorim.carhov.repository.CarHovUserRepository;
import com.benorim.carhov.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CarHovUserRepository carHovUserRepository;

    public Vehicle createVehicle(CreateVehicleDTO createVehicleDTO) {
        log.info("Creating new vehicle for user ID: {}", createVehicleDTO.getUserId());

        CarHovUser user = carHovUserRepository.findById(createVehicleDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + createVehicleDTO.getUserId()));

        Vehicle vehicle = new Vehicle();
        vehicle.setUser(user);
        vehicle.setMake(createVehicleDTO.getMake());
        vehicle.setModel(createVehicleDTO.getModel());
        vehicle.setCapacity(createVehicleDTO.getCapacity());
        vehicle.setColor(createVehicleDTO.getColor());
        vehicle.setLicensePlate(createVehicleDTO.getLicensePlate());

        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> findVehicleByUserId(Long userId) {
        log.info("Finding vehicles for user ID: {}", userId);
        return vehicleRepository.findByUserId(userId);
    }
}
