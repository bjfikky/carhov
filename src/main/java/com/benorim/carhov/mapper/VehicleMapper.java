package com.benorim.carhov.mapper;

import com.benorim.carhov.dto.vehicle.CreateVehicleDTO;
import com.benorim.carhov.dto.vehicle.VehicleDTO;
import com.benorim.carhov.entity.CarHovUser;
import com.benorim.carhov.entity.Vehicle;

public class VehicleMapper {

    public static VehicleDTO toDTO(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();

        dto.setId(vehicle.getId());
        dto.setUserId(vehicle.getUser().getId());
        dto.setMake(vehicle.getMake());
        dto.setModel(vehicle.getModel());
        dto.setColor(vehicle.getColor());
        dto.setYear(vehicle.getYear());
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setPassengerCapacity(vehicle.getPassengerCapacity());

        return dto;
    }

    public static Vehicle toEntity(CreateVehicleDTO dto, CarHovUser user) {
        return Vehicle.builder()
                .make(dto.getMake())
                .model(dto.getModel())
                .user(user)
                .passengerCapacity(dto.getCapacity())
                .color(dto.getColor())
                .licensePlate(dto.getLicensePlate())
                .build();
    }
}
