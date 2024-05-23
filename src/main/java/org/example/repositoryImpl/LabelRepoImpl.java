package org.example.repositoryImpl;


import org.example.model.Label;
import org.example.model.Status;
import org.example.repositoryInterface.LabelRepo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LabelRepoImpl implements LabelRepo {
    private String url = "jdbc:mysql://localhost:3306/yarikhanov_khasan";
    private String user = "username";

    private String password = "password";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public LabelRepoImpl(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public LabelRepoImpl() {
    }

    @Override
    public Label getById(Long id) {
        String query = "SELECT * FROM labels WHERE id = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                Label label = new Label();
                label.setName(rs.getString("name"));
                label.setId(rs.getLong("id"));

                return label;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    @Override
    public List<Label> getAll() {
        List<Label> labels = new ArrayList<>();
        String query = "SELECT * FROM labels WHERE status = 'ACTIVE'";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
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

    @Override
    public Label save(Label label) {
        String query = "INSERT INTO labels (name, status) VALUES (?, ?)";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, label.getName());
            preparedStatement.setString(2, Status.ACTIVE.toString());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                    if (rs.next()) {
                        label.setId(rs.getLong(1));
                        return label;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    @Override
    public Label update(Label label) {
        String query = "UPDATE labels SET name = ? WHERE id = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, label.getName());
            preparedStatement.setLong(2, label.getId());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                return label;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    @Override
    public void deleteById(Long id) {
        String query = "UPDATE labels SET status = 'DELETED' WHERE id = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
