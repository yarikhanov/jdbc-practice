package org.example.repositoryImpl;


import org.example.model.Label;
import org.example.model.Post;
import org.example.model.Writer;
import org.example.repositoryInterface.WriterRepo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WriterRepoImpl implements WriterRepo {

    private final String url = "jdbc:mysql://localhost:3306/yarikhanov_khasan";
    private final String user = "username";

    private final String password = "password";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public Writer getById(Long id) {
        String query = "SELECT * FROM writers WHERE id = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                Writer writer = new Writer();
                writer.setId(rs.getLong("id"));
                writer.setFirstName(rs.getString("first_name"));
                writer.setLastName(rs.getString("last_name"));
                List<Post> posts = getPosts(id, connection);
                writer.setPosts(posts);
                return writer;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    @Override
    public List<Writer> getAll() {
        String query = "SELECT * FROM writers WHERE status = 'ACTIVE'";
        List<Writer> writers = new ArrayList<>();
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Writer writer = new Writer();
                writer.setId(resultSet.getLong("id"));
                writer.setFirstName(resultSet.getString("first_name"));
                writer.setFirstName(resultSet.getString("last_name"));
                List<Post> allPosts = getAllPosts(writer.getId(), connection);
                writer.setPosts(allPosts);
                writers.add(writer);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return writers;
    }

    @Override
    public Writer save(Writer writer) {
        String query = "INSERT INTO writers (first_name, last_name) VALUES (?, ?)";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, writer.getFirstName());
            preparedStatement.setString(2, writer.getLastName());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        writer.setId(resultSet.getLong(1));
                        List<Post> postList = savePost(writer.getPosts(), writer.getId(), connection);
                        writer.setPosts(postList);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return writer;
    }

    @Override
    public Writer update(Writer writer) {
        String query = "UPDATE writers SET first_name = ?, last_name = ? WHERE id = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, writer.getFirstName());
            preparedStatement.setString(2, writer.getLastName());
            preparedStatement.setLong(3, writer.getId());
            List<Post> postList = updatePosts(writer.getPosts(), writer.getId(), connection);
            writer.setPosts(postList);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return writer;
    }

    @Override
    public void deleteById(Long id) {
        String query = "UPDATE writers SET status = 'DELETED' WHERE id = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();

            deletePosts(id, connection);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private List<Post> getPosts(Long writerId, Connection connection) {
        String query = "SELECT * FROM posts WHERE writer_id = ?";
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, writerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Post post = new Post();
                post.setId(resultSet.getLong("id"));
                post.setTitle(resultSet.getString("title"));
                post.setContent(resultSet.getString("content"));
                List<Label> labels = getLabels(post.getId(), connection);
                post.setLabels(labels);
                posts.add(post);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return posts;
    }

    private List<Label> getLabels(Long postId, Connection connection) {
        String query = "SELECT * FROM labels WHERE post_id = ?";
        List<Label> labels = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, postId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Label label = new Label();
                label.setId(resultSet.getLong("id"));
                label.setName(resultSet.getString("name"));
                labels.add(label);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return labels;
    }

    private List<Post> getAllPosts(Long writerId, Connection connection) {
        String query = "SELECT * FROM posts WHERE writer_id = ? AND status = 'ACTIVE'";
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, writerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Post post = new Post();
                post.setId(resultSet.getLong("id"));
                post.setTitle(resultSet.getString("title"));
                post.setContent("content");
                List<Label> allLabels = getAllLabels(post.getId(), connection);
                post.setLabels(allLabels);
                posts.add(post);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return posts;
    }

    private List<Label> getAllLabels(Long postId, Connection connection) {
        String query = "SELECT * FROM posts WHERE post_id = ? AND status = 'ACTIVE'";
        List<Label> labels = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, postId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Label label = new Label();
                label.setId(resultSet.getLong("id"));
                label.setName(resultSet.getString("name"));
                labels.add(label);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return labels;
    }

    private List<Post> savePost(List<Post> posts, Long writerId, Connection connection) {
        String query = "INSERT INTO posts (title, content, writer_id) VALUES (?, ?, ?)";
        posts.forEach(post -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, post.getTitle());
                preparedStatement.setString(2, post.getContent());
                preparedStatement.setLong(3, writerId);
                int affectedRow = preparedStatement.executeUpdate();

                if (affectedRow > 0) {
                    ResultSet resultSet = preparedStatement.getGeneratedKeys();
                    if (resultSet.next()) {
                        post.setId(resultSet.getLong(1));
                        List<Label> labelList = saveLabels(post.getLabels(), post.getId(), connection);
                        post.setLabels(labelList);
                        posts.add(post);
                    }
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        });

        return posts;
    }

    private List<Label> saveLabels(List<Label> labels, Long postId, Connection connection) {
        String query = "INSERT INTO labels (name, post_id) VALUES (?, ?)";
        labels.forEach(label -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, label.getName());
                preparedStatement.setLong(2, postId);
                int affectedRow = preparedStatement.executeUpdate();

                if (affectedRow > 0) {
                    ResultSet resultSet = preparedStatement.getGeneratedKeys();
                    if (resultSet.next()) {
                        label.setId(resultSet.getLong(1));
                        labels.add(label);
                    }
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        });

        return labels;
    }

    private List<Post> updatePosts(List<Post> posts, Long wirterId, Connection connection) {
        String queryInsert = "INSERT INTO posts (title, content, writer_id) VALUES (?, ?, ?)";
        String queryUpdate = "UPDATE posts SET title = ?, content = ? WHERE id = ?";
        posts.forEach(post -> {
            if (post.getId() == null) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(queryInsert)) {
                    preparedStatement.setString(1, post.getTitle());
                    preparedStatement.setString(2, post.getContent());
                    preparedStatement.setLong(3, wirterId);

                    int affectedRows = preparedStatement.executeUpdate();

                    if (affectedRows > 0) {
                        try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                            if (rs.next()) {
                                post.setId(rs.getLong(1));
                                List<Label> labelList = updateLabels(post.getLabels(), post.getId(), connection);
                                post.setLabels(labelList);
                            }
                        }
                    }
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            } else {
                try (PreparedStatement preparedStatement = connection.prepareStatement(queryUpdate)) {
                    preparedStatement.setString(1, post.getTitle());
                    preparedStatement.setString(2, post.getContent());
                    preparedStatement.setLong(3, post.getId());
                    List<Label> labelList = updateLabels(post.getLabels(), post.getId(), connection);
                    post.setLabels(labelList);

                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        });

        return posts;
    }

    private List<Label> updateLabels(List<Label> labels, Long postId, Connection connection) {
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

    private void deletePosts(Long writerId, Connection connection) {
        String query = "UPDATE posts SET status = 'DELETED' WHERE writer_id = ?";
        String queryPostsIdForDelete = "SELECT id FROM posts WHERE writer_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, writerId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryPostsIdForDelete)) {
            preparedStatement.setLong(1, writerId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Long> postsId = new ArrayList<>();
            while (resultSet.next()) {
                postsId.add(resultSet.getLong("id"));
            }
            if (!postsId.isEmpty()) {
                deleteLabels(postsId, connection);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private void deleteLabels(List<Long> postsId, Connection connection) {
        String query = "UPDATE labels SET status = 'DELETED' WHERE post_id = ?";
        postsId.forEach(id -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setLong(1, id);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        });
    }
}
