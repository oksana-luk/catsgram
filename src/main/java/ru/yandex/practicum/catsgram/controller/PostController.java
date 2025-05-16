package ru.yandex.practicum.catsgram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.dto.NewPostRequest;
import ru.yandex.practicum.catsgram.dto.PostDto;
import ru.yandex.practicum.catsgram.dto.UpdatePostRequest;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Collection<PostDto> findAll(@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "0") long from,
                                    @RequestParam(defaultValue = "asc") String sort) {
        if (!sort.equals("asc") & !sort.equals("desc")) {
            throw new ParameterNotValidException("sort", "Input sort : " + sort + ". The sort parameter can be set to asc (ascending) or desc (descending).");
        }
        if (size <= 0) {
            throw new ParameterNotValidException("size", "Input size: " + size + ". The size parameter can be only positive.");
        }
        if (from < 0) {
            throw new ParameterNotValidException("from", "Input from: " + from + ". The from parameter can be 0 and more.");
        }
        return postService.findAll(size, from, sort);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public PostDto findPostById(@PathVariable long id) {
        return postService.findPostById(id);
     }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostDto create(@RequestBody NewPostRequest newPostRequest) {
        return postService.create(newPostRequest);
    }

    @PutMapping("/{id}")
    @ResponseBody
    public PostDto update(@PathVariable("id") long postId, @RequestBody UpdatePostRequest updatePostRequest) {
        return postService.update(postId, updatePostRequest);
    }
 }