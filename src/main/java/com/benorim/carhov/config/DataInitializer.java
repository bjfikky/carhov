package com.benorim.carhov.config;

import com.benorim.carhov.entity.Role;
import com.benorim.carhov.enums.RoleType;
import com.benorim.carhov.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final RoleRepository roleRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            log.info("Initializing roles...");
            createRoleIfNotExists(RoleType.ROLE_USER.name());
            createRoleIfNotExists(RoleType.ROLE_ADMIN.name());
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
}