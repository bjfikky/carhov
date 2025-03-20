package com.benorim.carhov.dto.vehicle;

import lombok.Data;

@Data
public class VehicleDTO {

    private Long id;
    private Long userId;
    private String make;
    private String model;
    private String licensePlate;
    private String color;
    private int capacity;
}
