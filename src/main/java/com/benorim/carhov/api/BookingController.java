package com.benorim.carhov.api;

import com.benorim.carhov.dto.booking.BookingDTO;
import com.benorim.carhov.dto.booking.CreateBookingDTO;
import com.benorim.carhov.dto.booking.UpdateBookingDTO;
import com.benorim.carhov.entity.Booking;
import com.benorim.carhov.entity.RideSchedule;
import com.benorim.carhov.enums.DayOfWeek;
import com.benorim.carhov.exception.DataOwnershipException;
import com.benorim.carhov.mapper.BookingMapper;
import com.benorim.carhov.repository.RideScheduleRepository;
import com.benorim.carhov.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for booking operations
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final RideScheduleRepository rideScheduleRepository;

    /**
     * Create a new booking
     *
     * @param createBookingDTO The booking data to create
     * @return The created booking
     */
    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody CreateBookingDTO createBookingDTO) {
        log.info("Received request to create booking: {}", createBookingDTO);
        
        try {
            Booking booking = bookingService.createBooking(createBookingDTO);
            return new ResponseEntity<>(BookingMapper.toDTO(booking), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.error("Failed to create booking: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            log.error("Failed to create booking: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Failed to create booking", e);
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update an existing booking
     *
     * @param updateBookingDTO The updated booking data
     * @return The updated booking
     */
    @PutMapping("/{bookingId}")
    public ResponseEntity<?> updateBooking(
            @PathVariable Long bookingId, 
            @Valid @RequestBody UpdateBookingDTO updateBookingDTO) {
        
        log.info("Received request to update booking ID: {}", bookingId);
        
        // Ensure path variable and body ID match
        if (!bookingId.equals(updateBookingDTO.getId())) {
            return new ResponseEntity<>("Path variable ID and request body ID must match", HttpStatus.BAD_REQUEST);
        }
        
        try {
            Booking booking = bookingService.updateBooking(updateBookingDTO);
            return new ResponseEntity<>(BookingMapper.toDTO(booking), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Failed to update booking: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            log.error("Failed to update booking: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Failed to update booking", e);
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete a booking
     *
     * @param bookingId The ID of the booking to delete
     * @return A success or error response
     */
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long bookingId) {
        log.info("Received request to delete booking ID: {}", bookingId);
        
        if (bookingService.deleteBooking(bookingId)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get a booking by ID
     *
     * @param bookingId The ID of the booking to get
     * @return The booking, if found
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDTO> getBooking(@PathVariable Long bookingId) {
        log.info("Received request to get booking ID: {}", bookingId);
        
        return bookingService.findBookingById(bookingId)
                .map(booking -> new ResponseEntity<>(BookingMapper.toDTO(booking), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Get all bookings for a user
     *
     * @param userId The ID of the user
     * @return A list of bookings for the user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingDTO>> getUserBookings(@PathVariable Long userId) {
        log.info("Received request to get bookings for user ID: {}", userId);
        
        List<BookingDTO> bookings = bookingService.findBookingsByUserId(userId)
                .stream()
                .map(BookingMapper::toDTO)
                .toList();
        
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    /**
     * Get all bookings for a ride schedule
     *
     * @param rideScheduleId The ID of the ride schedule
     * @return A list of bookings for the ride schedule
     */
    @GetMapping("/ride/{rideScheduleId}")
    public ResponseEntity<List<BookingDTO>> getRideBookings(@PathVariable Long rideScheduleId) {
        log.info("Received request to get bookings for ride schedule ID: {}", rideScheduleId);
        
        List<BookingDTO> bookings = bookingService.findBookingsByRideScheduleId(rideScheduleId)
                .stream()
                .map(BookingMapper::toDTO)
                .toList();
        
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }
    
    /**
     * Accept a booking as the ride schedule owner
     *
     * @param bookingId The ID of the booking to accept
     * @return The updated booking or an error response
     */
    @PostMapping("/{bookingId}/accept")
    public ResponseEntity<?> acceptBooking(@PathVariable Long bookingId) {
        
        log.info("Received request to accept booking ID: {}", bookingId);
        
        try {
            Booking booking = bookingService.acceptBooking(bookingId);
            return new ResponseEntity<>(BookingMapper.toDTO(booking), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Failed to accept booking: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (DataOwnershipException e) {
            log.error("Failed to accept booking: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (IllegalStateException e) {
            log.error("Failed to accept booking: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Failed to accept booking", e);
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Reject a booking as the ride schedule owner
     *
     * @param bookingId The ID of the booking to reject
     * @return The updated booking or an error response
     */
    @PostMapping("/{bookingId}/reject")
    public ResponseEntity<?> rejectBooking(@PathVariable Long bookingId) {
        
        log.info("Received request to reject booking ID: {}", bookingId);
        
        try {
            Booking booking = bookingService.rejectBooking(bookingId);
            return new ResponseEntity<>(BookingMapper.toDTO(booking), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Failed to reject booking: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (DataOwnershipException e) {
            log.error("Failed to reject booking: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (IllegalStateException e) {
            log.error("Failed to reject booking: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Failed to reject booking", e);
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Cancel a booking as the booking owner
     *
     * @param bookingId The ID of the booking to cancel
     * @return The updated booking or an error response
     */
    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        
        log.info("Received request to cancel booking ID: {}", bookingId);
        
        try {
            Booking booking = bookingService.cancelBooking(bookingId);
            return new ResponseEntity<>(BookingMapper.toDTO(booking), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Failed to cancel booking: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (DataOwnershipException e) {
            log.error("Failed to cancel booking: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (IllegalStateException e) {
            log.error("Failed to cancel booking: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Failed to cancel booking", e);
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get all pending bookings for a ride schedule
     *
     * @param rideScheduleId The ID of the ride schedule
     * @return A list of pending bookings for the ride schedule
     */
    @GetMapping("/ride/{rideScheduleId}/pending")
    public ResponseEntity<?> getPendingBookings(@PathVariable Long rideScheduleId) {
        
        log.info("Received request to get pending bookings for ride schedule ID: {}", rideScheduleId);

        
        List<BookingDTO> pendingBookings = bookingService.findPendingBookingsByRideScheduleId(rideScheduleId)
                .stream()
                .map(BookingMapper::toDTO)
                .toList();
        
        return new ResponseEntity<>(pendingBookings, HttpStatus.OK);
    }
    
    /**
     * Get the total number of seats booked for a ride schedule
     *
     * @param rideScheduleId The ID of the ride schedule
     * @return The total number of seats booked
     */
    @GetMapping("/ride/{rideScheduleId}/seats")
    public ResponseEntity<Integer> getTotalSeatsBooked(@PathVariable Long rideScheduleId) {
        log.info("Received request to get total seats booked for ride schedule ID: {}", rideScheduleId);
        
        int totalSeatsBooked = bookingService.getTotalSeatsBooked(rideScheduleId);
        return new ResponseEntity<>(totalSeatsBooked, HttpStatus.OK);
    }
    
    /**
     * Get the total number of seats booked for a ride schedule on a specific day
     *
     * @param rideScheduleId The ID of the ride schedule
     * @param day The day of the week
     * @return The total number of seats booked for that day
     */
    @GetMapping("/ride/{rideScheduleId}/seats/{day}")
    public ResponseEntity<Integer> getTotalSeatsBookedForDay(
            @PathVariable Long rideScheduleId, 
            @PathVariable("day") DayOfWeek day) {
        log.info("Received request to get total seats booked for ride schedule ID: {} on day: {}", 
                rideScheduleId, day);
        
        int totalSeatsBookedForDay = bookingService.getTotalSeatsBookedForDay(rideScheduleId, day);
        return new ResponseEntity<>(totalSeatsBookedForDay, HttpStatus.OK);
    }
    
    /**
     * Get availability information for each day of a ride schedule
     *
     * @param rideScheduleId The ID of the ride schedule
     * @return Map of days to available seats
     */
    @GetMapping("/ride/{rideScheduleId}/availability")
    public ResponseEntity<Map<DayOfWeek, Integer>> getRideAvailability(@PathVariable Long rideScheduleId) {
        log.info("Received request to get availability for ride schedule ID: {}", rideScheduleId);
        
        // Get the ride schedule to check available seats and days
        Optional<RideSchedule> rideScheduleOpt = rideScheduleRepository.findById(rideScheduleId);
        
        if (rideScheduleOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        RideSchedule rideSchedule = rideScheduleOpt.get();
        
        if (!rideSchedule.isAvailable()) {
            return new ResponseEntity<>(Map.of(), HttpStatus.OK);
        }
        
        // Calculate availability for each day
        Map<DayOfWeek, Integer> availability = new HashMap<>();
        
        for (DayOfWeek day : rideSchedule.getDayList()) {
            int bookedSeats = bookingService.getTotalSeatsBookedForDay(rideScheduleId, day);
            int availableSeats = Math.max(0, rideSchedule.getAvailableSeats() - bookedSeats);
            availability.put(day, availableSeats);
        }
        
        return new ResponseEntity<>(availability, HttpStatus.OK);
    }
}