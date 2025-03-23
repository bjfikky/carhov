package com.benorim.carhov.repository;

import com.benorim.carhov.entity.UserNonce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Repository
public interface UserNonceRepository extends JpaRepository<UserNonce, Long> {
    @Transactional
    int deleteByExpiryDateBefore(Instant now);
}
