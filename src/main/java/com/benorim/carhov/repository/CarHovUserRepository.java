package com.benorim.carhov.repository;

import com.benorim.carhov.entity.CarHovUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarHovUserRepository extends JpaRepository<CarHovUser, Long> {
    Optional<CarHovUser> findByEmail(String email);
    Boolean existsByEmail(String email);
}
