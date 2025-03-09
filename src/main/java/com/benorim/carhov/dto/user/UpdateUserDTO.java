package com.benorim.carhov.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO {
    private String displayName;
    private String email;
    private String phone;
    private String password;
    private Boolean enabled;
    private Boolean accountNonLocked;
    private Boolean accountNonExpired;
}