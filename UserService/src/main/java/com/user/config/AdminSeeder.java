package com.user.config;

import com.user.entity.Role;
import com.user.entity.User;
import com.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Runs once every time the app starts. It only ever creates the admin if
 * NO admin exists yet (existsByRole check), so it's safe to restart the
 * service repeatedly - it will never create duplicates or reset the
 * password of an admin that already exists.
 */
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.default.username}")
    private String defaultAdminUsername;

    @Value("${admin.default.password}")
    private String defaultAdminPassword;
    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);
    @Override
    public void run(String... args) {
        if (userRepository.existsByRole(Role.ADMIN)) {
            log.info("Admin account already present, skipping seed.");
            return;
        }

        if (userRepository.existsByUsername(defaultAdminUsername)) {
            log.warn("A user named '{}' already exists but is not an ADMIN - not auto-promoting. " +
                    "Ask an existing admin to promote them via PUT /admin/users/{{id}}/role.", defaultAdminUsername);
            return;
        }

        userRepository.save(User.builder()
                .username(defaultAdminUsername)
                .password(passwordEncoder.encode(defaultAdminPassword))
                .role(Role.ADMIN)
                .build());

        log.warn("==============================================================");
        log.warn(" Seeded default ADMIN account -> username: '{}'", defaultAdminUsername);
        log.warn(" Log in once and change this password, or update it via");
        log.warn(" application.properties (admin.default.password) before prod use.");
        log.warn("==============================================================");
    }
}
