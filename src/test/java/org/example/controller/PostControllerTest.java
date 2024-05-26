package org.example.controller;

import org.example.model.Post;
import org.example.model.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@EnableTestContainer
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql(value = {"classpath:add_data.sql"})
class PostControllerTest {


    private PostController postController = new PostController();

    @Test
    @Order(1)
    void getLabelById() {
        Post byId = postController.getById(1L);
        Assertions.assertEquals(1L, byId.getId());
        Assertions.assertEquals("Title", byId.getTitle());
        Assertions.assertEquals("Content", byId.getContent());
    }

    @Test
    @Order(2)
    void getAll() {
        List<Post> all = postController.getAll();
        Assertions.assertEquals(1, all.size());
    }

    @Test
    @Order(3)
    void saveLabel() {
        Post post = new Post();
        post.setTitle("lorem");
        post.setContent("ipsum");
        Post save = postController.save(post);
        Assertions.assertEquals("lorem", save.getTitle());
        Assertions.assertEquals("ipsum", save.getContent());
        Assertions.assertNotNull(save.getId());
    }

    @Test
    @Order(4)
    void updateLabel() {
        Post byId = postController.getById(1L);
        byId.setTitle("New");
        byId.setContent("New");
        Post update = postController.update(byId);
        Assertions.assertEquals("New", update.getTitle());
        Assertions.assertEquals("New", update.getContent());
    }

    @Test
    @Order(5)
    void deleteLabel() {
        postController.delete(1L);
        Post byId = postController.getById(1L);
        Assertions.assertEquals(Status.DELETED, byId.getStatus());
    }
}