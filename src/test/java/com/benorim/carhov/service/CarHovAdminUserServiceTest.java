package com.benorim.carhov.service;

import com.benorim.carhov.entity.CarHovUser;
import com.benorim.carhov.entity.Role;
import com.benorim.carhov.entity.UserNonce;
import com.benorim.carhov.enums.RoleType;
import com.benorim.carhov.repository.RoleRepository;
import com.benorim.carhov.repository.UserNonceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarHovAdminUserServiceTest {

    @Mock
    private CarHovUserService carHovUserService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserNonceRepository userNonceRepository;

    @InjectMocks
    private CarHovAdminUserService adminUserService;

    private CarHovUser testUser;
    private Role adminRole;
    private Role userRole;
    private UserNonce testNonce;

    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        testUser = CarHovUser.builder()
                .displayName("Test Admin")
                .email("admin@test.com")
                .phone("1234567890")
                .password("password")
                .enabled(true)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .build();

        adminRole = Role.builder()
                .id(1L)
                .name(RoleType.ROLE_ADMIN.name())
                .build();

        userRole = Role.builder()
                .id(2L)
                .name(RoleType.ROLE_USER.name())
                .build();

        testNonce = UserNonce.builder()
                .id(1L)
                .nonce("test-nonce")
                .createdAt(LocalDateTime.now())
                .expiryDate(Instant.now().plusSeconds(3600))
                .user(testUser)
                .build();

        // Set the nonceDurationMs field using reflection
        Field nonceField = CarHovAdminUserService.class.getDeclaredField("nonceDurationMs");
        nonceField.setAccessible(true);
        nonceField.set(adminUserService, 3600L); // Set the expiration to 1 hour
    }

    @Test
    void createAdminUser_Success() {
        // Arrange
        when(roleRepository.findByName(RoleType.ROLE_ADMIN.name())).thenReturn(Optional.of(adminRole));
        when(roleRepository.findByName(RoleType.ROLE_USER.name())).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(CarHovAdminUserService.DEFAULT_ADMIN_PASSWORD)).thenReturn("encoded-password");
        when(carHovUserService.createUser(any(CarHovUser.class))).thenReturn(testUser);
        when(userNonceRepository.save(any(UserNonce.class))).thenReturn(testNonce);

        // Act
        CarHovUser result = adminUserService.createAdminUser(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getDisplayName(), result.getDisplayName());
        assertEquals(2, result.getRoles().size());
        assertTrue(result.getRoles().contains(adminRole));
        assertTrue(result.getRoles().contains(userRole));
        assertEquals("encoded-password", result.getPassword());
        assertFalse(result.isEnabled());

        // Verify
        verify(roleRepository).findByName(RoleType.ROLE_ADMIN.name());
        verify(roleRepository).findByName(RoleType.ROLE_USER.name());
        verify(passwordEncoder).encode(CarHovAdminUserService.DEFAULT_ADMIN_PASSWORD);
        verify(carHovUserService).createUser(any(CarHovUser.class));
        verify(userNonceRepository).save(any(UserNonce.class));
    }

    @Test
    void createAdminUser_AdminRoleNotFound_ThrowsException() {
        // Arrange
        when(roleRepository.findByName(RoleType.ROLE_ADMIN.name())).thenReturn(Optional.empty());

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> adminUserService.createAdminUser(testUser));
        assertEquals("ROLE_ADMIN does not exist", exception.getMessage());

        // Verify
        verify(roleRepository).findByName(RoleType.ROLE_ADMIN.name());
        verifyNoMoreInteractions(roleRepository, passwordEncoder, carHovUserService, userNonceRepository);
    }

    @Test
    void createAdminUser_UserRoleNotFound_ThrowsException() {
        // Arrange
        when(roleRepository.findByName(RoleType.ROLE_ADMIN.name())).thenReturn(Optional.of(adminRole));
        when(roleRepository.findByName(RoleType.ROLE_USER.name())).thenReturn(Optional.empty());

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> adminUserService.createAdminUser(testUser));
        assertEquals("ROLE_USER does not exist", exception.getMessage());

        // Verify
        verify(roleRepository).findByName(RoleType.ROLE_ADMIN.name());
        verify(roleRepository).findByName(RoleType.ROLE_USER.name());
        verifyNoMoreInteractions(roleRepository, passwordEncoder, carHovUserService, userNonceRepository);
    }

    @Test
    void createAdminUser_VerifyUserNonceCreation() {
        // Arrange
        when(roleRepository.findByName(RoleType.ROLE_ADMIN.name())).thenReturn(Optional.of(adminRole));
        when(roleRepository.findByName(RoleType.ROLE_USER.name())).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(CarHovAdminUserService.DEFAULT_ADMIN_PASSWORD)).thenReturn("encoded-password");
        when(carHovUserService.createUser(any(CarHovUser.class))).thenReturn(testUser);
        when(userNonceRepository.save(any(UserNonce.class))).thenReturn(testNonce);

        // Act
        adminUserService.createAdminUser(testUser);

        // Verify UserNonce creation
        ArgumentCaptor<UserNonce> nonceCaptor = ArgumentCaptor.forClass(UserNonce.class);
        verify(userNonceRepository).save(nonceCaptor.capture());
        UserNonce capturedNonce = nonceCaptor.getValue();

        assertNotNull(capturedNonce.getNonce());
        assertNotNull(capturedNonce.getCreatedAt());
        assertNotNull(capturedNonce.getExpiryDate());
        assertEquals(testUser, capturedNonce.getUser());
    }
} 