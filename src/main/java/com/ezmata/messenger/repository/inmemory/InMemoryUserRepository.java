package com.ezmata.messenger.repository.inmemory;

import com.ezmata.messenger.model.User;
import com.ezmata.messenger.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> user = new ConcurrentHashMap<>();
    private final Map<String, User> usersByUsername = new ConcurrentHashMap<>();

    private final AtomicLong userId = new AtomicLong(101);
    @Override
    public User add(String username, String email, String password) {
        long id = userId.getAndIncrement();
        User newUser = new User(id, username, email, password);
        user.put(id, newUser);
        usersByUsername.put(username, newUser);
        return newUser;
    }

    @Override
    public Optional<User> get(long id) {
        return Optional.ofNullable(user.get(id));
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(username));
    }

    @Override
    public List<User> getAll() {
        return List.copyOf(user.values());
    }

    @Override
    public boolean existsByUsername(String username) {
        return usersByUsername.containsKey(username);
    }
}
