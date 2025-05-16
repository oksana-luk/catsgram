package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.catsgram.dal.ImageRepository;
import ru.yandex.practicum.catsgram.dal.PostRepository;
import ru.yandex.practicum.catsgram.dto.ImageDto;
import ru.yandex.practicum.catsgram.dto.ImageUploadResponse;
import ru.yandex.practicum.catsgram.exception.ImageFileException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.mapper.ImageMapper;
import ru.yandex.practicum.catsgram.model.Image;
import ru.yandex.practicum.catsgram.model.ImageData;
import ru.yandex.practicum.catsgram.model.Post;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    //@Value("${catsgram.image-directory}")
    private String imageDirectory = "C:\\Users\\oksan\\Downloads\\CatsgramImages";


    public List<ImageDto> getPostImages(long postId) {
        return imageRepository.findByPostId(postId)
                .stream()
                .map(image -> {
                    byte[] bytes = loadFile(image);
                    return ImageMapper.mapToImageDto(image, bytes);
                })
                .toList();
    }

    public List<ImageUploadResponse> saveImages(long postId, List<MultipartFile> files) {
        return files.stream()
                .map(file -> saveImage(postId, file))
                .collect(Collectors.toList());
    }

    public ImageData getImageData(long imageId) {
        Image image = imageRepository.findById(imageId).orElseThrow(() -> new NotFoundException("Image witn id " + imageId + "not found"));
        byte[] data = loadFile(image);
        return new ImageData(data, image.getOriginalFileName());
    }

    private ImageUploadResponse saveImage(long postId, MultipartFile file) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post with " + postId + " not found"));

        Path filePath = saveFile(file, post);
        Image image = ImageMapper.mapToImage(postId, filePath, file.getOriginalFilename());
        image = imageRepository.saveImage(image);

        return ImageMapper.mapToImageUploadResponse(image);
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
}
