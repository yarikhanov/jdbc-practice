package org.example.repository.jdbc;


import org.example.mapper.LabelMapper;
import org.example.mapper.PostMapper;
import org.example.mapper.WriterMapper;
import org.example.model.Label;
import org.example.model.Post;
import org.example.model.Writer;
import org.example.repository.WriterRepo;
import org.example.utils.JdbcUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcWriterRepoImpl implements WriterRepo {
    private final String GET_WRITERS = "SELECT * FROM writers WHERE id = ?";
    private final String GET_POSTS_BY_WRITER = "SELECT * FROM posts WHERE writer_id = ?";
    private final String GET_LABELS_BY_POST = "SELECT * FROM labels WHERE post_id = ?";
    private final String GET_ALL_WRITERS = "SELECT * FROM writers WHERE status = 'ACTIVE'";
    private final String GET_ALL_POSTS_BY_WRITER = "SELECT * FROM posts WHERE writer_id = ? AND status = 'ACTIVE'";
    private final String GET_ALL_LABELS_BY_POST = "SELECT * FROM posts WHERE post_id = ? AND status = 'ACTIVE'";
    private final String SAVE_WRITER = "INSERT INTO writers (first_name, last_name) VALUES (?, ?)";
    private final String SAVE_POSTS_BY_WRITER = "INSERT INTO posts (title, content, writer_id) VALUES (?, ?, ?)";
    private final String SAVE_LABELS_BY_POST = "INSERT INTO labels (name, post_id) VALUES (?, ?)";
    private final String UPDATE_WRITER = "UPDATE writers SET first_name = ?, last_name = ? WHERE id = ?";
    private final String UPDATE_POSTS_BY_WRITER = "UPDATE posts SET title = ?, content = ? WHERE id = ?";
    private final String UPDATE_LABELS_BY_POST = "UPDATE labels SET name = ? WHERE id = ? AND post_id = ?";
    private final String DELETE_WRITER = "UPDATE writers SET status = 'DELETED' WHERE id = ?";
    private final String DELETE_POSTS_BY_WRITER = "UPDATE posts SET status = 'DELETED' WHERE writer_id = ?";
    private final String GET_POSTS_ID = "SELECT id FROM posts WHERE writer_id = ?";
    private final String DELETE_LABELS_BY_POST = "UPDATE labels SET status = 'DELETED' WHERE post_id = ?";
    private WriterMapper writerMapper = new WriterMapper();
    private PostMapper postMapper = new PostMapper();
    private LabelMapper labelMapper = new LabelMapper();


    @Override
    public Writer getById(Long id) {
        try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatement(GET_WRITERS)) {
            preparedStatement.setLong(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                List<Post> posts = getPosts(rs.getLong("id"));

                return writerMapper.map(rs, posts);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    @Override
    public List<Writer> getAll() {
        List<Writer> writers = new ArrayList<>();
        try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatement(GET_ALL_WRITERS);) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                List<Post> allPosts = getAllPosts(resultSet.getLong("id"));
                writers.add(writerMapper.map(resultSet, allPosts));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return writers;
    }

    @Override
    public Writer save(Writer writer) {
        try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatementWithKeys(SAVE_WRITER)) {
            preparedStatement.setString(1, writer.getFirstName());
            preparedStatement.setString(2, writer.getLastName());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        writer.setId(resultSet.getLong(1));
                        List<Post> postList = savePost(writer.getPosts(), writer.getId());
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
        try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatement(UPDATE_WRITER)) {
            preparedStatement.setString(1, writer.getFirstName());
            preparedStatement.setString(2, writer.getLastName());
            preparedStatement.setLong(3, writer.getId());
            List<Post> postList = updatePosts(writer.getPosts(), writer.getId());
            writer.setPosts(postList);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return writer;
    }

    @Override
    public void deleteById(Long id) {

        try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatement(DELETE_WRITER)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();

            deletePosts(id);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private List<Post> getPosts(Long writerId) {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatement(GET_POSTS_BY_WRITER)) {
            preparedStatement.setLong(1, writerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                List<Label> labels = getLabels(resultSet.getLong("id"));
                posts.add(postMapper.map(resultSet, labels));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return posts;
    }

    private List<Label> getLabels(Long postId) {
        List<Label> labels = new ArrayList<>();
        try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatement(GET_LABELS_BY_POST)) {
            preparedStatement.setLong(1, postId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                labels.add(labelMapper.map(resultSet));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return labels;
    }

    private List<Post> getAllPosts(Long writerId) {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatement(GET_ALL_POSTS_BY_WRITER)) {
            preparedStatement.setLong(1, writerId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                List<Label> allLabels = getAllLabels(resultSet.getLong("id"));
                posts.add(postMapper.map(resultSet, allLabels));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return posts;
    }

    private List<Label> getAllLabels(Long postId) {
        List<Label> labels = new ArrayList<>();
        try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatement(GET_ALL_LABELS_BY_POST)) {
            preparedStatement.setLong(1, postId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                labels.add(labelMapper.map(resultSet));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return labels;
    }

    private List<Post> savePost(List<Post> posts, Long writerId) {
        posts.forEach(post -> {
            try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatement(SAVE_POSTS_BY_WRITER)) {
                preparedStatement.setString(1, post.getTitle());
                preparedStatement.setString(2, post.getContent());
                preparedStatement.setLong(3, writerId);
                int affectedRow = preparedStatement.executeUpdate();

                if (affectedRow > 0) {
                    ResultSet resultSet = preparedStatement.getGeneratedKeys();
                    if (resultSet.next()) {
                        post.setId(resultSet.getLong(1));
                        List<Label> labelList = saveLabels(post.getLabels(), post.getId());
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

    private List<Label> saveLabels(List<Label> labels, Long postId) {
        labels.forEach(label -> {
            try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatement(SAVE_LABELS_BY_POST)) {
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

    private List<Post> updatePosts(List<Post> posts, Long wirterId) {
        posts.forEach(post -> {
            if (post.getId() == null) {
                try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatementWithKeys(SAVE_POSTS_BY_WRITER)) {
                    preparedStatement.setString(1, post.getTitle());
                    preparedStatement.setString(2, post.getContent());
                    preparedStatement.setLong(3, wirterId);

                    int affectedRows = preparedStatement.executeUpdate();

                    if (affectedRows > 0) {
                        try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                            if (rs.next()) {
                                post.setId(rs.getLong(1));
                                List<Label> labelList = updateLabels(post.getLabels(), post.getId());
                                post.setLabels(labelList);
                            }
                        }
                    }
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            } else {
                try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatement(UPDATE_POSTS_BY_WRITER)) {
                    preparedStatement.setString(1, post.getTitle());
                    preparedStatement.setString(2, post.getContent());
                    preparedStatement.setLong(3, post.getId());
                    List<Label> labelList = updateLabels(post.getLabels(), post.getId());
                    post.setLabels(labelList);

                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        });

        return posts;
    }

    private List<Label> updateLabels(List<Label> labels, Long postId) {
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
                try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatement(UPDATE_LABELS_BY_POST)) {
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

    private void deletePosts(Long writerId) {
        try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatement(DELETE_POSTS_BY_WRITER)) {
            preparedStatement.setLong(1, writerId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatement(GET_POSTS_ID)) {
            preparedStatement.setLong(1, writerId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Long> postsId = new ArrayList<>();
            while (resultSet.next()) {
                postsId.add(resultSet.getLong("id"));
            }
            if (!postsId.isEmpty()) {
                deleteLabels(postsId);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private void deleteLabels(List<Long> postsId) {
        postsId.forEach(id -> {
            try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatement(DELETE_LABELS_BY_POST)) {
                preparedStatement.setLong(1, id);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        });
    }
}
