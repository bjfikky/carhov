package com.benorim.carhov.service;

import com.benorim.carhov.dto.vehicle.CreateVehicleDTO;
import com.benorim.carhov.dto.vehicle.UpdateVehicleDTO;
import com.benorim.carhov.entity.CarHovUser;
import com.benorim.carhov.entity.Vehicle;
import com.benorim.carhov.exception.DataOwnershipException;
import com.benorim.carhov.mapper.VehicleMapper;
import com.benorim.carhov.repository.CarHovUserRepository;
import com.benorim.carhov.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CarHovUserRepository carHovUserRepository;
    private final AuthService authService;

    public Vehicle createVehicle(CreateVehicleDTO createVehicleDTO) {
        log.info("Creating new vehicle for user ID: {}", createVehicleDTO.getUserId());

        CarHovUser user = carHovUserRepository.findById(createVehicleDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + createVehicleDTO.getUserId()));

        doesVehicleBelongToLoggedInUser(user);

        Vehicle vehicle = VehicleMapper.toEntity(createVehicleDTO, user);

        return vehicleRepository.save(vehicle);
    }

    public Vehicle updateVehicle(Long vehicleId, UpdateVehicleDTO updateVehicleDTO) {
        log.info("Updating vehicle with ID: {}", vehicleId);
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + vehicleId));

        doesVehicleBelongToLoggedInUser(vehicle.getUser());

        // color is the only thing that could change on the vehicle
        // delete and add a new vehicle if a user purchases a new vehicle
        if (StringUtils.isNotBlank(updateVehicleDTO.getColor())) {
            vehicle.setColor(updateVehicleDTO.getColor());
        }
        return vehicleRepository.save(vehicle);
    }

    public boolean deleteVehicle(Long vehicleId) {
        log.info("Deleting vehicle with ID: {}", vehicleId);
        return vehicleRepository.findById(vehicleId)
                .map(vehicle -> {
                    doesVehicleBelongToLoggedInUser(vehicle.getUser());
                    vehicleRepository.delete(vehicle);
                    return true;
                })
                .orElse(false);
    }

    public Vehicle findVehicleById(Long vehicleId) {
        log.info("Finding vehicle with ID: {}", vehicleId);
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + vehicleId));

        // TODO: An admin should be able to use this method too. Add an || isAdminUser
        doesVehicleBelongToLoggedInUser(vehicle.getUser());

        return vehicle;
    }

    public List<Vehicle> findVehiclesByUserId(Long userId) {
        log.info("Finding all vehicles for user ID: {}", userId);
        CarHovUser user = carHovUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        List<Vehicle> vehicles = vehicleRepository.findByUserId(userId);
        doesVehicleBelongToLoggedInUser(user);
        return vehicles;
    }

    private void doesVehicleBelongToLoggedInUser(CarHovUser user) {
        if (user == null) {
            throw new DataOwnershipException("User not found");
        }
        Long signedInUserId = authService.getSignedInUserId();
        if (signedInUserId == null) {
            log.error("User is not signed in");
            throw new DataOwnershipException("User is not signed in");
        }

        if (!signedInUserId.equals(user.getId())) {
            log.error("User id mismatch");
            throw new DataOwnershipException("User id mismatch");
        }
    }
}
