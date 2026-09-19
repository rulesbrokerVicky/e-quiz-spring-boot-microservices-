package com.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {

    @NotBlank(message = "username is required")
    private String username;

    @NotBlank(message = "password is required")
    @Size(min = 4, message = "password must be at least 4 characters")
    private String password;

    // NOTE: there is deliberately no "role" field here.
    // Every self-registered user becomes ROLE_USER - see AuthService.signUp().
    // This stops someone from posting {"role":"ADMIN"} to make themselves an admin.
}
