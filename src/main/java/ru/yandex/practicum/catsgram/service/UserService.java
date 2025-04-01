package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, User> usersEmail = new HashMap<>();

    public Collection<User> finsAll() {
        return users.values();
    }

    public User createUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Email must be provided");
        }
        if (usersEmail.containsKey(user.getEmail())) {
            throw new DuplicatedDataException("This email is already in use");
        }
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);
        usersEmail.put(user.getEmail(), user);
        return user;
    }

    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new ConditionsNotMetException("Id must be specified");
        }
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("No user found with this Id");
        }
        User findedUser = users.get(user.getId());
        if (!user.getEmail().isBlank() && !findedUser.getEmail().equals(user.getEmail())
                && usersEmail.containsKey(user.getEmail())) {
            throw new DuplicatedDataException("This email is already in use");
        }
        if (user.getUserName() == null || !user.getUserName().isBlank()) {
            findedUser.setUserName(user.getUserName());
        }
        if (user.getPassword() == null || !user.getPassword().isBlank()) {
            findedUser.setPassword(user.getPassword());
        }
        if (user.getEmail() == null || !findedUser.getEmail().equals(user.getEmail())) {
            usersEmail.remove(findedUser.getEmail());
            findedUser.setEmail(user.getEmail());
            usersEmail.put(findedUser.getEmail(), findedUser);
        }
        return findedUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public Optional<User> findUserPerId(long id) {
        return users.values().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }
}

