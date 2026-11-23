package com.studywithmochi.backend.auth;

import com.studywithmochi.backend.auth.dto.LoginRequest;
import com.studywithmochi.backend.auth.dto.MessageResponse;
import com.studywithmochi.backend.auth.dto.RegisterRequest;
import com.studywithmochi.backend.auth.dto.UserResponse;
import com.studywithmochi.backend.security.JwtService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String COOKIE_NAME = "access_token";

    private final AuthService authService;
    private final JwtService jwtService;
    private final boolean cookieSecure;
    private final String cookieSameSite;

    public AuthController(
            AuthService authService,
            JwtService jwtService,
            @Value("${app.cookie.secure}") boolean cookieSecure,
            @Value("${app.cookie.same-site}") String cookieSameSite
    ) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.cookieSecure = cookieSecure;
        this.cookieSameSite = cookieSameSite;
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new MessageResponse("User registered successfully"));
    }

    // LOGIN: sets the HttpOnly cookie and returns the user
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        AuthService.AuthResult result = authService.login(request);

        // Cookie lifetime always matches the JWT lifetime
        ResponseCookie cookie = buildCookie(
                result.token(),
                jwtService.getExpirationSeconds()
        );

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(result.user());
    }

    // CURRENT USER
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            Authentication authentication
    ) {

        Long userId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(authService.getCurrentUser(userId));
    }

    // LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {

        ResponseCookie cookie = buildCookie("", 0);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    private ResponseCookie buildCookie(String value, long maxAgeSeconds) {

        return ResponseCookie
                .from(COOKIE_NAME, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/")
                .maxAge(maxAgeSeconds)
                .build();
    }
}
