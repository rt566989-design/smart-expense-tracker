
package com.expensetracker.dao;

import com.expensetracker.model.Budget;
import com.expensetracker.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Monthly Budget operations using PreparedStatement.
 */
public class BudgetDAO {

    /**
     * Gets the budget record for a given user and month.
     * 
     * @param userId    Logged in user ID
     * @param monthYear Month in "YYYY-MM" format
     * @return Budget object or null if not set
     */
    public Budget getBudget(int userId, String monthYear) {
        String sql = "SELECT id, user_id, month_year, amount, created_at, updated_at " +
                "FROM budgets WHERE user_id = ? AND month_year = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, monthYear);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return extractBudgetFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("[BudgetDAO] Error in getBudget: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return null;
    }

    /**
     * Sets or updates a budget for a given month using UPSERT (ON DUPLICATE KEY
     * UPDATE).
     * Ensures each user has at most one unique budget per month.
     * 
     * @param userId    Logged in user ID
     * @param monthYear Month in "YYYY-MM" format
     * @param amount    Target budget amount
     * @return true if successful, false otherwise
     */
    public boolean setOrUpdateBudget(int userId, String monthYear, BigDecimal amount) {
        String sql = "INSERT INTO budgets (user_id, month_year, amount) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE amount = VALUES(amount), updated_at = CURRENT_TIMESTAMP";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, monthYear);
            stmt.setBigDecimal(3, amount);

            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("[BudgetDAO] Error in setOrUpdateBudget: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt);
        }
        return false;
    }

    /**
     * Retrieves all past and present budgets for a user, sorted descending by
     * month.
     * 
     * @param userId Logged in user ID
     * @return List of Budget records
     */
    public List<Budget> getAllBudgetsByUser(int userId) {
        List<Budget> budgets = new ArrayList<>();
        String sql = "SELECT id, user_id, month_year, amount, created_at, updated_at " +
                "FROM budgets WHERE user_id = ? ORDER BY month_year DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                budgets.add(extractBudgetFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[BudgetDAO] Error in getAllBudgetsByUser: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return budgets;
    }

    /**
     * Deletes a budget record ensuring it belongs to the authenticated user.
     * 
     * @param id     Budget ID
     * @param userId Authenticated user ID
     * @return true if deleted, false otherwise
     */
    public boolean deleteBudget(int id, int userId) {
        String sql = "DELETE FROM budgets WHERE id = ? AND user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BudgetDAO] Error in deleteBudget: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt);
        }
        return false;
    }

    private Budget extractBudgetFromResultSet(ResultSet rs) throws SQLException {
        Budget b = new Budget();
        b.setId(rs.getInt("id"));
        b.setUserId(rs.getInt("user_id"));
        b.setMonthYear(rs.getString("month_year"));
        b.setAmount(rs.getBigDecimal("amount"));
        b.setCreatedAt(rs.getTimestamp("created_at"));
        b.setUpdatedAt(rs.getTimestamp("updated_at"));
        return b;
    }
}
