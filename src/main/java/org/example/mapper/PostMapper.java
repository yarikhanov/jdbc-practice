package org.example.mapper;

import org.example.model.Label;
import org.example.model.Post;
import org.example.model.Status;

import java.sql.ResultSet;
import java.util.List;

public class PostMapper {
    public Post map(ResultSet rs, List<Label> labels){
        Post post = new Post();
        post.setLabels(labels);
        post.setTitle(rs.getString("title"));
        post.setStatus(Status.valueOf(rs.getString("status")));
        post.setContent(rs.getString("content"));
        post.setId(rs.getLong("id"));

        return post;
    }
}
