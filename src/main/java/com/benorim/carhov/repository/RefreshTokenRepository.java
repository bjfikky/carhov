package com.benorim.carhov.repository;

import com.benorim.carhov.entity.RefreshToken;
import com.benorim.carhov.entity.CarHovUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    
    @Modifying
    int deleteByUser(CarHovUser user);
}