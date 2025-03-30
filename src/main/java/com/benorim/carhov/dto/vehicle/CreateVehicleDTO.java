package com.benorim.carhov.dto.vehicle;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateVehicleDTO {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Make is required")
    private String make;

    @NotNull(message = "Model is required")
    private String model;

    @NotNull(message = "License Plate is required")
    private String licensePlate;

    @NotNull(message = "Color is required")
    private String color;

    @NotNull(message = "Capacity is required")
    private int capacity;
}
