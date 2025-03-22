package com.benorim.carhov.config;

import com.benorim.carhov.entity.CarHovUser;
import com.benorim.carhov.entity.Role;
import com.benorim.carhov.enums.RoleType;
import com.benorim.carhov.repository.CarHovUserRepository;
import com.benorim.carhov.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final CarHovUserRepository carHovUserRepository;
    private final PasswordEncoder encoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            log.info("Initializing roles...");
            createRoleIfNotExists(RoleType.ROLE_USER.name());
            createRoleIfNotExists(RoleType.ROLE_ADMIN.name());
            createRoleIfNotExists(RoleType.ROLE_SUPER_ADMIN.name());
            createSuperAdminUser();
            log.info("Data initialization completed.");
        };
    }

    private void createRoleIfNotExists(String roleName) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = Role.builder().name(roleName).build();
            roleRepository.save(role);
            log.info("Created role: {}", roleName);
        }
    }

    private void createSuperAdminUser() {
        String superAdminEmail = "superadmin@carhov.com";
        if (!carHovUserRepository.existsByEmail(superAdminEmail)) {
            Role superAdminRole = roleRepository.findByName(RoleType.ROLE_SUPER_ADMIN.name())
                    .orElseThrow(() -> new IllegalStateException("ROLE_SUPER_ADMIN does not exist"));

            CarHovUser carHovUser = CarHovUser.builder()
                    .email(superAdminEmail)
                    .phone("2404748000")
                    .createdAt(LocalDateTime.now())
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .enabled(true)
                    .credentialsNonExpired(true)
                    .displayName("Admin")
                    .roles(Set.of(superAdminRole)) // Use the existing role
                    .password(encoder.encode("password1234"))
                    .build();
            carHovUserRepository.save(carHovUser);
            log.info("Created super admin user: {}", superAdminEmail);
        }
    }
}