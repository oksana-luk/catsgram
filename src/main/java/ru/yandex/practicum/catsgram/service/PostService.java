package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.dal.PostRepository;
import ru.yandex.practicum.catsgram.dal.UserRepository;
import ru.yandex.practicum.catsgram.dto.NewPostRequest;
import ru.yandex.practicum.catsgram.dto.PostDto;
import ru.yandex.practicum.catsgram.dto.UpdatePostRequest;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.mapper.PostMapper;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final UserService userService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Collection<PostDto> findAll(int size, long fromId, String sort) {
        return postRepository.findAll(size, fromId, sort)
                .stream()
                .map(PostMapper::mapToPostDto)
                .toList();
    }

    public PostDto create(NewPostRequest newPostRequest) {
        Optional<User> userOpt = userRepository.findById(newPostRequest.getAuthorId());
        if (userOpt.isEmpty()) {
            throw new NotFoundException(String.format("An author with id = %d not found", newPostRequest.getAuthorId()));
        }
        validateDescription(newPostRequest.getDescription());
        Post post = PostMapper.mapToPost(newPostRequest);
        post.setPostDate(Instant.now());
        post = postRepository.save(post);
        return PostMapper.mapToPostDto(post);
    }

    public PostDto update(long id, UpdatePostRequest updatePostRequest) {
        Post post = getPostById(id);
        validateDescription(updatePostRequest.getDescription());
        post.setDescription(updatePostRequest.getDescription());
        post = postRepository.update(post);
        return PostMapper.mapToPostDto(post);
    }

    public PostDto findPostById(long id) {
        return PostMapper.mapToPostDto(getPostById(id));
    }

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new ConditionsNotMetException("Description cannot be empty");
        }
    }

    private Post getPostById(long id) {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty()) {
            throw new NotFoundException("Post with " + id + " not found");
        } else {
            return postOpt.get();
        }
    }
}