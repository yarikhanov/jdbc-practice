package org.example.repository.jdbc;


import org.example.mapper.LabelMapper;
import org.example.model.Label;
import org.example.model.Status;
import org.example.repository.LabelRepo;
import org.example.utils.JdbcUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcLabelRepoImpl implements LabelRepo {
    private final String GET_LABEL_BY_ID = "SELECT * FROM labels WHERE id = ?";
    private final String GET_ALL_LABELS = "SELECT * FROM labels WHERE status = 'ACTIVE'";
    private final String SAVE_LABEL = "INSERT INTO labels (name, status) VALUES (?, ?)";
    private final String UPDATE_LABEL = "UPDATE labels SET name = ? WHERE id = ?";
    private final String DELETE_LABEL = "UPDATE labels SET status = 'DELETED' WHERE id = ?";

    LabelMapper labelMapper = new LabelMapper();

    public JdbcLabelRepoImpl() {
    }

    @Override
    public Label getById(Long id) {
        try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatement(GET_LABEL_BY_ID)) {
            preparedStatement.setLong(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return labelMapper.map(rs);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    @Override
    public List<Label> getAll() {
        List<Label> labels = new ArrayList<>();
        try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatement(GET_ALL_LABELS)) {
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                labels.add(labelMapper.map(rs));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return labels;
    }

    @Override
    public Label save(Label label) {
        try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatementWithKeys(SAVE_LABEL)) {
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
        try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatementWithKeys(UPDATE_LABEL)) {
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
        try (PreparedStatement preparedStatement = JdbcUtils.getPreparedStatement(DELETE_LABEL)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
