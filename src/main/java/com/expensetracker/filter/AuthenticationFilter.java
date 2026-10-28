package com.expensetracker.filter;

import com.expensetracker.dao.UserDAO;
import com.expensetracker.model.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filter that provides seamless access without requiring password authentication.
 * Automatically initializes session with default user if no active session exists.
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/*"})
public class AuthenticationFilter implements Filter {

    private UserDAO userDAO;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = uri.substring(contextPath.length());

        // Static assets pass through directly
        boolean isStaticAsset = path.startsWith("/assets/") || path.endsWith(".css") || 
                               path.endsWith(".js") || path.endsWith(".png") || path.endsWith(".ico");
        if (isStaticAsset) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = request.getSession(true);
        User currentUser = (User) session.getAttribute("user");

        // If no user session, auto-login default user (John Doe, id=1) so no password is required!
        if (currentUser == null) {
            currentUser = userDAO.getUserById(1);
            if (currentUser == null) {
                currentUser = userDAO.getUserByEmail("john@example.com");
            }
            if (currentUser != null) {
                session.setAttribute("user", currentUser);
            }
        }

        // Set no-cache headers for dynamic views
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }
}
