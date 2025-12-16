package com.ezmata.messenger.repository;

import com.ezmata.messenger.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User add(String username, String email, String password);
    Optional<User> get(long id);
    Optional<User> getByUsername(String username);
    List<User> getAll();
    boolean existsByUsername(String username);
    User update(long id, User updatedUser);
    boolean blockUser(long userId, long blockId);
    boolean unblockUser(long userId, long blockId);
    boolean isBlockedBy(long userId, long blockedBy);
}
