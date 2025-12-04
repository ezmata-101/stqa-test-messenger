package com.ezmata.messenger.service;

import com.ezmata.messenger.api.response.LoginResponse;
import com.ezmata.messenger.model.User;
import com.ezmata.messenger.security.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthService(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    public User signup(String username, String email, String password) {
        if(userService.getByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }
        return userService.addUser(username, email, password);
    }

    public LoginResponse login(String username, String password) {
        if(userService.getByUsername(username).isPresent()) {
            User user = userService.getByUsername(username).get();
            if(user.getPassword().equals(password)) {
                return new LoginResponse(jwtUtil.generateToken(username));
            } else {
                throw new IllegalArgumentException("Invalid password");
            }
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }
}
