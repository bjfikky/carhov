package com.benorim.carhov.mapper;

import com.benorim.carhov.dto.CreateUserDTO;
import com.benorim.carhov.dto.UpdateUserDTO;
import com.benorim.carhov.dto.UserDTO;
import com.benorim.carhov.entity.CarHovUser;
import org.springframework.stereotype.Component;

@Component
public class CarHovUserMapper {

    public static CarHovUser toEntity(CreateUserDTO createUserDTO) {
        CarHovUser user = new CarHovUser();
        user.setDisplayName(createUserDTO.getDisplayName());
        user.setEmail(createUserDTO.getEmail());
        user.setPhone(createUserDTO.getPhone());
        user.setPassword(createUserDTO.getPassword());
        user.setEnabled(createUserDTO.isEnabled());
        user.setAccountNonLocked(createUserDTO.isAccountNonLocked());
        user.setAccountNonExpired(createUserDTO.isAccountNonExpired());
        return user;
    }

    public static CarHovUser toEntity(UpdateUserDTO updateUserDTO) {
        CarHovUser user = new CarHovUser();
        user.setDisplayName(updateUserDTO.getDisplayName());
        user.setEmail(updateUserDTO.getEmail());
        user.setPhone(updateUserDTO.getPhone());
        user.setPassword(updateUserDTO.getPassword());
        if (updateUserDTO.getEnabled() != null) {
            user.setEnabled(updateUserDTO.getEnabled());
        }
        if (updateUserDTO.getAccountNonLocked() != null) {
            user.setAccountNonLocked(updateUserDTO.getAccountNonLocked());
        }
        if (updateUserDTO.getAccountNonExpired() != null) {
            user.setAccountNonExpired(updateUserDTO.getAccountNonExpired());
        }
        return user;
    }

    public static UserDTO toDTO(CarHovUser user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setDisplayName(user.getDisplayName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone(user.getPhone());
        userDTO.setEnabled(user.isEnabled());
        userDTO.setAccountNonLocked(user.isAccountNonLocked());
        userDTO.setAccountNonExpired(user.isAccountNonExpired());
        userDTO.setCreatedAt(user.getCreatedAt());
        return userDTO;
    }
}