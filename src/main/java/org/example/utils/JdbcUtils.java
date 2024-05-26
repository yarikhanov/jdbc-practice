package org.example.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class JdbcUtils {
    private static final Properties properties = new Properties();
    private static Connection connection;

    private static Connection getConnection() {
        if (connection == null) {
            try {
                return connection = DriverManager.getConnection(
                        properties.getProperty("spring.datasource.url"),
                        properties.getProperty("spring.datasource.username"),
                        properties.getProperty("spring.datasource.password")
                );
            } catch (SQLException e) {
                System.err.println("Connection lost");
                System.exit(1);
                return null;
            }
        } else {
            return connection;
        }
    }

    public static PreparedStatement getPreparedStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }

    public static PreparedStatement getPreparedStatementWithKeys(String sql) throws SQLException {
        return getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }
}
