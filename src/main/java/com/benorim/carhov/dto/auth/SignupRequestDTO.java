package com.benorim.carhov.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDTO {
    @NotBlank
    private String displayName;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    private String phone;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private Set<String> roles;
}