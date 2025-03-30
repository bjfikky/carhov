package com.benorim.carhov.service;

import com.benorim.carhov.entity.CarHovUser;
import com.benorim.carhov.repository.CarHovUserRepository;
import com.benorim.carhov.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarHovUserServiceTest {

    @Mock
    private CarHovUserRepository carHovUserRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CarHovUserService carHovUserService;

    private CarHovUser existingUser;
    private CarHovUser updatedUser;

    @BeforeEach
    void setUp() {
        existingUser = new CarHovUser();
        existingUser.setId(1L);
        existingUser.setDisplayName("Old Name");
        existingUser.setEmail("old@example.com");
        existingUser.setPhone("1234567890");
        existingUser.setPassword("oldpassword");
        existingUser.setEnabled(true);
        existingUser.setAccountNonLocked(true);
        existingUser.setAccountNonExpired(true);

        updatedUser = new CarHovUser();
        updatedUser.setDisplayName("New Name");
        updatedUser.setEmail("new@example.com");
        updatedUser.setPhone("0987654321");
        updatedUser.setPassword("newpassword");
        updatedUser.setEnabled(false);
        updatedUser.setAccountNonLocked(false);
        updatedUser.setAccountNonExpired(false);
    }

    @Test
    void updateUser_UserExists_ShouldUpdateUser() {
        when(carHovUserRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(carHovUserRepository.save(any(CarHovUser.class))).thenReturn(updatedUser);

        Optional<CarHovUser> result = carHovUserService.updateUser(1L, updatedUser);

        assertTrue(result.isPresent());
        assertEquals("New Name", result.get().getDisplayName());
        assertEquals("new@example.com", result.get().getEmail());
        assertEquals("0987654321", result.get().getPhone());
        assertEquals("newpassword", result.get().getPassword());
        assertFalse(result.get().isEnabled());
        assertFalse(result.get().isAccountNonLocked());
        assertFalse(result.get().isAccountNonExpired());

        verify(carHovUserRepository).findById(1L);
        verify(carHovUserRepository).save(existingUser);
    }

    @Test
    void updateUser_UserDoesNotExist_ShouldReturnEmpty() {
        when(carHovUserRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<CarHovUser> result = carHovUserService.updateUser(2L, updatedUser);

        assertFalse(result.isPresent());
        verify(carHovUserRepository).findById(2L);
        verify(carHovUserRepository, never()).save(any());
    }

    @Test
    void findUserById_UserExists_ShouldReturnUser() {
        when(carHovUserRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        Optional<CarHovUser> result = carHovUserService.findUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(existingUser, result.get());

        verify(carHovUserRepository).findById(1L);
    }

    @Test
    void findUserById_UserDoesNotExist_ShouldReturnEmpty() {
        when(carHovUserRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<CarHovUser> result = carHovUserService.findUserById(2L);

        assertFalse(result.isPresent());
        verify(carHovUserRepository).findById(2L);
    }

    @Test
    void deleteUser_UserExists_ShouldReturnTrue() {
        when(carHovUserRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        doNothing().when(carHovUserRepository).delete(existingUser);

        boolean result = carHovUserService.deleteUser(1L);

        assertTrue(result);
        verify(carHovUserRepository).findById(1L);
        verify(carHovUserRepository).delete(existingUser);
    }

    @Test
    void deleteUser_UserDoesNotExist_ShouldReturnFalse() {
        when(carHovUserRepository.findById(2L)).thenReturn(Optional.empty());

        boolean result = carHovUserService.deleteUser(2L);

        assertFalse(result);
        verify(carHovUserRepository).findById(2L);
        verify(carHovUserRepository, never()).delete(any());
    }

}