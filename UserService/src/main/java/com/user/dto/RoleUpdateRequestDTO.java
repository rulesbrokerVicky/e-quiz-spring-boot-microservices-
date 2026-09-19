package com.user.dto;

import com.user.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoleUpdateRequestDTO {

    @NotNull(message = "role is required")
    private Role role;
}
