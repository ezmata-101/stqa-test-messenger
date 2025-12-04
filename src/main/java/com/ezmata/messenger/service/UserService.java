package com.ezmata.messenger.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {
    private final Map<String, String> users = new ConcurrentHashMap<>();
    private final PasswordEncoder passwordEncoder;

    public UserService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    public void registerUser(String username, String rawPassword) {
        String encodedPassword = passwordEncoder.encode(rawPassword);
        users.put(username, encodedPassword);
    }

    public boolean validateCredentials(String username, String rawPassword) {
        String storedPassword = users.get(username);
        return storedPassword != null && passwordEncoder.matches(rawPassword, storedPassword);
    }
}
