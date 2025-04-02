package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.catsgram.exception.ImageFileException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Image;
import ru.yandex.practicum.catsgram.model.ImageData;
import ru.yandex.practicum.catsgram.model.Post;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final PostService postService;
    private final Map<Long, Image> images = new HashMap<>();
    //@Value("${catsgram.image-directory}")
    private String imageDirectory = "C:\\Users\\oksan\\Downloads\\CatsgramImages";

    public List<Image> getPostImages(long postId) {
        return images.values()
                .stream()
                .filter(image -> image.getPostId() == postId)
                .collect(Collectors.toList());
    }

    public List<Image> saveImages(long postId, List<MultipartFile> files) {
        return files.stream()
                .map(file -> saveImage(postId, file))
                .collect(Collectors.toList());
    }

    public ImageData getImageData(long imageId) {
        if (!images.containsKey(imageId)) {
            throw new NotFoundException(String.format("Image with id = %d not found", imageId));
        }
        Image image = images.get(imageId);
        byte[] data = loadFile(image);
        return new ImageData(data, image.getOriginalFileName());
    }

    private Image saveImage(long postId, MultipartFile file) {
        Post post = postService.findPostPerId(postId).orElseThrow(() -> new NotFoundException(
                String.format("Post with id = %d not found", postId)));
        Path filePath = saveFile(file, post);

        Image image = new Image();
        image.setId(getNextId());
        image.setFilePath(filePath.toString());
        image.setPostId(post.getId());
        image.setOriginalFileName(file.getOriginalFilename());

        images.put(image.getId(), image);
        return image;
    }

    private Path saveFile(MultipartFile file, Post post) {
        try {
            String uniqueFileName = String.format("%d.%s", Instant.now().toEpochMilli(),
                    StringUtils.getFilenameExtension(file.getOriginalFilename()));
            Path uploadPath = Paths.get(imageDirectory, String.valueOf(post.getAuthorId()), post.getId().toString());
            Path filePath = uploadPath.resolve(uniqueFileName);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            file.transferTo(filePath);
            return filePath;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] loadFile(Image image) {
        Path path = Paths.get(image.getFilePath());
        if (Files.exists(path)) {
            try {
                return Files.readAllBytes(path);
            } catch (IOException e) {
                throw new ImageFileException(String.format("Error reading file.  Id: %d, name: %s", image.getId(),
                        image.getOriginalFileName()));
            }
        } else {
            throw new ImageFileException(String.format("Not found file.  Id: %d, name: %s", image.getId(),
                    image.getOriginalFileName()));
        }
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
