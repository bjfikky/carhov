package com.benorim.carhov.service;

import com.benorim.carhov.dto.vehicle.CreateVehicleDTO;
import com.benorim.carhov.entity.CarHovUser;
import com.benorim.carhov.entity.Vehicle;
import com.benorim.carhov.repository.CarHovUserRepository;
import com.benorim.carhov.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceTest {
    
    @Mock
    private VehicleRepository vehicleRepository;
    
    @Mock
    private CarHovUserRepository carHovUserRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private CarHovUser user;
    private Vehicle vehicle;
    private CreateVehicleDTO createVehicleDTO;

    @BeforeEach
    void setUp() {
        user = new CarHovUser();
        user.setId(1L);

        vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setUser(user);

        createVehicleDTO = new CreateVehicleDTO();
        createVehicleDTO.setUserId(1L);
        createVehicleDTO.setMake("Toyota");
        createVehicleDTO.setModel("Avalon");
        createVehicleDTO.setLicensePlate("ABC123");
        createVehicleDTO.setColor("Black");
        createVehicleDTO.setCapacity(5);
    }

    @Test
    void createVehicle_Success() {
        when(carHovUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        Vehicle result = vehicleService.createVehicle(createVehicleDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void createVehicle_UserNotFound() {
        when(carHovUserRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                vehicleService.createVehicle(createVehicleDTO));

        assertEquals("User not found with ID: 1", exception.getMessage());

    }
}
