package com.ezmata.messenger.controller;

import com.ezmata.messenger.api.request.LoginRequest;
import com.ezmata.messenger.api.response.LoginResponse;
import com.ezmata.messenger.api.request.SignupRequest;
import com.ezmata.messenger.security.JwtUtil;
import com.ezmata.messenger.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            authService.signup(
                    request.username(),
                    request.email(),
                    request.password()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(
                    request.username(),
                    request.password()
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/unauthapi")
    public String unauthApi() {
        return "Welcome to the server";
    }

    @GetMapping("/authapi")
    public ResponseEntity<?> authApi(Authentication authentication) {
        if(authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        }
        String username = authentication.getName();
        return ResponseEntity.ok("Hello, " + username + "! This is a protected API.");
    }
}
