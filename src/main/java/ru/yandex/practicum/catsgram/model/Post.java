package ru.yandex.practicum.catsgram.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@EqualsAndHashCode(of = {"id"})
public class Post {
    protected Long id;
    protected Long authorId;
    protected String description;
    protected Instant postDate;
}
