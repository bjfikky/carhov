package com.benorim.carhov.repository;

import com.benorim.carhov.entity.RideSchedule;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideScheduleRepository extends ListCrudRepository<RideSchedule, Long> {
    
    /**
     * Find ride schedules by user ID
     * @param userId The ID of the user who created the ride schedules
     * @return List of ride schedules created by the user
     */
    List<RideSchedule> findByUserId(Long userId);
    
    /**
     * Find all ride schedules that are available
     * @return List of available ride schedules
     */
    List<RideSchedule> findByAvailableTrue();
}
