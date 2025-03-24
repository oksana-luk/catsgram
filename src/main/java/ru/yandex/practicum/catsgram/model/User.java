package ru.yandex.practicum.catsgram.model;

import lombok.EqualsAndHashCode;

import java.time.Instant;

@EqualsAndHashCode(of = {"email"})
public class User {
    protected Long id;
    protected String userName;
    protected String email;
    protected String password;
    protected Instant registrationDate;
}
