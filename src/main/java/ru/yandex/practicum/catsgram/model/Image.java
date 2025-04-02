package ru.yandex.practicum.catsgram.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id"})
public class Image {
    protected Long id;
    protected Long postId;
    protected String originalFileName;
    protected String filePath;
}
