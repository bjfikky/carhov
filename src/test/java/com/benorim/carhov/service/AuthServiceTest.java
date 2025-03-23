package com.benorim.carhov.service;

import com.benorim.carhov.dto.auth.SignupRequestDTO;
import com.benorim.carhov.entity.CarHovUser;
import com.benorim.carhov.entity.Role;
import com.benorim.carhov.enums.RoleType;
import com.benorim.carhov.repository.CarHovUserRepository;
import com.benorim.carhov.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private CarHovUserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private AuthService authService;

    private SignupRequestDTO signupRequest;
    private Role userRole;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequestDTO();
        signupRequest.setDisplayName("Test User");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPhone("1234567890");
        signupRequest.setPassword("password123");

        userRole = Role.builder()
                .id(1L)
                .name(RoleType.ROLE_USER.name())
                .build();

        adminRole = Role.builder()
                .id(2L)
                .name(RoleType.ROLE_ADMIN.name())
                .build();
    }

    @Test
    void registerUser_WithNullRoles_SetsDefaultUserRole() {
        // Arrange
        signupRequest.setRoles(null);
        when(roleRepository.findByName(RoleType.ROLE_USER.name())).thenReturn(Optional.of(userRole));
        when(encoder.encode(any())).thenReturn("encoded-password");
        when(userRepository.save(any())).thenReturn(new CarHovUser());

        // Act
        authService.registerUser(signupRequest);

        // Verify
        verify(roleRepository).findByName(RoleType.ROLE_USER.name());
        verify(roleRepository, never()).findByName(RoleType.ROLE_ADMIN.name());
        verify(encoder).encode(signupRequest.getPassword());
        verify(userRepository).save(any());
    }

    @Test
    void registerUser_WithAdminRole_SetsAdminAndUserRoles() {
        // Arrange
        Set<String> roles = new HashSet<>();
        roles.add("admin");
        roles.add("user");
        signupRequest.setRoles(roles);
        when(roleRepository.findByName(RoleType.ROLE_ADMIN.name())).thenReturn(Optional.of(adminRole));
        when(roleRepository.findByName(RoleType.ROLE_USER.name())).thenReturn(Optional.of(userRole));
        when(encoder.encode(any())).thenReturn("encoded-password");
        when(userRepository.save(any())).thenReturn(new CarHovUser());

        // Act
        authService.registerUser(signupRequest);

        // Verify
        verify(roleRepository).findByName(RoleType.ROLE_ADMIN.name());
        verify(roleRepository).findByName(RoleType.ROLE_USER.name());
        verify(encoder).encode(signupRequest.getPassword());
        verify(userRepository).save(any());
    }

    @Test
    void registerUser_WithUnknownRole_SetsDefaultUserRole() {
        // Arrange
        Set<String> roles = new HashSet<>();
        roles.add("unknown");
        signupRequest.setRoles(roles);
        when(roleRepository.findByName(RoleType.ROLE_USER.name())).thenReturn(Optional.of(userRole));
        when(encoder.encode(any())).thenReturn("encoded-password");
        when(userRepository.save(any())).thenReturn(new CarHovUser());

        // Act
        authService.registerUser(signupRequest);

        // Verify
        verify(roleRepository, never()).findByName(RoleType.ROLE_ADMIN.name());
        verify(roleRepository).findByName(RoleType.ROLE_USER.name());
        verify(encoder).encode(signupRequest.getPassword());
        verify(userRepository).save(any());
    }

    @Test
    void registerUser_VerifyUserCreation() {
        // Arrange
        signupRequest.setRoles(null);
        when(roleRepository.findByName(RoleType.ROLE_USER.name())).thenReturn(Optional.of(userRole));
        when(encoder.encode(any())).thenReturn("encoded-password");
        when(userRepository.save(any())).thenReturn(new CarHovUser());

        // Act
        authService.registerUser(signupRequest);

        // Verify user creation
        ArgumentCaptor<CarHovUser> userCaptor = ArgumentCaptor.forClass(CarHovUser.class);
        verify(userRepository).save(userCaptor.capture());
        CarHovUser capturedUser = userCaptor.getValue();

        assertEquals(signupRequest.getDisplayName(), capturedUser.getDisplayName());
        assertEquals(signupRequest.getEmail(), capturedUser.getEmail());
        assertEquals(signupRequest.getPhone(), capturedUser.getPhone());
        assertEquals("encoded-password", capturedUser.getPassword());
        assertTrue(capturedUser.isEnabled());
        assertTrue(capturedUser.isAccountNonExpired());
        assertTrue(capturedUser.isAccountNonLocked());
        assertTrue(capturedUser.isCredentialsNonExpired());
        assertEquals(1, capturedUser.getRoles().size());
        assertTrue(capturedUser.getRoles().contains(userRole));
    }

    @Test
    void registerUser_RoleNotFound_ThrowsException() {
        // Arrange
        signupRequest.setRoles(null);
        when(roleRepository.findByName(RoleType.ROLE_USER.name())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.registerUser(signupRequest));
        assertEquals("Error: Role is not found.", exception.getMessage());

        // Verify
        verify(encoder).encode(signupRequest.getPassword());
        verify(roleRepository).findByName(RoleType.ROLE_USER.name());
        verifyNoMoreInteractions(roleRepository, encoder, userRepository);
    }
} 