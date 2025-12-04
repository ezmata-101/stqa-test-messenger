package com.ezmata.messenger.controller;

import com.ezmata.messenger.dto.LoginRequest;
import com.ezmata.messenger.dto.LoginResponse;
import com.ezmata.messenger.dto.SignupRequest;
import com.ezmata.messenger.security.JwtUtil;
import com.ezmata.messenger.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        if (userService.userExists(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Username already exists");
        }
        userService.registerUser(request.getUsername(), request.getPassword());
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        boolean ok = userService.validateCredentials(
                request.getUsername(),
                request.getPassword()
        );

        if (!ok) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }

        String token = jwtUtil.generateToken(request.getUsername());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @GetMapping("/unauthapi")
    public String unauthApi() {
        return "Welcome to the server";
    }

    @GetMapping("/authapi")
    public ResponseEntity<?> authApi(Authentication authentication) {
        // Authentication.getName() will be the username set by JwtAuthFilter
        if(authentication == null) {
//        send 403 forbidden
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        }
        String username = authentication.getName();
        return ResponseEntity.ok("Hello, " + username + "! This is a protected API.");
    }
}
