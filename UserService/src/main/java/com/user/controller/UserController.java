package com.user.controller;

import com.user.dto.UserResponseDTO;
import com.user.entity.User;
import com.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // Any logged-in user (USER or ADMIN) can check who they are
    @GetMapping("/me")
    public UserResponseDTO me(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getRole().name());
    }
}
