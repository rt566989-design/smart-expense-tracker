package com.expensetracker.servlet;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.Expense;
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
import java.sql.Date;
import java.util.Arrays;
import java.util.List;

/**
 * Controller servlet for CRUD operations on Expenses.
 * Uses GET for read actions and POST for state mutations (Post-Redirect-Get pattern).
 */
@WebServlet(name = "ExpenseServlet", urlPatterns = {"/expenses"})
public class ExpenseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final List<String> VALID_CATEGORIES = Arrays.asList(
            "Food", "Travel", "Bills", "Shopping", "Education", "Other"
    );

    private static final List<String> VALID_PAYMENT_METHODS = Arrays.asList(
            "Cash", "Credit Card", "Debit Card", "UPI", "Net Banking"
    );

    private ExpenseDAO expenseDAO;

    @Override
    public void init() throws ServletException {
        expenseDAO = new ExpenseDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "new":
            case "add":
                showAddForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "list":
            default:
                listExpenses(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            action = "insert";
        }

        switch (action) {
            case "insert":
                insertExpense(request, response);
                break;
            case "update":
                updateExpense(request, response);
                break;
            case "delete":
                deleteExpense(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/expenses");
                break;
        }
    }

    private void listExpenses(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        int userId = user.getId();

        String month = request.getParameter("month");
        String category = request.getParameter("category");
        String sortBy = request.getParameter("sortBy");
        String search = request.getParameter("search");

        List<Expense> expenseList = expenseDAO.getFilteredExpenses(userId, month, category, sortBy, search);

        request.setAttribute("expenses", expenseList);
        request.setAttribute("selectedMonth", month != null ? month : "");
        request.setAttribute("selectedCategory", category != null ? category : "");
        request.setAttribute("selectedSortBy", sortBy != null ? sortBy : "date_desc");
        request.setAttribute("searchQuery", search != null ? search : "");
        request.setAttribute("categories", VALID_CATEGORIES);
        request.setAttribute("paymentMethods", VALID_PAYMENT_METHODS);

        request.getRequestDispatcher("/WEB-INF/views/expenses/list.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("categories", VALID_CATEGORIES);
        request.setAttribute("paymentMethods", VALID_PAYMENT_METHODS);
        request.setAttribute("defaultDate", DateUtil.getTodayDate().toString());
        request.setAttribute("formAction", "insert");
        request.setAttribute("pageTitle", "Add New Expense");

        request.getRequestDispatcher("/WEB-INF/views/expenses/form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        int userId = user.getId();

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/expenses?error=invalid_id");
            return;
        }

        try {
            int expenseId = Integer.parseInt(idParam);
            Expense expense = expenseDAO.getExpenseById(expenseId, userId);

            if (expense == null) {
                response.sendRedirect(request.getContextPath() + "/expenses?error=not_found");
                return;
            }

            request.setAttribute("expense", expense);
            request.setAttribute("categories", VALID_CATEGORIES);
            request.setAttribute("paymentMethods", VALID_PAYMENT_METHODS);
            request.setAttribute("formAction", "update");
            request.setAttribute("pageTitle", "Edit Expense #" + expenseId);

            request.getRequestDispatcher("/WEB-INF/views/expenses/form.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/expenses?error=invalid_id");
        }
    }

    private void insertExpense(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        int userId = user.getId();

        String amountStr = request.getParameter("amount");
        String category = request.getParameter("category");
        String dateStr = request.getParameter("expenseDate");
        String paymentMethod = request.getParameter("paymentMethod");
        String description = request.getParameter("description");

        String validationError = validateExpenseInputs(amountStr, category, dateStr, paymentMethod);
        if (validationError != null) {
            request.setAttribute("errorMessage", validationError);
            retainFormState(request, null, amountStr, category, dateStr, paymentMethod, description);
            request.setAttribute("categories", VALID_CATEGORIES);
            request.setAttribute("paymentMethods", VALID_PAYMENT_METHODS);
            request.setAttribute("formAction", "insert");
            request.setAttribute("pageTitle", "Add New Expense");
            request.getRequestDispatcher("/WEB-INF/views/expenses/form.jsp").forward(request, response);
            return;
        }

        BigDecimal amount = new BigDecimal(amountStr.trim());
        Date expenseDate = DateUtil.parseDate(dateStr);
        if (description != null && description.trim().isEmpty()) {
            description = null;
        } else if (description != null) {
            description = description.trim();
        }

        Expense expense = new Expense(userId, amount, category, expenseDate, paymentMethod, description);
        boolean added = expenseDAO.addExpense(expense);

        if (added) {
            response.sendRedirect(request.getContextPath() + "/expenses?success=added");
        } else {
            request.setAttribute("errorMessage", "Failed to save expense due to a database error.");
            retainFormState(request, null, amountStr, category, dateStr, paymentMethod, description);
            request.setAttribute("categories", VALID_CATEGORIES);
            request.setAttribute("paymentMethods", VALID_PAYMENT_METHODS);
            request.setAttribute("formAction", "insert");
            request.setAttribute("pageTitle", "Add New Expense");
            request.getRequestDispatcher("/WEB-INF/views/expenses/form.jsp").forward(request, response);
        }
    }

    private void updateExpense(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        int userId = user.getId();

        String idStr = request.getParameter("id");
        String amountStr = request.getParameter("amount");
        String category = request.getParameter("category");
        String dateStr = request.getParameter("expenseDate");
        String paymentMethod = request.getParameter("paymentMethod");
        String description = request.getParameter("description");

        int expenseId;
        try {
            expenseId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/expenses?error=invalid_id");
            return;
        }

        String validationError = validateExpenseInputs(amountStr, category, dateStr, paymentMethod);
        if (validationError != null) {
            request.setAttribute("errorMessage", validationError);
            retainFormState(request, expenseId, amountStr, category, dateStr, paymentMethod, description);
            request.setAttribute("categories", VALID_CATEGORIES);
            request.setAttribute("paymentMethods", VALID_PAYMENT_METHODS);
            request.setAttribute("formAction", "update");
            request.setAttribute("pageTitle", "Edit Expense #" + expenseId);
            request.getRequestDispatcher("/WEB-INF/views/expenses/form.jsp").forward(request, response);
            return;
        }

        BigDecimal amount = new BigDecimal(amountStr.trim());
        Date expenseDate = DateUtil.parseDate(dateStr);
        if (description != null && description.trim().isEmpty()) {
            description = null;
        } else if (description != null) {
            description = description.trim();
        }

        Expense expense = new Expense(expenseId, userId, amount, category, expenseDate, paymentMethod, description, null);
        boolean updated = expenseDAO.updateExpense(expense);

        if (updated) {
            response.sendRedirect(request.getContextPath() + "/expenses?success=updated");
        } else {
            response.sendRedirect(request.getContextPath() + "/expenses?error=update_failed");
        }
    }

    private void deleteExpense(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        int userId = user.getId();

        String idStr = request.getParameter("id");
        if (idStr != null) {
            try {
                int expenseId = Integer.parseInt(idStr);
                boolean deleted = expenseDAO.deleteExpense(expenseId, userId);
                if (deleted) {
                    response.sendRedirect(request.getContextPath() + "/expenses?success=deleted");
                    return;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        response.sendRedirect(request.getContextPath() + "/expenses?error=delete_failed");
    }

    private String validateExpenseInputs(String amountStr, String category, String dateStr, String paymentMethod) {
        if (amountStr == null || amountStr.trim().isEmpty()) {
            return "Expense amount is required.";
        }
        try {
            BigDecimal amt = new BigDecimal(amountStr.trim());
            if (amt.compareTo(BigDecimal.ZERO) <= 0) {
                return "Amount must be a positive number greater than 0.";
            }
        } catch (NumberFormatException e) {
            return "Please enter a valid numeric amount (e.g. 150.00).";
        }

        if (category == null || !VALID_CATEGORIES.contains(category)) {
            return "Please select a valid expense category.";
        }

        if (dateStr == null || dateStr.trim().isEmpty()) {
            return "Expense date is required.";
        }

        if (paymentMethod == null || !VALID_PAYMENT_METHODS.contains(paymentMethod)) {
            return "Please select a valid payment method.";
        }

        return null;
    }

    private void retainFormState(HttpServletRequest request, Integer id, String amountStr,
                                 String category, String dateStr, String paymentMethod, String description) {
        Expense e = new Expense();
        if (id != null) {
            e.setId(id);
        }
        try {
            if (amountStr != null && !amountStr.trim().isEmpty()) {
                e.setAmount(new BigDecimal(amountStr.trim()));
            }
        } catch (Exception ignored) {
        }
        e.setCategory(category);
        e.setExpenseDate(DateUtil.parseDate(dateStr));
        e.setPaymentMethod(paymentMethod);
        e.setDescription(description);
        request.setAttribute("expense", e);
    }
}
