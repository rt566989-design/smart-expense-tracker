package com.expensetracker.servlet;

import com.expensetracker.dao.BudgetDAO;
import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.Budget;
import com.expensetracker.model.CategorySummary;
import com.expensetracker.model.Expense;
import com.expensetracker.model.MonthlySummary;
import com.expensetracker.model.User;
import com.expensetracker.util.DateUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Controller servlet for the main application Dashboard.
 * Gathers monthly spending summaries, budget limits, warning thresholds,
 * category breakdowns, and recent transactions.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ExpenseDAO expenseDAO;
    private BudgetDAO budgetDAO;

    @Override
    public void init() throws ServletException {
        expenseDAO = new ExpenseDAO();
        budgetDAO = new BudgetDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        int userId = user.getId();

        // Selected month or current month (YYYY-MM)
        String selectedMonth = request.getParameter("month");
        if (selectedMonth == null || !DateUtil.isValidMonthYear(selectedMonth)) {
            selectedMonth = DateUtil.getCurrentMonthYear();
        }

        // Fetch monthly budget and total expenses
        Budget budget = budgetDAO.getBudget(userId, selectedMonth);
        BigDecimal totalExpenses = expenseDAO.getTotalExpensesForMonth(userId, selectedMonth);
        int expenseCount = expenseDAO.getExpenseCountForMonth(userId, selectedMonth);

        BigDecimal budgetAmount = (budget != null) ? budget.getAmount() : BigDecimal.ZERO;
        BigDecimal remainingBalance = budgetAmount.subtract(totalExpenses);

        // Budget utilization percentage and alert status
        double budgetPercentage = 0.0;
        String budgetStatus = "NO_BUDGET"; // NO_BUDGET, NORMAL, WARNING (80%+), DANGER (>100%)

        if (budget != null && budgetAmount.compareTo(BigDecimal.ZERO) > 0) {
            budgetPercentage = totalExpenses.divide(budgetAmount, 4, RoundingMode.HALF_UP)
                                           .multiply(new BigDecimal("100"))
                                           .doubleValue();

            if (budgetPercentage >= 100.0) {
                budgetStatus = "DANGER"; // Budget exceeded!
            } else if (budgetPercentage >= 80.0) {
                budgetStatus = "WARNING"; // Spent 80% or more
            } else {
                budgetStatus = "NORMAL";
            }
        }

        // Category breakdown for selected month
        List<CategorySummary> categorySummaries = expenseDAO.getCategoryExpensesForMonth(userId, selectedMonth);

        // Recent 5 expenses across all time
        List<Expense> recentExpenses = expenseDAO.getRecentExpenses(userId, 5);

        // Past 6 months history
        List<MonthlySummary> monthlyHistory = expenseDAO.getMonthlyHistory(userId, 6);

        // Set request attributes
        request.setAttribute("selectedMonth", selectedMonth);
        request.setAttribute("friendlySelectedMonth", DateUtil.formatMonthYear(selectedMonth));
        request.setAttribute("budget", budget);
        request.setAttribute("budgetAmount", budgetAmount);
        request.setAttribute("totalExpenses", totalExpenses);
        request.setAttribute("remainingBalance", remainingBalance);
        request.setAttribute("budgetPercentage", Math.min(budgetPercentage, 100.0));
        request.setAttribute("rawBudgetPercentage", budgetPercentage);
        request.setAttribute("budgetStatus", budgetStatus);
        request.setAttribute("expenseCount", expenseCount);
        request.setAttribute("categorySummaries", categorySummaries);
        request.setAttribute("recentExpenses", recentExpenses);
        request.setAttribute("monthlyHistory", monthlyHistory);

        request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
    }
}
