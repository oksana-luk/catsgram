package ru.yandex.practicum.catsgram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.dto.NewUserRequest;
import ru.yandex.practicum.catsgram.dto.UpdateUserRequest;
import ru.yandex.practicum.catsgram.dto.UserDto;
import ru.yandex.practicum.catsgram.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDto> findAll() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(@PathVariable long id) {
        return userService.findUserPerId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody NewUserRequest request) {
         return userService.createUser(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@PathVariable long id, @RequestBody UpdateUserRequest request) {
        return userService.updateUser(id, request);
    }
}
