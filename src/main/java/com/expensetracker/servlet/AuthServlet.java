package com.expensetracker.servlet;

import com.expensetracker.dao.UserDAO;
import com.expensetracker.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet managing user profile selection and registration without password barriers.
 */
@WebServlet(name = "AuthServlet", urlPatterns = {"/login", "/register", "/logout"})
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/logout".equals(path)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute("user");
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login?success=logged_out");
            return;
        }

        if ("/register".equals(path)) {
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
        } else {
            // Default to profile switch / login page
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        if ("/register".equals(path)) {
            handleRegister(request, response);
        } else if ("/login".equals(path)) {
            handleLogin(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");

        // Quick default login if empty or clicked direct demo button
        if (email == null || email.trim().isEmpty()) {
            email = "john@example.com";
        }

        email = email.trim().toLowerCase();
        User user = userDAO.getUserByEmail(email);

        // If user doesn't exist, auto-create on the fly so login never fails!
        if (user == null) {
            String name = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
            user = new User(capitalize(name), email, "NOPASSWORD", "NOPASSWORD");
            userDAO.registerUser(user);
            user = userDAO.getUserByEmail(email);
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");

        if (fullName == null || fullName.trim().isEmpty() || email == null || email.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Please provide both Full Name and Email.");
            request.setAttribute("enteredFullName", fullName);
            request.setAttribute("enteredEmail", email);
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }

        fullName = fullName.trim();
        email = email.trim().toLowerCase();

        User existing = userDAO.getUserByEmail(email);
        if (existing == null) {
            User newUser = new User(fullName, email, "NOPASSWORD", "NOPASSWORD");
            userDAO.registerUser(newUser);
            existing = userDAO.getUserByEmail(email);
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("user", existing);
        response.sendRedirect(request.getContextPath() + "/dashboard?success=registered");
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return "User";
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
