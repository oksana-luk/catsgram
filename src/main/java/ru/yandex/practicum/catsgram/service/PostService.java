package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final UserService userService;
    private final Map<Long, Post> posts = new HashMap<>();
    private static long globalId = 0;

    public PostService(UserService userService) {
        this.userService = userService;
    }

    public Collection<Post> findAll(int size, long fromId, String sort) {
        if (fromId <= 0 || fromId > globalId) {
            fromId = (globalId <= size) ? 1L : globalId - size;
        }
        long finalFromId = fromId;
        long finalToId = fromId + size - 1;

        SortOrder sortOrder = SortOrder.from(sort);

        Comparator<Post> comparator = Comparator.comparingLong(Post::getId);
        if (sortOrder == SortOrder.DESCENDING) {
            comparator = comparator.reversed();
        }

        return posts.values().stream()
                .filter(post -> post.getId() >= finalFromId && post.getId() <= finalToId)
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public Post create(Post post) {
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Description cannot be empty");
        }
        Optional<User> userOpt = userService.findUserPerId(post.getAuthorId());
        if (userOpt.isEmpty()) {
            throw new NotFoundException(String.format("An author with id = %d not found", post.getAuthorId()));
        }
        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id must be specified");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Description cannot be empty");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Post with id = " + newPost.getId() + " not found");
    }

    private long getNextId() {
//        long currentMaxId = posts.keySet()
//                .stream()
//                .mapToLong(id -> id)
//                .max()
//                .orElse(0);
//        return ++currentMaxId;
        return ++globalId;
    }

    public Optional<Post> findUserPerId(long id) {
       return  posts.values().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }
}