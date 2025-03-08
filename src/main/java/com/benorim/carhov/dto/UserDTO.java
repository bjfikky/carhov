package com.benorim.carhov.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String displayName;
    private String email;
    private String phone;
    private boolean enabled;
    private boolean accountNonLocked;
    private boolean accountNonExpired;
    private LocalDateTime createdAt;
}