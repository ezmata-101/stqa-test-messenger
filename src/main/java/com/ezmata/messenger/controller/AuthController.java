package com.ezmata.messenger.controller;

import com.ezmata.messenger.records.request.LoginRequest;
import com.ezmata.messenger.records.request.SignupRequest;
import com.ezmata.messenger.records.response.LoginResponse;
import com.ezmata.messenger.records.response.SignupResponse;
import com.ezmata.messenger.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            SignupResponse response = authService.signup(
                    request.username(),
                    request.email(),
                    request.password()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
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

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String username) {
        if(authService.logout(username)) {
            return ResponseEntity.ok("User logged out successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Logout failed");
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> authApi(Authentication authentication) {
        if(authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        }
        String username = authentication.getName();
        return ResponseEntity.ok("Hello, " + username + "! This is a protected API.");
    }
}
