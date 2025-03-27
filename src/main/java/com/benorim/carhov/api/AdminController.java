package com.benorim.carhov.api;

import com.benorim.carhov.dto.auth.MessageResponseDTO;
import com.benorim.carhov.dto.user.CreateUserDTO;
import com.benorim.carhov.dto.user.UpdateUserDTO;
import com.benorim.carhov.dto.user.UserDTO;
import com.benorim.carhov.entity.CarHovUser;
import com.benorim.carhov.enums.RoleType;
import com.benorim.carhov.mapper.CarHovUserMapper;
import com.benorim.carhov.service.CarHovAdminUserService;
import com.benorim.carhov.service.CarHovUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole(T(com.benorim.carhov.enums.RoleType).ROLE_SUPER_ADMIN)")
@RequestMapping("/api/admin")
public class AdminController {

    private final CarHovAdminUserService adminUserService;
    private final CarHovUserService userService;

    @GetMapping("/users/board")
    public String adminAccess() {
        return "Admin Board.";
    }

    @PostMapping("/users/create")
    public ResponseEntity<?> createAdminUser(@Valid @RequestBody CreateUserDTO createUserDTO) {
        CarHovUser adminUser = CarHovUserMapper.toEntity(createUserDTO);
        adminUserService.createAdminUser(adminUser);
        return ResponseEntity.ok(new MessageResponseDTO("Admin user %s created successfully!".formatted(adminUser.getEmail())));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllAdminUsers() {
        List<CarHovUser> adminUsers = userService.findUsersByRole(RoleType.ROLE_ADMIN);
        List<UserDTO> adminUserDTOs = adminUsers.stream()
                .map(CarHovUserMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(adminUserDTOs);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getAdminUserById(@PathVariable Long id) {
        return userService.findUserById(id)
                .map(CarHovUserMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteAdminUser(@PathVariable Long id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDTO> updateAdminUser(@PathVariable Long id, @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        CarHovUser user = CarHovUserMapper.toEntity(updateUserDTO);
        return userService.updateUser(id, user)
                .map(CarHovUserMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
