package com.studywithmochi.backend.auth;

import com.studywithmochi.backend.auth.dto.LoginRequest;
import com.studywithmochi.backend.auth.dto.RegisterRequest;
import com.studywithmochi.backend.auth.dto.UserResponse;
import com.studywithmochi.backend.common.exception.ResourceNotFoundException;
import com.studywithmochi.backend.security.JwtService;
import com.studywithmochi.backend.user.User;
import com.studywithmochi.backend.user.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class AuthService {

    // Result of a successful login: JWT for the cookie + user info for the frontend
    public record AuthResult(String token, UserResponse user) {
    }

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // REGISTER
    public void register(RegisterRequest request) {

        String email = normalizeEmail(request.getEmail());

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Email already registered"
            );
        }

        String passwordHash =
                passwordEncoder.encode(request.getPassword());

        User user = new User();

        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    // LOGIN
    public AuthResult login(LoginRequest request) {

        String email = normalizeEmail(request.getEmail());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid email or password"
                        )
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        )) {
            throw new IllegalArgumentException(
                    "Invalid email or password"
            );
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail()
        );

        return new AuthResult(token, toResponse(user));
    }

    // CURRENT USER (for GET /api/auth/me)
    public UserResponse getCurrentUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    // Emails are case-insensitive: "A@x.com" and "a@x.com" are the same account
    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
