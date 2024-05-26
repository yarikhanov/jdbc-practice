package org.example.controller;


import org.example.model.Post;
import org.example.repository.jdbc.JdbcPostRepoImpl;
import org.example.repository.PostRepo;
import org.example.service.PostService;

import java.util.List;

public class PostController {
    private final PostService postService = new PostService();

    public Post getById(Long id) {
        return postService.getById(id);
    }

    public List<Post> getAll() {
        return postService.getAll();
    }

    public Post save(Post post) {
        return postService.save(post);
    }

    public Post update(Post post) {
        return postService.save(post);
    }

    public void delete(Long id) {
        postService.delete(id);
    }
}
