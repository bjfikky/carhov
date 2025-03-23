package com.benorim.carhov.service;

import com.benorim.carhov.entity.CarHovUser;
import com.benorim.carhov.entity.Role;
import com.benorim.carhov.entity.UserNonce;
import com.benorim.carhov.enums.RoleType;
import com.benorim.carhov.repository.RoleRepository;
import com.benorim.carhov.repository.UserNonceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CarHovAdminUserService {

    @Value("${app.nonce.expirationMs}")
    private Long nonceDurationMs;
    public static final String DEFAULT_ADMIN_PASSWORD = "defaultPasswordToBeReset";

    private final CarHovUserService carHovUserService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserNonceRepository userNonceRepository;

    @Transactional
    public CarHovUser createAdminUser(CarHovUser user) {
        Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN.name())
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN does not exist"));

        Role userRole = roleRepository.findByName(RoleType.ROLE_USER.name())
                .orElseThrow(() -> new IllegalStateException("ROLE_USER does not exist"));

        user.setRoles(Set.of(adminRole, userRole));
        user.setPassword(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
        user.setEnabled(false);
        log.info("Creating admin user: {}", user);
        CarHovUser createdUser = carHovUserService.createUser(user);

        UserNonce userNonce = UserNonce.builder()
                .nonce(UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .expiryDate(Instant.now().plusSeconds(nonceDurationMs))
                .user(createdUser)
                .build();
        UserNonce savedNonce = userNonceRepository.save(userNonce);

        // TODO: Here, an email should be sent to the user. The email should have a link with a nonce
        return createdUser;
    }
}
