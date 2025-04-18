package com.benorim.carhov.service;

import com.benorim.carhov.dto.booking.CreateBookingDTO;
import com.benorim.carhov.entity.Booking;
import com.benorim.carhov.entity.CarHovUser;
import com.benorim.carhov.entity.RideSchedule;
import com.benorim.carhov.enums.BookingStatus;
import com.benorim.carhov.enums.DayOfWeek;
import com.benorim.carhov.exception.DataOwnershipException;
import com.benorim.carhov.repository.BookingRepository;
import com.benorim.carhov.repository.CarHovUserRepository;
import com.benorim.carhov.repository.RideScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RideScheduleRepository rideScheduleRepository;

    @Mock
    private CarHovUserRepository carHovUserRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private BookingService bookingService;

    private CarHovUser user;
    private RideSchedule rideSchedule;
    private Booking booking;
    private CreateBookingDTO createBookingDTO;

    @BeforeEach
    void setUp() {
        user = new CarHovUser();
        user.setId(1L);
        user.setDisplayName("Test User");

        rideSchedule = new RideSchedule();
        rideSchedule.setId(1L);
        rideSchedule.setUser(user);
        rideSchedule.setAvailableSeats(4);
        rideSchedule.setAvailable(true);
        rideSchedule.setDayList(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY));

        booking = Booking.builder()
                .id(1L)
                .carHovUser(user)
                .rideSchedule(rideSchedule)
                .seatsBooked(2)
                .bookedDays("MONDAY")
                .status(BookingStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        createBookingDTO = new CreateBookingDTO();
        createBookingDTO.setUserId(1L);
        createBookingDTO.setRideScheduleId(1L);
        createBookingDTO.setSeatsBooked(2);
        createBookingDTO.setBookedDays(List.of(DayOfWeek.MONDAY));
    }

    @Test
    void createBooking_Success() {
        // Arrange
        when(rideScheduleRepository.findById(1L)).thenReturn(Optional.of(rideSchedule));
        when(carHovUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByCarHovUserIdAndRideScheduleId(1L, 1L)).thenReturn(List.of());
        when(bookingRepository.sumSeatsBookedByRideScheduleIdAndDay(1L, "MONDAY")).thenReturn(0);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(authService.getSignedInUserId()).thenReturn(1L);

        // Act
        Booking result = bookingService.createBooking(createBookingDTO);

        // Assert
        assertNotNull(result);
        assertEquals(BookingStatus.PENDING, result.getStatus());
        assertEquals(2, result.getSeatsBooked());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_RideScheduleNotFound() {
        // Arrange
        when(rideScheduleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            bookingService.createBooking(createBookingDTO)
        );
    }

    @Test
    void createBooking_RideNotAvailable() {
        // Arrange
        rideSchedule.setAvailable(false);
        when(rideScheduleRepository.findById(1L)).thenReturn(Optional.of(rideSchedule));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> 
            bookingService.createBooking(createBookingDTO)
        );
    }

    @Test
    void createBooking_DayNotAvailable() {
        // Arrange
        createBookingDTO.setBookedDays(List.of(DayOfWeek.FRIDAY));
        when(rideScheduleRepository.findById(1L)).thenReturn(Optional.of(rideSchedule));
        when(carHovUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(authService.getSignedInUserId()).thenReturn(1L);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            bookingService.createBooking(createBookingDTO)
        );
    }

    @Test
    void cancelBooking_Success() {
        // Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(authService.getSignedInUserId()).thenReturn(1L);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        // Act
        Booking result = bookingService.cancelBooking(1L);

        // Assert
        assertNotNull(result);
        assertEquals(BookingStatus.CANCELLED, result.getStatus());
        assertNotNull(result.getCancellationDate());
        verify(bookingRepository).save(booking);
    }

    @Test
    void cancelBooking_NotOwner() {
        // Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(authService.getSignedInUserId()).thenReturn(2L);

        // Act & Assert
        assertThrows(DataOwnershipException.class, () -> 
            bookingService.cancelBooking(1L)
        );
    }

    @Test
    void acceptBooking_Success() {
        // Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(authService.getSignedInUserId()).thenReturn(1L);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        // Act
        Booking result = bookingService.acceptBooking(1L);

        // Assert
        assertNotNull(result);
        assertEquals(BookingStatus.ACCEPTED, result.getStatus());
        assertNotNull(result.getStatusUpdateDate());
        verify(bookingRepository).save(booking);
    }

    @Test
    void acceptBooking_NotRideOwner() {
        // Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(authService.getSignedInUserId()).thenReturn(2L);

        // Act & Assert
        assertThrows(DataOwnershipException.class, () -> 
            bookingService.acceptBooking(1L)
        );
    }

    @Test
    void findBookingsByUserId() {
        // Arrange
        List<Booking> bookings = List.of(booking);
        when(bookingRepository.findByCarHovUserId(1L)).thenReturn(bookings);

        // Act
        List<Booking> result = bookingService.findBookingsByUserId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking, result.getFirst());
    }

    @Test
    void getTotalSeatsBookedForDay() {
        // Arrange
        when(bookingRepository.sumSeatsBookedByRideScheduleIdAndDay(1L, "MONDAY")).thenReturn(2);

        // Act
        int result = bookingService.getTotalSeatsBookedForDay(1L, DayOfWeek.MONDAY);

        // Assert
        assertEquals(2, result);
    }
} 