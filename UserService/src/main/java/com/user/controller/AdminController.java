package com.user.controller;

import com.user.dto.RoleUpdateRequestDTO;
import com.user.dto.UserResponseDTO;
import com.user.entity.User;
import com.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Everything under /admin/** is locked to hasRole("ADMIN") in WebSecurityConfig.
 * This is the "full access" surface the ADMIN user gets.
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    @GetMapping("/users")
    public List<UserResponseDTO> listUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserResponseDTO(u.getId(), u.getUsername(), u.getRole().name()))
                .toList();
    }

    @GetMapping("/users/{id}")
    public UserResponseDTO getUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("No user with id " + id));
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getRole().name());
    }

    // Lets an admin promote/demote another user's role
    @PutMapping("/users/{id}/role")
    public UserResponseDTO updateRole(@PathVariable Long id, @RequestBody RoleUpdateRequestDTO body) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("No user with id " + id));
        user.setRole(body.getRole());
        userRepository.save(user);
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getRole().name());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
