package com.user.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This service does NOT parse JWTs itself. The ApiGateway is the single place
 * that validates the token; once valid, it forwards these two headers to every
 * downstream service. Because these services should only ever be reachable
 * through the Gateway (not exposed directly on the public internet), trusting
 * these headers here is safe.
 *
 * X-Auth-Username: the authenticated user's username
 * X-Auth-Roles:    comma-separated roles, e.g. "ROLE_ADMIN" or "ROLE_USER"
 */
@Component
public class HeaderAuthFilter extends OncePerRequestFilter {

    public static final String HEADER_USERNAME = "X-Auth-Username";
    public static final String HEADER_ROLES = "X-Auth-Roles";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String username = request.getHeader(HEADER_USERNAME);
        String rolesHeader = request.getHeader(HEADER_ROLES);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            List<GrantedAuthority> authorities = rolesHeader == null || rolesHeader.isBlank()
                    ? List.of()
                    : Arrays.stream(rolesHeader.split(","))
                    .map(String::trim)
                    .filter(r -> !r.isEmpty())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
