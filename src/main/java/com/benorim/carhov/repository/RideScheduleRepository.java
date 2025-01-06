package com.benorim.carhov.repository;

import com.benorim.carhov.entity.RideSchedule;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideScheduleRepository extends ListCrudRepository<RideSchedule, Long> {
}
