package com.benorim.carhov.repository;

import com.benorim.carhov.entity.CarHovUser;
import com.benorim.carhov.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Transactional
    int deleteByExpiryDateBefore(Instant now);
    
    @Transactional
    int deleteByUser(CarHovUser user);
}