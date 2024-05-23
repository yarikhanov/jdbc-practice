package org.example.repositoryImpl;


import org.example.model.Label;
import org.example.model.Post;
import org.example.model.Status;
import org.example.repositoryInterface.PostRepo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostRepoImpl implements PostRepo {

    private final String url = "jdbc:mysql://localhost:3306/yarikhanov_khasan";
    private final String user = "username";

    private final String password = "password";
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public Post getById(Long id) {
        String query = "SELECT * FROM posts WHERE id = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            List<Label> labels = getLabels(id, connection);
            if (rs.next()) {
                Post post = new Post();
                post.setLabels(labels);
                post.setTitle(rs.getString("title"));
                post.setStatus(Status.valueOf(rs.getString("status")));
                post.setContent(rs.getString("content"));
                post.setId(rs.getLong("id"));
                return post;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    @Override
    public List<Post> getAll() {
        String query = "SELECT * FROM posts WHERE status = 'ACTIVE'";
        List<Post> posts = new ArrayList<>();
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Post post = new Post();
                post.setId(rs.getLong("id"));
                post.setStatus(Status.valueOf(rs.getString("status")));
                post.setContent(rs.getString("content"));
                post.setTitle(rs.getString("title"));
                List<Label> labels = getLabels(post.getId(), connection);
                post.setLabels(labels);
                posts.add(post);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return posts;
    }

    @Override
    public Post save(Post post) {
        String query = "INSERT INTO posts (title, content, status) VALUES (?, ?, ?)";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, post.getTitle());
            preparedStatement.setString(2, post.getContent());
            preparedStatement.setString(3, post.getStatus().toString());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        post.setId(generatedKeys.getLong(1));
                        List<Label> labels = saveLabels(post.getId(), post.getLabels(), connection);
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
        String query = "UPDATE posts SET title = ?, content = ? WHERE id = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, post.getTitle());
            preparedStatement.setString(2, post.getContent());
            preparedStatement.setLong(3, post.getId());
            List<Label> labels = updateLabels(post.getId(), post.getLabels(), connection);
            post.setLabels(labels);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return post;
    }

    @Override
    public void deleteById(Long id) {
        String query = "UPDATE posts SET status = 'DELETED' WHERE id = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, id);
            deleteLabels(id, connection);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private List<Label> getLabels(Long postId, Connection connection) {
        List<Label> labels = new ArrayList<>();
        String query = "SELECT * FROM labels WHERE post_id = ? AND status = 'ACTIVE'";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
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

    private List<Label> saveLabels(Long postId, List<Label> labels, Connection connection) {
        String query = "INSERT INTO labels (name, post_id) VALUES (?, ?)";
        labels.forEach(label -> {
            if (label.getId() == null) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
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

    private List<Label> updateLabels(Long postId, List<Label> labels, Connection connection) {
        String queryInsert = "INSERT INTO labels (name, post_id) VALUES (?, ?)";
        String queryUpdate = "UPDATE labels SET name = ? WHERE id = ? AND post_id = ?";
        labels.forEach(label -> {
            if (label.getId() == null) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(queryInsert)) {
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
                try (PreparedStatement preparedStatement = connection.prepareStatement(queryUpdate)) {
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

    private void deleteLabels(Long postId, Connection connection) {
        String query = "UPDATE labels SET status = 'DELETED' WHERE post_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, postId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }
}
