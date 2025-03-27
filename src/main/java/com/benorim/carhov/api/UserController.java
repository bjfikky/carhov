package com.benorim.carhov.api;

import com.benorim.carhov.aspect.RequireUserOwnership;
import com.benorim.carhov.dto.user.UpdateUserDTO;
import com.benorim.carhov.dto.user.UserDTO;
import com.benorim.carhov.entity.CarHovUser;
import com.benorim.carhov.mapper.CarHovUserMapper;
import com.benorim.carhov.service.CarHovUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final CarHovUserService carHovUserService;

    @PutMapping("/{userId}")
    @RequireUserOwnership
    public ResponseEntity<UserDTO> updateUser(@PathVariable("userId") Long userId, @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        log.info("Received request to update user with ID: {}", userId);

        CarHovUser user = CarHovUserMapper.toEntity(updateUserDTO);
        return carHovUserService.updateUser(userId, user)
                .map(updatedUser -> {
                    log.info("User {} successfully updated their profile", userId);
                    return new ResponseEntity<>(CarHovUserMapper.toDTO(updatedUser), HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId) {
        log.info("Received request to get user with ID: {}", userId);

        return carHovUserService.findUserById(userId)
                .map(user -> new ResponseEntity<>(CarHovUserMapper.toDTO(user), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @DeleteMapping("/{userId}")
    @RequireUserOwnership
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
        log.info("Received request to delete user with ID: {}", userId);

        if (carHovUserService.deleteUser(userId)) {
            log.info("User {} successfully deleted their profile", userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            log.warn("Failed to delete user profile for ID: {}", userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}