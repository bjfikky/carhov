package com.benorim.carhov.repository;

import com.benorim.carhov.entity.Booking;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends ListCrudRepository<Booking, Long> {
}
