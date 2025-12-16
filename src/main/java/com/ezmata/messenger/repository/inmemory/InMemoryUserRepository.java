package com.ezmata.messenger.repository.inmemory;

import com.ezmata.messenger.model.User;
import com.ezmata.messenger.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> usersById = new ConcurrentHashMap<>();
    private final Map<String, User> usersByUsername = new ConcurrentHashMap<>();
    private final Map<Long, List<Long>> blockedUsers = new ConcurrentHashMap<>();

    private final AtomicLong userId = new AtomicLong(101);
    @Override
    public User add(String username, String email, String password) {
        long id = userId.getAndIncrement();
        User newUser = new User(id, username, email, password);
        usersById.put(id, newUser);
        usersByUsername.put(username, newUser);
        return newUser;
    }

    @Override
    public Optional<User> get(long id) {
        return Optional.ofNullable(usersById.get(id));
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(username));
    }

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        for (User u : usersById.values()) {
            users.add(new User(u.getUserId(), u.getUsername(), null, null));
        }
        return users;
    }

    @Override
    public User update(long id, User user) {
        String oldUsername = usersById.get(id).getUsername();
        usersById.put(user.getUserId(), user);
        usersByUsername.remove(oldUsername);
        usersByUsername.put(user.getUsername(), user);
        return user;
    }

    @Override
    public boolean blockUser(long userId, long blockId) {
        blockedUsers.putIfAbsent(userId, new ArrayList<>());
        List<Long> blockedList = blockedUsers.get(userId);
        if (!blockedList.contains(blockId)) {
            blockedList.add(blockId);
            return true;
        }
        return false;
    }

    @Override
    public boolean unblockUser(long userId, long blockId) {
        List<Long> blockedList = blockedUsers.get(userId);
        if (blockedList != null && blockedList.contains(blockId)) {
            blockedList.remove(blockId);
            return true;
        }
        return false;
    }

    @Override
    public boolean isBlockedBy(long userId, long blockedBy) {
        List<Long> blockedList = blockedUsers.get(blockedBy);
        return blockedList != null && blockedList.contains(userId);
    }

    @Override
    public boolean existsByUsername(String username) {
        return usersByUsername.containsKey(username);
    }

    @Override
    public void reset() {
        usersById.clear();
        usersByUsername.clear();
        blockedUsers.clear();
        userId.set(101);
    }
}
