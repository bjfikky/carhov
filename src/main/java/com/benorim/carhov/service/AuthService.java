package com.benorim.carhov.service;

import com.benorim.carhov.dto.auth.SignupRequestDTO;
import com.benorim.carhov.entity.CarHovUser;
import com.benorim.carhov.entity.Role;
import com.benorim.carhov.enums.RoleType;
import com.benorim.carhov.exception.DataOwnershipException;
import com.benorim.carhov.repository.CarHovUserRepository;
import com.benorim.carhov.repository.RoleRepository;
import com.benorim.carhov.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final CarHovUserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;

    public void registerUser(SignupRequestDTO signUpRequest) {
        // Create new user's account
        CarHovUser user = CarHovUser.builder()
                .displayName(signUpRequest.getDisplayName())
                .email(signUpRequest.getEmail())
                .phone(signUpRequest.getPhone())
                .password(encoder.encode(signUpRequest.getPassword()))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(RoleType.ROLE_USER.name())
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN.name())
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(RoleType.ROLE_USER.name())
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
    }

    public Long getSignedInUserId() {
        // Get the current authentication
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            log.error("No authentication found");
            throw new DataOwnershipException("No authentication found");
        }

        // Get the principal
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Check if principal is UserDetailsImpl
        if (!(principal instanceof UserDetailsImpl currentUser)) {
            log.error("Principal is not a UserDetailsImpl: {}", principal.getClass().getName());
            throw new DataOwnershipException("Principal is not a UserDetailsImpl");
        }

        return currentUser.getId();
    }

    public boolean isSuperAdmin() {
        return adminType(RoleType.ROLE_SUPER_ADMIN);
    }

    public boolean isAdmin() {
        return adminType(RoleType.ROLE_ADMIN);
    }

    public boolean isUser() {
        return adminType(RoleType.ROLE_USER);
    }

    public boolean isRequestMadeByLoggedInUserOrAdmin(CarHovUser user) {
        return isSuperAdmin() || isAdmin() || isRequestMadeByLoggedInUser(user);
    }

    public boolean isRequestMadeByLoggedInUser(CarHovUser user) {
        if (user == null) {
            throw new DataOwnershipException("User not found");
        }
        Long signedInUserId = getSignedInUserId();
        if (signedInUserId == null) {
            log.error("User is not signed in");
            throw new DataOwnershipException("User is not signed in");
        }

        if (!signedInUserId.equals(user.getId())) {
            log.error("User id mismatch");
            throw new DataOwnershipException("User id mismatch");
        }
        return true;
    }

    private static boolean adminType(RoleType roleType) {
        UserDetailsImpl currentUser = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals(roleType.name()));
    }
}
