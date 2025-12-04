package com.ezmata.messenger.service;

import com.ezmata.messenger.model.User;
import com.ezmata.messenger.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public User addUser(String username, String email, String password) {
        return userRepository.add(username, email, password);
    }

    public Optional<User> getByUsername(String username) {
        return userRepository.getByUsername(username);
    }

    public Optional<User> getById(long id) {
        return userRepository.get(id);
    }

    public List<User> getAllUsers() {
        return userRepository.getAll();
    }
}
