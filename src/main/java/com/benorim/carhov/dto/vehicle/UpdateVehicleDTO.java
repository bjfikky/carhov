package com.benorim.carhov.dto.vehicle;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateVehicleDTO {
    // Vehicle color is the only thing that could change
    @NotNull(message = "Color is required")
    private String color;
}
