package com.benorim.carhov.repository;

import com.benorim.carhov.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
    /**
     * Find all vehicles owned by a specific user
     * @param userId The ID of the user who owns the vehicles
     * @return List of vehicles owned by the user
     */
    List<Vehicle> findByUserId(Long userId);

    /**
     * Find vehicle by license plate
     * @param licensePlate The license plate to search for
     * @return Vehicle with the specified license plate
     */
    Vehicle findByLicensePlate(String licensePlate);
}
