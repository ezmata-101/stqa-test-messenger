package com.ezmata.messenger.controller;

import com.ezmata.messenger.records.request.UserUpdateRequest;
import com.ezmata.messenger.records.response.GenericResponse;
import com.ezmata.messenger.model.User;
import com.ezmata.messenger.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getUsers(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You must be logged in to view users");
        }
        if(!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable long id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You must be logged in to view user details");
        }
        if(!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        if (userService.getById(id).isPresent()) {
            return ResponseEntity.ok(userService.getById(id).get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You must be logged in to view user details");
            }
        if(!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        if (userService.getByUsername(username).isPresent()) {
            return ResponseEntity.ok(userService.getByUsername(username).get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable long id,
            @RequestBody UserUpdateRequest request,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No authentication token provided");
        }
        if(!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        try {
            String usernameFromToken = authentication.getName();
            System.out.println("Updating user with id: " + id + " by user: " + usernameFromToken);
            System.out.println("New details - username: " + request.username() + ", email: " + request.email() + ", password: " + request.password());
            User updatedUser = userService.updateUser(id, usernameFromToken, request);
            return ResponseEntity.ok(new GenericResponse("User updated successfully", updatedUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PatchMapping("/block/{id}")
    public ResponseEntity<?> blockUser(
            @PathVariable long id,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No authentication token provided");
        }
        if(!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        try {
            String usernameFromToken = authentication.getName();
            if(userService.blockUser(usernameFromToken, id)) {
                return ResponseEntity.ok(new GenericResponse("User blocked successfully", null));
            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to block user");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/block/{id}")
    public ResponseEntity<?> unblockUser(
            @PathVariable long id,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No authentication token provided");
        } else if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        try {
            String usernameFromToken = authentication.getName();
            if(userService.unblockUser(usernameFromToken, id)) {
                return ResponseEntity.ok(new GenericResponse<>("User unblocked successfully", null));
            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to unblock user");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
