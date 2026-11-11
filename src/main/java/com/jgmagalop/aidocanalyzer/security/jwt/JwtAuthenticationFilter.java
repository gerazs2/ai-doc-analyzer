package com.jgmagalop.aidocanalyzer.security.jwt;

import com.jgmagalop.aidocanalyzer.security.AuthenticatedUser;
import com.jgmagalop.aidocanalyzer.security.user.User;
import com.jgmagalop.aidocanalyzer.security.user.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Claims claims = jwtService.validate(token);
                Instant issuedAt = claims.getIssuedAt().toInstant();

                userRepository.findByEmailIgnoreCase(claims.getSubject())
                        .filter(User::isActive)
                        .filter(user -> isTokenStillValid(user, issuedAt))
                        .ifPresent(user -> authenticate(user, request));

            } catch (JwtException e) {
                log.debug("Token JWT rechazado: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isTokenStillValid(User user, Instant issuedAt) {
        Instant validSince = user.getTokensValidSince().truncatedTo(ChronoUnit.SECONDS);
        return !issuedAt.isBefore(validSince);
    }

    private void authenticate(User user, HttpServletRequest request) {
        AuthenticatedUser principal = new AuthenticatedUser(
                user.getId(), user.getEmail(), user.getRole(), user.getDepartment());

        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}