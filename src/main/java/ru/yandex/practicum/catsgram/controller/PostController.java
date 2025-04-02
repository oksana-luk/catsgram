package ru.yandex.practicum.catsgram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Collection<Post> findAll(@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "0") long from,
                                    @RequestParam(defaultValue = "asc") String sort) {
        return postService.findAll(size, from, sort);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Post findPostById(@PathVariable long id) {
        Optional<Post> postOpt = postService.findPostPerId(id);
        if (postOpt.isEmpty()) {
            throw new NotFoundException(String.format("Post with id %s was not found", id));
        }
        return postOpt.get();
     }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }

 }