package com.benorim.carhov.job;

import com.benorim.carhov.repository.UserNonceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpiredNonceCleanupJob {

    private final UserNonceRepository userNonceRepository;

    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanUpExpiredNonces() {
        log.info("Starting cleanup of expired nonces...");
        int deletedCount = userNonceRepository.deleteByExpiryDateBefore(Instant.now());
        log.info("Deleted {} expired nonces.", deletedCount);
    }
}
