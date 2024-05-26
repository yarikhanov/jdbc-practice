package org.example.repository.jdbc;


import org.example.mapper.PostMapper;
import org.example.model.Label;
import org.example.model.Post;
import org.example.repository.PostRepo;
import org.example.utils.JdbcUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcPostRepoImpl implements PostRepo {
    private final String GET_POST_BY_ID = "SELECT * FROM posts WHERE id = ?";
    private final String GET_LABELS_BY_POST = "SELECT * FROM labels WHERE post_id = ? AND status = 'ACTIVE'";
    private final String GET_ALL_POSTS = "SELECT * FROM posts WHERE status = 'ACTIVE'";
    private final String SAVE_POST = "INSERT INTO posts (title, content, status) VALUES (?, ?, ?)";
    private final String SAVE_LABELS_BY_POST = "INSERT INTO labels (name, post_id) VALUES (?, ?)";
    private final String UPDATE_POST = "UPDATE posts SET title = ?, content = ? WHERE id = ?";
    private final String UPDATE_LABELS_BY_POST = "UPDATE labels SET name = ? WHERE id = ? AND post_id = ?";
    private final String DELETE_POST = "UPDATE posts SET status = 'DELETED' WHERE id = ?";
    private final String DELETE_LABELS = "UPDATE labels SET status = 'DELETED' WHERE post_id = ?";

    private PostMapper postMapper = new PostMapper();
    @Override
    public Post getById(Long id) {

        try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatement(GET_POST_BY_ID)) {
            preparedStatement.setLong(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            List<Label> labels = getLabels(id);
            if (rs.next()) {
                postMapper.map(rs, labels);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    @Override
    public List<Post> getAll() {

        List<Post> posts = new ArrayList<>();
        try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatement(GET_ALL_POSTS)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                List<Label> labels = getLabels(rs.getLong("id"));
                posts.add(postMapper.map(rs, labels));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return posts;
    }

    @Override
    public Post save(Post post) {
        try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatementWithKeys(SAVE_POST)) {
            preparedStatement.setString(1, post.getTitle());
            preparedStatement.setString(2, post.getContent());
            preparedStatement.setString(3, post.getStatus().toString());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        post.setId(generatedKeys.getLong(1));
                        List<Label> labels = saveLabels(post.getId(), post.getLabels());
                        post.setLabels(labels);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return post;
    }

    @Override
    public Post update(Post post) {
        try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatementWithKeys(UPDATE_POST)) {
            preparedStatement.setString(1, post.getTitle());
            preparedStatement.setString(2, post.getContent());
            preparedStatement.setLong(3, post.getId());
            List<Label> labels = updateLabels(post.getId(), post.getLabels());
            post.setLabels(labels);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return post;
    }

    @Override
    public void deleteById(Long id) {

        try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatement(DELETE_POST)) {
            preparedStatement.setLong(1, id);
            deleteLabels(id);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private List<Label> getLabels(Long postId) {
        List<Label> labels = new ArrayList<>();
        try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatement(GET_LABELS_BY_POST)) {
            preparedStatement.setLong(1, postId);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Label label = new Label();
                label.setId(rs.getLong("id"));
                label.setName(rs.getString("name"));
                labels.add(label);
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return labels;
    }

    private List<Label> saveLabels(Long postId, List<Label> labels) {
        labels.forEach(label -> {
            if (label.getId() == null) {
                try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatementWithKeys(SAVE_LABELS_BY_POST)) {
                    preparedStatement.setString(1, label.getName());
                    preparedStatement.setLong(2, postId);
                    int affectedRows = preparedStatement.executeUpdate();

                    if (affectedRows > 0) {
                        try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                            if (rs.next()) {
                                label.setId(rs.getLong(1));
                            }
                        }
                    }
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        });

        return labels;
    }

    private List<Label> updateLabels(Long postId, List<Label> labels) {
        labels.forEach(label -> {
            if (label.getId() == null) {
                try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatementWithKeys(SAVE_LABELS_BY_POST)) {
                    preparedStatement.setString(1, label.getName());
                    preparedStatement.setLong(2, postId);

                    int affectedRows = preparedStatement.executeUpdate();

                    if (affectedRows > 0) {
                        try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                            if (rs.next()) {
                                label.setId(rs.getLong(1));
                            }
                        }
                    }
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            } else {
                try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatementWithKeys(UPDATE_LABELS_BY_POST)) {
                    preparedStatement.setString(1, label.getName());
                    preparedStatement.setLong(2, label.getId());
                    preparedStatement.setLong(3, postId);

                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        });

        return labels;
    }

    private void deleteLabels(Long postId) {
        try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatement(DELETE_LABELS)) {
            preparedStatement.setLong(1, postId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }
}
