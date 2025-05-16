package ru.yandex.practicum.catsgram.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.catsgram.dto.NewPostRequest;
import ru.yandex.practicum.catsgram.dto.PostDto;
import ru.yandex.practicum.catsgram.model.Post;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostMapper {

    public static PostDto mapToPostDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setAuthorId(post.getAuthorId());
        dto.setDescription(post.getDescription());
        dto.setPostDate(post.getPostDate());
        return dto;
    }

    public static Post mapToPost(NewPostRequest newPostRequest) {
        Post post = new Post();
        post.setAuthorId(newPostRequest.getAuthorId());
        post.setDescription(newPostRequest.getDescription());
        return post;
    }
}
