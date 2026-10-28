package com.expensetracker.dao;

import com.expensetracker.model.CategorySummary;
import com.expensetracker.model.Expense;
import com.expensetracker.model.MonthlySummary;
import com.expensetracker.util.DBConnection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Expense operations using JDBC PreparedStatement.
 * Enforces strict tenant isolation: users can only view and modify their own records.
 */
public class ExpenseDAO {

    /**
     * Adds a new expense record.
     */
    public boolean addExpense(Expense expense) {
        String sql = "INSERT INTO expenses (user_id, amount, category, expense_date, payment_method, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, expense.getUserId());
            stmt.setBigDecimal(2, expense.getAmount());
            stmt.setString(3, expense.getCategory());
            stmt.setDate(4, expense.getExpenseDate());
            stmt.setString(5, expense.getPaymentMethod());
            stmt.setString(6, expense.getDescription());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    expense.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Error in addExpense: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, generatedKeys);
        }
        return false;
    }

    /**
     * Updates an existing expense. Strictly ensures the expense belongs to the user.
     */
    public boolean updateExpense(Expense expense) {
        String sql = "UPDATE expenses SET amount = ?, category = ?, expense_date = ?, " +
                     "payment_method = ?, description = ? WHERE id = ? AND user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setBigDecimal(1, expense.getAmount());
            stmt.setString(2, expense.getCategory());
            stmt.setDate(3, expense.getExpenseDate());
            stmt.setString(4, expense.getPaymentMethod());
            stmt.setString(5, expense.getDescription());
            stmt.setInt(6, expense.getId());
            stmt.setInt(7, expense.getUserId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Error in updateExpense: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt);
        }
        return false;
    }

    /**
     * Deletes an expense. Strictly ensures the expense belongs to the user.
     */
    public boolean deleteExpense(int expenseId, int userId) {
        String sql = "DELETE FROM expenses WHERE id = ? AND user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, expenseId);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Error in deleteExpense: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt);
        }
        return false;
    }

    /**
     * Retrieves an expense by its ID ensuring it belongs to the given user.
     */
    public Expense getExpenseById(int expenseId, int userId) {
        String sql = "SELECT id, user_id, amount, category, expense_date, payment_method, description, created_at " +
                     "FROM expenses WHERE id = ? AND user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, expenseId);
            stmt.setInt(2, userId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return extractExpenseFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Error in getExpenseById: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return null;
    }

    /**
     * Retrieves filtered and sorted expenses for a user.
     * @param userId Logged in user ID
     * @param monthYear Optional month filter (YYYY-MM)
     * @param category Optional category filter
     * @param sortBy Sorting criteria (date_desc, date_asc, amount_desc, amount_asc)
     * @param search Optional search keyword in description or payment method
     */
    public List<Expense> getFilteredExpenses(int userId, String monthYear, String category, String sortBy, String search) {
        List<Expense> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT id, user_id, amount, category, expense_date, payment_method, description, created_at " +
                "FROM expenses WHERE user_id = ? "
        );
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (monthYear != null && !monthYear.trim().isEmpty()) {
            sql.append(" AND DATE_FORMAT(expense_date, '%Y-%m') = ? ");
            params.add(monthYear.trim());
        }

        if (category != null && !category.trim().isEmpty() && !"ALL".equalsIgnoreCase(category)) {
            sql.append(" AND category = ? ");
            params.add(category.trim());
        }

        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (LOWER(description) LIKE ? OR LOWER(payment_method) LIKE ?) ");
            String pattern = "%" + search.trim().toLowerCase() + "%";
            params.add(pattern);
            params.add(pattern);
        }

        // Safe sorting whitelist
        if ("amount_desc".equalsIgnoreCase(sortBy)) {
            sql.append(" ORDER BY amount DESC, expense_date DESC ");
        } else if ("amount_asc".equalsIgnoreCase(sortBy)) {
            sql.append(" ORDER BY amount ASC, expense_date DESC ");
        } else if ("date_asc".equalsIgnoreCase(sortBy)) {
            sql.append(" ORDER BY expense_date ASC, id ASC ");
        } else {
            sql.append(" ORDER BY expense_date DESC, id DESC ");
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(extractExpenseFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Error in getFilteredExpenses: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    /**
     * Computes the total expenses for a user in a given month.
     */
    public BigDecimal getTotalExpensesForMonth(int userId, String monthYear) {
        String sql = "SELECT COALESCE(SUM(amount), 0) AS total FROM expenses " +
                     "WHERE user_id = ? AND DATE_FORMAT(expense_date, '%Y-%m') = ?";
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
                BigDecimal total = rs.getBigDecimal("total");
                return total != null ? total : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Error in getTotalExpensesForMonth: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Retrieves category-wise aggregated expense totals and calculates percentages.
     */
    public List<CategorySummary> getCategoryExpensesForMonth(int userId, String monthYear) {
        List<CategorySummary> summaries = new ArrayList<>();
        BigDecimal monthTotal = getTotalExpensesForMonth(userId, monthYear);

        String sql = "SELECT category, SUM(amount) AS cat_total, COUNT(*) AS cat_count " +
                     "FROM expenses WHERE user_id = ? AND DATE_FORMAT(expense_date, '%Y-%m') = ? " +
                     "GROUP BY category ORDER BY cat_total DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, monthYear);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String cat = rs.getString("category");
                BigDecimal amount = rs.getBigDecimal("cat_total");
                int count = rs.getInt("cat_count");

                double pct = 0.0;
                if (monthTotal.compareTo(BigDecimal.ZERO) > 0) {
                    pct = amount.divide(monthTotal, 4, RoundingMode.HALF_UP)
                                .multiply(new BigDecimal("100"))
                                .doubleValue();
                }

                summaries.add(new CategorySummary(cat, amount, pct, count));
            }
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Error in getCategoryExpensesForMonth: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return summaries;
    }

    /**
     * Gets the most recent expenses for dashboard display.
     */
    public List<Expense> getRecentExpenses(int userId, int limit) {
        List<Expense> list = new ArrayList<>();
        String sql = "SELECT id, user_id, amount, category, expense_date, payment_method, description, created_at " +
                     "FROM expenses WHERE user_id = ? ORDER BY expense_date DESC, id DESC LIMIT ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(extractExpenseFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Error in getRecentExpenses: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    /**
     * Gets monthly spending totals for the past N months.
     */
    public List<MonthlySummary> getMonthlyHistory(int userId, int limitMonths) {
        List<MonthlySummary> list = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(expense_date, '%Y-%m') AS ym, SUM(amount) AS total, COUNT(*) AS cnt " +
                     "FROM expenses WHERE user_id = ? " +
                     "GROUP BY ym ORDER BY ym DESC LIMIT ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, limitMonths);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String ym = rs.getString("ym");
                BigDecimal total = rs.getBigDecimal("total");
                int cnt = rs.getInt("cnt");
                list.add(new MonthlySummary(ym, total, cnt));
            }
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Error in getMonthlyHistory: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return list;
    }

    /**
     * Gets the count of expenses recorded in a given month.
     */
    public int getExpenseCountForMonth(int userId, String monthYear) {
        String sql = "SELECT COUNT(*) FROM expenses WHERE user_id = ? AND DATE_FORMAT(expense_date, '%Y-%m') = ?";
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
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Error in getExpenseCountForMonth: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
        return 0;
    }

    private Expense extractExpenseFromResultSet(ResultSet rs) throws SQLException {
        Expense exp = new Expense();
        exp.setId(rs.getInt("id"));
        exp.setUserId(rs.getInt("user_id"));
        exp.setAmount(rs.getBigDecimal("amount"));
        exp.setCategory(rs.getString("category"));
        exp.setExpenseDate(rs.getDate("expense_date"));
        exp.setPaymentMethod(rs.getString("payment_method"));
        exp.setDescription(rs.getString("description"));
        exp.setCreatedAt(rs.getTimestamp("created_at"));
        return exp;
    }
}

