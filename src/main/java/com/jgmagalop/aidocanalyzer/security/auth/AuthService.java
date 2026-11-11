package com.jgmagalop.aidocanalyzer.security.auth;

import com.jgmagalop.aidocanalyzer.security.jwt.JwtService;
import com.jgmagalop.aidocanalyzer.security.user.User;
import com.jgmagalop.aidocanalyzer.security.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest request) {
        return userRepository.findByEmailIgnoreCase(request.email().trim())
                .filter(User::isActive)
                .filter(user -> passwordEncoder.matches(request.password(), user.getPasswordHash()))
                .map(user -> new AuthResponse(
                        jwtService.generateToken(user),
                        user.getFirstName() + " " + user.getLastName(),
                        user.getRole().name()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
    }
}