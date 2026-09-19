package com.user.security;

import com.user.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class AuthUtil {

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates the JWT that the Gateway will later validate.
     * The "role" claim (e.g. ROLE_ADMIN) is what the Gateway reads
     * and forwards downstream as the X-Auth-Roles header.
     */
    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId().toString())
                .claim("role", "ROLE_" + user.getRole().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSecretKey())
                .compact();
    }

    // NOTE: there is no token-parsing/validation method here on purpose.
    // In this architecture the Gateway is the single place that validates
    // JWTs (see ApiGateway's JwtUtil). UserService only ever *issues* tokens.
}
