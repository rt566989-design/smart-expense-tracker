package com.expensetracker.dao;

import com.expensetracker.model.User;
import com.expensetracker.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Data Access Object for User operations using JDBC PreparedStatement.
 */
public class UserDAO {

    /**
     * Registers a new user.
     * @param user User object containing full name, email, password hash, and salt
     * @return true if inserted successfully, false otherwise
     */
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (full_name, email, password_hash, salt) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail().toLowerCase().trim());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getSalt());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error in registerUser: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, generatedKeys);
        }
        return false;
    }

    /**
     * Finds a user by email address.
     * @param email User's email
     * @return User object or null if not found
     */
    public User getUserByEmail(String email) {
        String sql = "SELECT id, full_name, email, password_hash, salt, created_at FROM users WHERE LOWER(email) = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email.toLowerCase().trim());
            rs = stmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error in getUserByEmail: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return null;
    }

    /**
     * Finds a user by primary key id.
     * @param id User ID
     * @return User object or null
     */
    public User getUserById(int id) {
        String sql = "SELECT id, full_name, email, password_hash, salt, created_at FROM users WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error in getUserById: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return null;
    }

    /**
     * Checks if an email is already registered.
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE LOWER(email) = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email.toLowerCase().trim());
            rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error in emailExists: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return false;
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setSalt(rs.getString("salt"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}

