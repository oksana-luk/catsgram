package ru.yandex.practicum.catsgram.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@EqualsAndHashCode(of = {"id"})
public class User {
    protected Long id;
    protected String userName;
    protected String email;
    protected String password;
    protected Instant registrationDate;
}
