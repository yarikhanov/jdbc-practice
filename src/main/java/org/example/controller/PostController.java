package org.example.controller;


import org.example.model.Post;
import org.example.repositoryImpl.PostRepoImpl;
import org.example.repositoryInterface.PostRepo;

import java.util.List;

public class PostController {
    private final PostRepo postRepo = new PostRepoImpl();

    public Post getById(Long id) {
        return postRepo.getById(id);
    }

    public List<Post> getAll() {
        return postRepo.getAll();
    }

    public Post save(Post post) {
        return postRepo.save(post);
    }

    public Post update(Post post) {
        return postRepo.save(post);
    }

    public void delete(Long id) {
        postRepo.deleteById(id);
    }
}
