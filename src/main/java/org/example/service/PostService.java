package org.example.service;

import org.example.model.Post;
import org.example.repository.PostRepo;
import org.example.repository.jdbc.JdbcPostRepoImpl;

import java.util.List;

public class PostService {
    private final PostRepo postRepo = new JdbcPostRepoImpl();

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
