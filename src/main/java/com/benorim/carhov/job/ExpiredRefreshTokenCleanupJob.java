package com.benorim.carhov.job;

import com.benorim.carhov.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpiredRefreshTokenCleanupJob {

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 * * * ?")
    public void cleanUpExpiredRefreshTokens() {
        log.info("Starting cleanup of expired tokens ...");
        int deletedCount = refreshTokenRepository.deleteByExpiryDateBefore(Instant.now());
        log.info("Deleted {} expired tokens.", deletedCount);
    }
}
