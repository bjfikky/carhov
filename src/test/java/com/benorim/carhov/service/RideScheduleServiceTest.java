package com.benorim.carhov.service;

import com.benorim.carhov.dto.rideSchedule.CreateRideScheduleDTO;
import com.benorim.carhov.dto.rideSchedule.SearchRideScheduleDTO;
import com.benorim.carhov.entity.CarHovUser;
import com.benorim.carhov.entity.RideSchedule;
import com.benorim.carhov.entity.Vehicle;
import com.benorim.carhov.enums.DayOfWeek;
import com.benorim.carhov.repository.CarHovUserRepository;
import com.benorim.carhov.repository.RideScheduleRepository;
import com.benorim.carhov.repository.VehicleRepository;
import com.benorim.carhov.util.GeoUtils;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RideScheduleServiceTest {

    @Mock
    private RideScheduleRepository rideScheduleRepository;

    @Mock
    private CarHovUserRepository carHovUserRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private RideScheduleService rideScheduleService;

    private CarHovUser user;
    private RideSchedule rideSchedule;
    private CreateRideScheduleDTO createRideScheduleDTO;

    @BeforeEach
    void setUp() {
        user = new CarHovUser();
        user.setId(1L);

        rideSchedule = new RideSchedule();
        rideSchedule.setId(1L);
        rideSchedule.setUser(user);

        createRideScheduleDTO = new CreateRideScheduleDTO();
        createRideScheduleDTO.setUserId(1L);
        createRideScheduleDTO.setVehicleId(1L);
        createRideScheduleDTO.setStartLatitude(1234.0);
        createRideScheduleDTO.setStartLongitude(-1234.0);
        createRideScheduleDTO.setEndLatitude(4321.0);
        createRideScheduleDTO.setEndLongitude(-4321.0);
        createRideScheduleDTO.setAvailable(true);
        createRideScheduleDTO.setDayList(List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY));
        createRideScheduleDTO.setAvailableSeats(3);
    }

    @Test
    void createRideSchedule_Success() {
        when(carHovUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(rideScheduleRepository.save(any(RideSchedule.class))).thenReturn(rideSchedule);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(new Vehicle()));

        RideSchedule result = rideScheduleService.createRideSchedule(createRideScheduleDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(rideScheduleRepository, times(1)).save(any(RideSchedule.class));
    }

    @Test
    void createRideSchedule_UserNotFound() {
        when(carHovUserRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                rideScheduleService.createRideSchedule(createRideScheduleDTO));

        assertEquals("User not found with ID: 1", exception.getMessage());
    }

    @Test
    void findRideScheduleById_Found() {
        when(rideScheduleRepository.findById(1L)).thenReturn(Optional.of(rideSchedule));

        Optional<RideSchedule> result = rideScheduleService.findRideScheduleById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void findRideScheduleById_NotFound() {
        when(rideScheduleRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<RideSchedule> result = rideScheduleService.findRideScheduleById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void findAllRideSchedules() {
        when(rideScheduleRepository.findAll()).thenReturn(Collections.singletonList(rideSchedule));

        List<RideSchedule> result = rideScheduleService.findAllRideSchedules();

        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());
    }

    @Test
    void deleteRideSchedule_Success() {
        when(rideScheduleRepository.findById(1L)).thenReturn(Optional.of(rideSchedule));
        doNothing().when(rideScheduleRepository).delete(rideSchedule);

        boolean result = rideScheduleService.deleteRideSchedule(1L);

        assertTrue(result);
        verify(rideScheduleRepository, times(1)).delete(rideSchedule);
    }

    @Test
    void deleteRideSchedule_NotFound() {
        when(rideScheduleRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = rideScheduleService.deleteRideSchedule(1L);

        assertFalse(result);
    }

    @Test
    void findRideSchedulesByUserId() {
        when(rideScheduleRepository.findByUserId(1L)).thenReturn(Collections.singletonList(rideSchedule));

        List<RideSchedule> result = rideScheduleService.findRideSchedulesByUserId(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());
    }

    @Test
    void searchRideSchedules() {
        SearchRideScheduleDTO searchCriteria = new SearchRideScheduleDTO();
        searchCriteria.setStartLatitude(38.8951);
        searchCriteria.setStartLongitude(-77.0364);
        searchCriteria.setEndLatitude(38.8977);
        searchCriteria.setEndLongitude(-77.0365);
        searchCriteria.setRadiusInMiles(5.0);

        when(rideScheduleRepository.findByAvailableTrue()).thenReturn(Collections.singletonList(rideSchedule));
        mockStatic(GeoUtils.class).when(() -> GeoUtils.isWithinRadius(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(true);

        List<RideSchedule> result = rideScheduleService.searchRideSchedules(searchCriteria);

        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());
    }

}