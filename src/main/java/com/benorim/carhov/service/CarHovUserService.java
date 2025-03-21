package com.benorim.carhov.service;

import com.benorim.carhov.entity.CarHovUser;
import com.benorim.carhov.repository.CarHovUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CarHovUserService {
    
    private final CarHovUserRepository carHovUserRepository;
    
    public Optional<CarHovUser> updateUser(Long userId, CarHovUser updatedUser) {
        log.info("Updating user with ID: {}", userId);
        return carHovUserRepository.findById(userId)
                .map(existingUser -> {
                    if (updatedUser.getDisplayName() != null) {
                        existingUser.setDisplayName(updatedUser.getDisplayName());
                    }
                    if (updatedUser.getEmail() != null) {
                        existingUser.setEmail(updatedUser.getEmail());
                    }
                    if (updatedUser.getPhone() != null) {
                        existingUser.setPhone(updatedUser.getPhone());
                    }
                    if (updatedUser.getPassword() != null) {
                        existingUser.setPassword(updatedUser.getPassword());
                    }
                    existingUser.setEnabled(updatedUser.isEnabled());
                    existingUser.setAccountNonLocked(updatedUser.isAccountNonLocked());
                    existingUser.setAccountNonExpired(updatedUser.isAccountNonExpired());
                    
                    return carHovUserRepository.save(existingUser);
                });
    }
    
    public Optional<CarHovUser> findUserById(Long userId) {
        log.info("Finding user with ID: {}", userId);
        return carHovUserRepository.findById(userId);
    }

    public boolean deleteUser(Long userId) {
        log.info("Deleting user with ID: {}", userId);
        return carHovUserRepository.findById(userId)
                .map(user -> {
                    carHovUserRepository.delete(user);
                    return true;
                })
                .orElse(false);
    }
}
