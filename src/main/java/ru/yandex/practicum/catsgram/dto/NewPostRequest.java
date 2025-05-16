package ru.yandex.practicum.catsgram.dto;

import lombok.Data;

@Data
public class NewPostRequest {
    protected Long authorId;
    protected String description;
}
