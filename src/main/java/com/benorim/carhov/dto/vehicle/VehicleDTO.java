package com.benorim.carhov.dto.vehicle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDTO {
    private Long id;
    private Long userId;
    private String make;
    private String model;
    private Integer year;
    private String licensePlate;
    private String color;
    private Integer passengerCapacity;
}
