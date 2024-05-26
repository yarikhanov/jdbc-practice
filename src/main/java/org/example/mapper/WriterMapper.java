package org.example.mapper;

import org.example.model.Post;
import org.example.model.Writer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class WriterMapper {
    public Writer map(ResultSet rs, List<Post> posts) throws SQLException {
        Writer writer = new Writer();
        writer.setId(rs.getLong("id"));
        writer.setFirstName(rs.getString("first_name"));
        writer.setLastName(rs.getString("last_name"));
        writer.setPosts(posts);

        return writer;
    }
}
