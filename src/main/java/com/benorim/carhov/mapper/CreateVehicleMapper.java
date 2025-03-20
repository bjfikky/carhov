package com.benorim.carhov.mapper;

import com.benorim.carhov.dto.rideSchedule.RideScheduleDTO;
import com.benorim.carhov.dto.vehicle.VehicleDTO;
import com.benorim.carhov.entity.RideSchedule;
import com.benorim.carhov.entity.Vehicle;

public class CreateVehicleMapper {

    public static VehicleDTO toDTO(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();

        dto.setId(vehicle.getId());
        dto.setUserId(vehicle.getUser().getId());
        dto.setMake(vehicle.getMake());
        dto.setModel(vehicle.getModel());
        dto.setColor(vehicle.getColor());
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setCapacity(vehicle.getCapacity());

        return dto;
    }
}
