package ru.yandex.practicum.catsgram.storage;

import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.SortOrder;

import java.util.*;
import java.util.stream.Collectors;

public class PostStorage {
    private final Map<Long, Post> posts = new HashMap<>();
    private static long globalId = 0;


    public Collection<Post> findAll(int size, long fromId, String sort) {
        if (fromId > globalId) {
            return new ArrayList<>();
        }
        if (fromId == 0) {
            fromId = 1L;
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

    public Post createPost(Post post) {
        post.setId(getNextId());
        posts.put(post.getId(), post);
        return post;
    }

    public Post updatePost(Post post) {
        posts.put(post.getId(), post);
        return post;
    }

    public Optional<Post> findById(long id) {
        return Optional.ofNullable(posts.get(id));
    }

    private long getNextId() {
        return ++globalId;
    }
}
