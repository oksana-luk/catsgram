package ru.yandex.practicum.catsgram.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Image;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageStorage {

    private final Map<Long, Image> images = new HashMap<>();

    public List<Image> findByPostId(long postId) {
        return images.values()
                .stream()
                .filter(image -> image.getPostId() == postId)
                .collect(Collectors.toList());
    }

    public Image findById(long imageId) {
        if (!images.containsKey(imageId)) {
            throw new NotFoundException(String.format("Image with id = %d not found", imageId));
        } else {
            return images.get(imageId);
        }
    }

    private Image saveImage(Image image) {
        image.setId(getNextId());
        images.put(image.getId(), image);
        return image;
    }

    private long getNextId() {
        long currentMaxId = images.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}


