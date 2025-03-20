package com.benorim.carhov.repository;

import com.benorim.carhov.entity.Vehicle;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends ListCrudRepository<Vehicle, Long> {

    List<Vehicle> findByUserId(Long userId);
}
