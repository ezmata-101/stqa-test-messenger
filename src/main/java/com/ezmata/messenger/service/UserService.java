package com.ezmata.messenger.service;

import com.ezmata.messenger.records.request.UserUpdateRequest;
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

    public User updateUser(long id, String usernameFromToken, UserUpdateRequest request) {
        Optional<User> existingUserOpt = userRepository.get(id);
        if(request.id() != null) {
            throw new IllegalArgumentException("User ID cannot be updated");
        }
        if(existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            if(!existingUser.getUsername().equals(usernameFromToken)) {
                throw new SecurityException("You can only update your own user details");
            }
            User updatedUser = new User(
                    existingUser.getUserId(),
                    request.username() != null ? request.username() : existingUser.getUsername(),
                    request.email() != null ? request.email() : existingUser.getEmail(),
                    request.password() != null ? request.password() : existingUser.getPassword()
            );
            return userRepository.update(id, updatedUser);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    public boolean isUserBlockedBy(long blocked, long blockedBy) {
        return userRepository.isBlockedBy(blocked, blockedBy);
    }

    public boolean blockUser(String username, long blockedUserId) {
        Optional<User> userOpt = userRepository.getByUsername(username);
        if(userOpt.isPresent()) {
            User user = userOpt.get();
            Optional<User> blockedUserOpt = userRepository.get(blockedUserId);
            if(blockedUserOpt.isPresent()) {
                return userRepository.blockUser(user.getUserId(), blockedUserId);
            } else {
                throw new IllegalArgumentException("User to be blocked not found");
            }
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    public boolean unblockUser(String username, long blockedUserId) {
        Optional<User> userOpt = userRepository.getByUsername(username);
        if(userOpt.isPresent()) {
            User user = userOpt.get();
            Optional<User> blockedUserOpt = userRepository.get(blockedUserId);
            if(blockedUserOpt.isPresent()) {
                return userRepository.unblockUser(user.getUserId(), blockedUserId);
            } else {
                throw new IllegalArgumentException("User to be unblocked not found");
            }
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }
}
