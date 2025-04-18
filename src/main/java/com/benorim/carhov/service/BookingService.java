package com.benorim.carhov.service;

import com.benorim.carhov.dto.booking.CreateBookingDTO;
import com.benorim.carhov.dto.booking.UpdateBookingDTO;
import com.benorim.carhov.entity.Booking;
import com.benorim.carhov.entity.CarHovUser;
import com.benorim.carhov.entity.RideSchedule;
import com.benorim.carhov.enums.BookingStatus;
import com.benorim.carhov.enums.DayOfWeek;
import com.benorim.carhov.exception.BookingException;
import com.benorim.carhov.exception.DataOwnershipException;
import com.benorim.carhov.mapper.BookingMapper;
import com.benorim.carhov.repository.BookingRepository;
import com.benorim.carhov.repository.CarHovUserRepository;
import com.benorim.carhov.repository.RideScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.benorim.carhov.enums.BookingStatus.PENDING;

/**
 * Service for managing ride bookings
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RideScheduleRepository rideScheduleRepository;
    private final CarHovUserRepository carHovUserRepository;
    private final AuthService authService;

    /**
     * Create a new booking
     *
     * @param createBookingDTO The booking data to create
     * @return The created booking
     * @throws IllegalArgumentException if the ride schedule or user doesn't exist
     * @throws IllegalStateException if there are not enough seats available
     */
    @Transactional
    public Booking createBooking(CreateBookingDTO createBookingDTO) {
        log.info("Creating new booking for user ID: {} and ride schedule ID: {}", 
                createBookingDTO.getUserId(), createBookingDTO.getRideScheduleId());

        RideSchedule rideSchedule = rideScheduleRepository.findById(createBookingDTO.getRideScheduleId())
                .orElseThrow(() -> new IllegalArgumentException("Ride schedule not found"));

        if (!rideSchedule.isAvailable()) {
            throw new IllegalStateException("Ride is not available for booking");
        }

        CarHovUser user = carHovUserRepository.findById(createBookingDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Ensure that the signed-in user is creating the booking for their self
        if (!user.getId().equals(authService.getSignedInUserId())) {
            throw new DataOwnershipException("User is not authorized to create this booking");
        }

        // Check if the requested days are valid for this ride schedule
        validateRequestedDays(createBookingDTO.getBookedDays(), rideSchedule.getDayList());
        
        // Check if user has already booked this ride for any of the requested days
        List<Booking> existingBookings = bookingRepository.findByCarHovUserIdAndRideScheduleId(user.getId(), rideSchedule.getId());
        if (!existingBookings.isEmpty()) {
            // Check for day conflicts
            Set<DayOfWeek> requestedDays = new HashSet<>(createBookingDTO.getBookedDays());
            
            for (Booking existingBooking : existingBookings) {
                List<DayOfWeek> existingDays = BookingMapper.convertStringToDayList(existingBooking.getBookedDays());
                
                for (DayOfWeek day : existingDays) {
                    if (requestedDays.contains(day)) {
                        throw new BookingException("User has already booked this ride on " + day);
                    }
                }
            }
        }
        
        // Check if there are enough seats available for each requested day
        for (DayOfWeek day : createBookingDTO.getBookedDays()) {
            Integer seatsBookedForDay = bookingRepository.sumSeatsBookedByRideScheduleIdAndDay(
                    rideSchedule.getId(), day.name());
            int totalSeatsBookedForDay = (seatsBookedForDay != null) ? seatsBookedForDay : 0;
            
            if (rideSchedule.getAvailableSeats() < totalSeatsBookedForDay + createBookingDTO.getSeatsBooked()) {
                throw new IllegalStateException("Not enough seats available for " + day.name());
            }
        }
        
        // Convert days list to string
        String bookedDaysString = BookingMapper.convertDayListToString(createBookingDTO.getBookedDays());
        
        // Create the booking with default PENDING status
        Booking booking = Booking.builder()
                .rideSchedule(rideSchedule)
                .carHovUser(user)
                .seatsBooked(createBookingDTO.getSeatsBooked())
                .bookedDays(bookedDaysString)
                .status(PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        
        return bookingRepository.save(booking);
    }

    /**
     * Update an existing booking
     *
     * @param updateBookingDTO The updated booking data
     * @return The updated booking
     * @throws IllegalArgumentException if the booking doesn't exist
     * @throws IllegalStateException if there are not enough seats available
     */
    @Transactional
    public Booking updateBooking(UpdateBookingDTO updateBookingDTO) {
        // Updating a booking might be unnecessary. Better to cancel a booking and make a new one
        throw new NotImplementedException("Might be unnecessary");

//        log.info("Updating booking ID: {}", updateBookingDTO.getId());
//
//        // Get the booking
//        Booking booking = bookingRepository.findById(updateBookingDTO.getId())
//                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

//
//        RideSchedule rideSchedule = booking.getRideSchedule();
//
//        // Check if the ride is still available
//        if (!rideSchedule.isAvailable()) {
//            throw new IllegalStateException("Ride is no longer available");
//        }
//
//        // Get current booked days
//        List<DayOfWeek> currentBookedDays = BookingMapper.convertStringToDayList(booking.getBookedDays());
//
//        // Check if the requested days are valid for this ride schedule
//        validateRequestedDays(updateBookingDTO.getBookedDays(), rideSchedule.getDayList());
//
//        // Check for conflicts with other bookings by this user for the same ride
//        List<Booking> otherBookings = bookingRepository
//                .findByCarHovUserIdAndRideScheduleId(booking.getCarHovUser().getId(), rideSchedule.getId());
//
//        Set<DayOfWeek> requestedDays = new HashSet<>(updateBookingDTO.getBookedDays());
//
//        for (Booking otherBooking : otherBookings) {
//            // Skip the current booking
//            if (otherBooking.getId().equals(booking.getId())) {
//                continue;
//            }
//
//            List<DayOfWeek> existingDays = BookingMapper.convertStringToDayList(otherBooking.getBookedDays());
//
//            for (DayOfWeek day : existingDays) {
//                if (requestedDays.contains(day)) {
//                    throw new IllegalStateException("User has already booked this ride on " + day + " in another booking");
//                }
//            }
//        }
//
//        // Calculate the difference in seats
//        int seatsDifference = updateBookingDTO.getSeatsBooked() - booking.getSeatsBooked();
//
//        // If increasing seats or changing days, check availability for each requested day
//        if (seatsDifference > 0 || !currentBookedDays.equals(updateBookingDTO.getBookedDays())) {
//            for (DayOfWeek day : updateBookingDTO.getBookedDays()) {
//                // If this day was already booked, we need to account for the existing booking
//                boolean dayAlreadyBooked = currentBookedDays.contains(day);
//
//                Integer seatsBookedForDay = bookingRepository.sumSeatsBookedByRideScheduleIdAndDay(
//                        rideSchedule.getId(), day.name());
//                int totalSeatsBookedForDay = (seatsBookedForDay != null) ? seatsBookedForDay : 0;
//
//                // If the day was already booked, subtract the current booking's seats
//                if (dayAlreadyBooked) {
//                    totalSeatsBookedForDay -= booking.getSeatsBooked();
//                }
//
//                if (rideSchedule.getAvailableSeats() < totalSeatsBookedForDay + updateBookingDTO.getSeatsBooked()) {
//                    throw new IllegalStateException("Not enough seats available for " + day.name());
//                }
//            }
//        }
//
//        // Convert days list to string
//        String bookedDaysString = BookingMapper.convertDayListToString(updateBookingDTO.getBookedDays());
//
//        // Update the booking
//        booking.setSeatsBooked(updateBookingDTO.getSeatsBooked());
//        booking.setBookedDays(bookedDaysString);
//
//        return bookingRepository.save(booking);
    }

    /**
     * Delete a booking
     *
     * @param bookingId The ID of the booking to delete
     * @return true if the booking was deleted, false otherwise
     */
    @Transactional
    public boolean deleteBooking(Long bookingId) {
        log.info("Deleting booking ID: {}", bookingId);

        return bookingRepository.findById(bookingId)
                .map(booking -> {
                    Long bookerId = booking.getCarHovUser().getId();
                    if (bookerId.equals(authService.getSignedInUserId())) {
                        throw new DataOwnershipException("User is not authorized to delete this booking");
                    }
                    // TODO: rather than delete, let's cancel? API that uses this service can be deleted and this can be used for clean up job
                    bookingRepository.delete(booking);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Get a booking by ID
     *
     * @param bookingId The ID of the booking to get
     * @return The booking, if found
     */
    public Optional<Booking> findBookingById(Long bookingId) {
        log.info("Finding booking ID: {}", bookingId);
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        Booking booking = optionalBooking.orElse(null);
        Long userId = booking != null ? booking.getCarHovUser().getId() : null;
        if (!authService.getSignedInUserId().equals(userId)) {
            throw new DataOwnershipException("User is not authorized to view this booking");
        }

        return optionalBooking;
    }

    /**
     * Get all bookings for a user
     *
     * @param userId The ID of the user
     * @return A list of bookings for the user
     */
    public List<Booking> findBookingsByUserId(Long userId) {
        log.info("Finding bookings for user ID: {}", userId);
        //TODO: Ensure booking belongs to logged in user

        return bookingRepository.findByCarHovUserId(userId);
    }

    /**
     * Get all bookings for a ride schedule
     *
     * @param rideScheduleId The ID of the ride schedule
     * @return A list of bookings for the ride schedule
     */
    public List<Booking> findBookingsByRideScheduleId(Long rideScheduleId) {
        log.info("Finding bookings for ride schedule ID: {}", rideScheduleId);
        return bookingRepository.findByRideScheduleId(rideScheduleId);
    }
    
    /**
     * Get the total number of seats booked for a ride schedule
     *
     * @param rideScheduleId The ID of the ride schedule
     * @return The total number of seats booked
     */
    public int getTotalSeatsBooked(Long rideScheduleId) {
        Integer seatsBooked = bookingRepository.sumSeatsBookedByRideScheduleId(rideScheduleId);
        return (seatsBooked != null) ? seatsBooked : 0;
    }
    
    /**
     * Get the total number of seats booked for a ride schedule on a specific day
     *
     * @param rideScheduleId The ID of the ride schedule
     * @param day The day of the week
     * @return The total number of seats booked for that day
     */
    public int getTotalSeatsBookedForDay(Long rideScheduleId, DayOfWeek day) {
        Integer seatsBooked = bookingRepository.sumSeatsBookedByRideScheduleIdAndDay(rideScheduleId, day.name());
        return (seatsBooked != null) ? seatsBooked : 0;
    }
    
    /**
     * Accept a booking by the ride schedule owner
     *
     * @param bookingId The ID of the booking to accept
     * @return The updated booking
     * @throws IllegalArgumentException if the booking doesn't exist
     * @throws DataOwnershipException if the user is not the owner of the ride schedule
     */
    @Transactional
    public Booking acceptBooking(Long bookingId) {
        Long userId = authService.getSignedInUserId();
        log.info("Accepting booking ID: {} by user ID: {}", bookingId, userId);
        
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        
        RideSchedule rideSchedule = booking.getRideSchedule();
        
        // Verify that the user is the owner of the ride schedule
        if (!rideSchedule.getUser().getId().equals(userId)) {
            throw new DataOwnershipException("User is not the owner of this ride schedule");
        }
        
        // Verify the booking is in PENDING status
        if (booking.getStatus() != PENDING) {
            throw new IllegalStateException(
                    "Booking cannot be accepted because it is in " + booking.getStatus() + " status");
        }
        
        // Update the booking status
        booking.setStatus(BookingStatus.ACCEPTED);
        booking.setStatusUpdateDate(LocalDateTime.now());
        
        return bookingRepository.save(booking);
    }
    
    /**
     * Reject a booking by the ride schedule owner
     *
     * @param bookingId The ID of the booking to reject
     * @return The updated booking
     * @throws IllegalArgumentException if the booking doesn't exist
     * @throws DataOwnershipException if the user is not the owner of the ride schedule
     */
    @Transactional
    public Booking rejectBooking(Long bookingId) {
        Long userId = authService.getSignedInUserId();
        log.info("Rejecting booking ID: {} by user ID: {}", bookingId, userId);
        
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        
        RideSchedule rideSchedule = booking.getRideSchedule();
        
        // Verify that the user is the owner of the ride schedule
        if (!rideSchedule.getUser().getId().equals(userId)) {
            throw new DataOwnershipException("User is not the owner of this ride schedule");
        }
        
        // Verify the booking is in PENDING status
        if (booking.getStatus() != PENDING) {
            throw new IllegalStateException(
                    "Booking cannot be rejected because it is in " + booking.getStatus() + " status");
        }
        
        // Update the booking status
        booking.setStatus(BookingStatus.REJECTED);
        booking.setStatusUpdateDate(LocalDateTime.now());
        
        return bookingRepository.save(booking);
    }
    
    /**
     * Get all pending bookings for a ride schedule
     *
     * @param rideScheduleId The ID of the ride schedule
     * @return A list of pending bookings for the ride schedule
     */
    public List<Booking> findPendingBookingsByRideScheduleId(Long rideScheduleId) {
        log.info("Finding pending bookings for ride schedule ID: {}", rideScheduleId);
        RideSchedule rideSchedule = rideScheduleRepository.findById(rideScheduleId).orElseThrow(() -> new IllegalArgumentException("Ride schedule not found"));
        Long loggedInUserId = authService.getSignedInUserId();
        if (!loggedInUserId.equals(rideSchedule.getUser().getId())) {
            throw new DataOwnershipException("User is not the owner of this ride schedule");
        }
        return bookingRepository.findByRideScheduleIdAndStatus(rideScheduleId, PENDING);
    }
    
    /**
     * Cancel a booking by the booking owner
     *
     * @param bookingId The ID of the booking to cancel
     * @return The updated booking
     * @throws IllegalArgumentException if the booking doesn't exist
     * @throws DataOwnershipException if the user is not the owner of the booking
     */
    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Long userId = authService.getSignedInUserId();
        log.info("Cancelling booking ID: {} by user ID: {}", bookingId, userId);
        
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        
        // Verify that the user is the owner of the booking
        if (!booking.getCarHovUser().getId().equals(userId)) {
            throw new DataOwnershipException("User is not the owner of this booking");
        }
        
        // Verify the booking is not already cancelled
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }
        
        // Update the booking status
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationDate(LocalDateTime.now());
        booking.setStatusUpdateDate(LocalDateTime.now());
        
        return bookingRepository.save(booking);
    }
    
    /**
     * Validate that the requested days are a subset of the available days
     *
     * @param requestedDays The days requested for booking
     * @param availableDays The days available for the ride schedule
     * @throws IllegalArgumentException if any requested day is not available
     */
    private void validateRequestedDays(List<DayOfWeek> requestedDays, List<DayOfWeek> availableDays) {
        Set<DayOfWeek> availableDaysSet = new HashSet<>(availableDays);
        
        for (DayOfWeek day : requestedDays) {
            if (!availableDaysSet.contains(day)) {
                throw new IllegalArgumentException("Day " + day + " is not available for this ride schedule");
            }
        }
    }
}