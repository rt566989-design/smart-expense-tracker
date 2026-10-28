package com.expensetracker.servlet;

import com.expensetracker.dao.BudgetDAO;
import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.Budget;
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
 * Controller servlet for Monthly Budget management.
 * Enables setting, updating, and monitoring budget targets per month.
 */
@WebServlet(name = "BudgetServlet", urlPatterns = {"/budget"})
public class BudgetServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private BudgetDAO budgetDAO;
    private ExpenseDAO expenseDAO;

    @Override
    public void init() throws ServletException {
        budgetDAO = new BudgetDAO();
        expenseDAO = new ExpenseDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        int userId = user.getId();

        String selectedMonth = request.getParameter("month");
        if (selectedMonth == null || !DateUtil.isValidMonthYear(selectedMonth)) {
            selectedMonth = DateUtil.getCurrentMonthYear();
        }

        Budget currentBudget = budgetDAO.getBudget(userId, selectedMonth);
        BigDecimal totalSpent = expenseDAO.getTotalExpensesForMonth(userId, selectedMonth);

        BigDecimal budgetLimit = (currentBudget != null) ? currentBudget.getAmount() : BigDecimal.ZERO;
        BigDecimal remaining = budgetLimit.subtract(totalSpent);

        double percentage = 0.0;
        String status = "NO_BUDGET";

        if (currentBudget != null && budgetLimit.compareTo(BigDecimal.ZERO) > 0) {
            percentage = totalSpent.divide(budgetLimit, 4, RoundingMode.HALF_UP)
                                   .multiply(new BigDecimal("100"))
                                   .doubleValue();

            if (percentage >= 100.0) {
                status = "DANGER";
            } else if (percentage >= 80.0) {
                status = "WARNING";
            } else {
                status = "NORMAL";
            }
        }

        List<Budget> allBudgets = budgetDAO.getAllBudgetsByUser(userId);

        request.setAttribute("selectedMonth", selectedMonth);
        request.setAttribute("friendlySelectedMonth", DateUtil.formatMonthYear(selectedMonth));
        request.setAttribute("currentBudget", currentBudget);
        request.setAttribute("budgetLimit", budgetLimit);
        request.setAttribute("totalSpent", totalSpent);
        request.setAttribute("remaining", remaining);
        request.setAttribute("percentage", Math.min(percentage, 100.0));
        request.setAttribute("rawPercentage", percentage);
        request.setAttribute("status", status);
        request.setAttribute("allBudgets", allBudgets);

        request.getRequestDispatcher("/WEB-INF/views/budget/budget.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        int userId = user.getId();

        String action = request.getParameter("action");
        if ("delete".equalsIgnoreCase(action)) {
            handleDeleteBudget(request, response, userId);
            return;
        }

        String monthYear = request.getParameter("monthYear");
        String amountStr = request.getParameter("amount");

        if (monthYear == null || !DateUtil.isValidMonthYear(monthYear)) {
            response.sendRedirect(request.getContextPath() + "/budget?error=invalid_month");
            return;
        }

        if (amountStr == null || amountStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/budget?month=" + monthYear + "&error=missing_amount");
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr.trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                response.sendRedirect(request.getContextPath() + "/budget?month=" + monthYear + "&error=non_positive_amount");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/budget?month=" + monthYear + "&error=invalid_amount");
            return;
        }

        boolean saved = budgetDAO.setOrUpdateBudget(userId, monthYear, amount);
        if (saved) {
            response.sendRedirect(request.getContextPath() + "/budget?month=" + monthYear + "&success=saved");
        } else {
            response.sendRedirect(request.getContextPath() + "/budget?month=" + monthYear + "&error=save_failed");
        }
    }

    private void handleDeleteBudget(HttpServletRequest request, HttpServletResponse response, int userId)
            throws IOException {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            try {
                int budgetId = Integer.parseInt(idStr);
                boolean deleted = budgetDAO.deleteBudget(budgetId, userId);
                if (deleted) {
                    response.sendRedirect(request.getContextPath() + "/budget?success=deleted");
                    return;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        response.sendRedirect(request.getContextPath() + "/budget?error=delete_failed");
    }
}
