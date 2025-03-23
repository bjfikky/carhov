package com.benorim.carhov.repository;

import com.benorim.carhov.entity.CarHovUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarHovUserRepository extends JpaRepository<CarHovUser, Long> {
    Optional<CarHovUser> findByEmail(String email);
    Boolean existsByEmail(String email);

    @Query("SELECT c FROM CarHovUser c INNER JOIN c.roles r WHERE r.name = :roleName")
    List<CarHovUser> findByRole(@Param("roleName") String roleName);
}
