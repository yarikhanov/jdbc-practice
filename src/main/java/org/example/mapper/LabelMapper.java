package org.example.mapper;

import org.example.model.Label;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LabelMapper {
    public Label map(ResultSet rs) throws SQLException {
        Label label = new Label();
        label.setId(rs.getLong("id"));
        label.setName(rs.getString("name"));

        return label;
    }
}
