package com.benorim.carhov.service;

import com.benorim.carhov.dto.vehicle.CreateVehicleDTO;
import com.benorim.carhov.entity.CarHovUser;
import com.benorim.carhov.entity.Vehicle;
import com.benorim.carhov.exception.DataOwnershipException;
import com.benorim.carhov.repository.CarHovUserRepository;
import com.benorim.carhov.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceTest {
    
    @Mock
    private VehicleRepository vehicleRepository;
    
    @Mock
    private CarHovUserRepository carHovUserRepository;

    @Mock
    private AuthService authService;

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
        when(authService.isRequestMadeByLoggedInUser(user)).thenReturn(true);
        Vehicle result = vehicleService.createVehicle(createVehicleDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void createVehicle_UserTriesToCreateVehicleForAnotherUser() {
        when(carHovUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(authService.isRequestMadeByLoggedInUser(user))
                .thenThrow(new DataOwnershipException("User id mismatch"));

        Exception exception = assertThrows(DataOwnershipException.class, () ->
                vehicleService.createVehicle(createVehicleDTO));

        assertEquals("User id mismatch", exception.getMessage());
    }

    @Test
    void createVehicle_UserIsNotSignedIn() {
        when(carHovUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(authService.isRequestMadeByLoggedInUser(user))
                .thenThrow(new DataOwnershipException("User is not signed in"));

        Exception exception = assertThrows(DataOwnershipException.class, () ->
                vehicleService.createVehicle(createVehicleDTO));

        assertEquals("User is not signed in", exception.getMessage());
    }

    @Test
    void createVehicle_UserNotFound() {
        when(carHovUserRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                vehicleService.createVehicle(createVehicleDTO));

        assertEquals("User not found with ID: 1", exception.getMessage());
    }

    @Test
    void findVehiclesByUserId() {
        when(vehicleRepository.findByUserId(1L)).thenReturn(Collections.singletonList(vehicle));
        when(carHovUserRepository.findById(1L)).thenReturn(Optional.of(user));

        List<Vehicle> result = vehicleService.findVehiclesByUserId(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());
    }

    @Test
    void findVehiclesByUserId_Throws_DataOwnershipException() {
        when(vehicleRepository.findByUserId(1L)).thenReturn(Collections.singletonList(vehicle));
        when(carHovUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(authService.isRequestMadeByLoggedInUserOrAdmin(user)).thenThrow(new DataOwnershipException("User id mismatch"));

        assertThrows(DataOwnershipException.class, () -> vehicleService.findVehiclesByUserId(1L)) ;
    }

    @Test
    void deleteVehicle_Success() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        doNothing().when(vehicleRepository).delete(vehicle);
        when(authService.isRequestMadeByLoggedInUser(user)).thenReturn(true);

        boolean result = vehicleService.deleteVehicle(1L);

        assertTrue(result);
        verify(vehicleRepository, times(1)).delete(vehicle);
    }

    @Test
    void deleteVehicle_UserTriesToDeleteVehicleForAnotherUser() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(authService.isRequestMadeByLoggedInUser(any(CarHovUser.class)))
                .thenThrow(new DataOwnershipException("User id mismatch"));

        Exception exception = assertThrows(DataOwnershipException.class, () ->
                vehicleService.deleteVehicle(vehicle.getId()));

        assertEquals("User id mismatch", exception.getMessage());
    }
}
