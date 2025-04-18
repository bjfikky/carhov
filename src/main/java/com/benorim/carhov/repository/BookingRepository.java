package com.benorim.carhov.repository;

import com.benorim.carhov.entity.Booking;
import com.benorim.carhov.enums.BookingStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends ListCrudRepository<Booking, Long> {

    List<Booking> findByRideScheduleId(Long rideScheduleId);

    List<Booking> findByCarHovUserId(Long userId);

    List<Booking> findByCarHovUserIdAndRideScheduleId(Long userId, Long rideScheduleId);
    
    /**
     * Find bookings by ride schedule ID and status
     *
     * @param rideScheduleId ID of the ride schedule
     * @param status Status of the booking
     * @return List of bookings with the given status for the given ride schedule
     */
    List<Booking> findByRideScheduleIdAndStatus(Long rideScheduleId, BookingStatus status);
    
    /**
     * Find bookings by user ID and status
     *
     * @param userId ID of the user
     * @param status Status of the booking
     * @return List of bookings with the given status for the given user
     */
    List<Booking> findByCarHovUserIdAndStatus(Long userId, BookingStatus status);
    
    /**
     * Sum the total number of seats booked for a specific ride schedule
     * 
     * @param rideScheduleId ID of the ride schedule
     * @return Total number of seats booked
     */
    @Query("SELECT SUM(b.seatsBooked) FROM Booking b WHERE b.rideSchedule.id = :rideScheduleId AND (b.status = 'ACCEPTED' OR b.status = 'PENDING')")
    Integer sumSeatsBookedByRideScheduleId(Long rideScheduleId);
    
    /**
     * Sum the total number of seats booked for a specific ride schedule on a specific day
     * 
     * @param rideScheduleId ID of the ride schedule
     * @param day The day of the week (as a string)
     * @return Total number of seats booked for that day
     */
    @Query("SELECT SUM(b.seatsBooked) FROM Booking b WHERE b.rideSchedule.id = :rideScheduleId AND b.bookedDays LIKE CONCAT('%', :day, '%') AND (b.status = 'ACCEPTED' OR b.status = 'PENDING')")
    Integer sumSeatsBookedByRideScheduleIdAndDay(Long rideScheduleId, String day);
}
