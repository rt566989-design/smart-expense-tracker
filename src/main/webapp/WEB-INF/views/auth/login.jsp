<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Select User Profile" />
</jsp:include>

<div class="auth-wrapper">
    <div class="auth-card" style="max-width: 460px;">
        <div class="auth-header">
            <h1 class="auth-title">Select User Profile</h1>
            <p class="auth-subtitle">No password required. Instant 1-click access!</p>
        </div>

        <!-- 1-Click Direct Demo Login -->
        <form action="${pageContext.request.contextPath}/login" method="POST" style="margin-bottom: 1.25rem;">
            <input type="hidden" name="email" value="john@example.com">
            <button type="submit" class="btn btn-primary" style="width: 100%; padding: 0.85rem; font-size: 1rem; font-weight: 600;">
                ⚡ Continue as John Doe (Default Demo)
            </button>
            <div style="font-size: 0.78rem; color: var(--text-muted); text-align: center; margin-top: 0.35rem;">
                (Preloaded with October 2026 budget &amp; 80% warning test)
            </div>
        </form>

        <form action="${pageContext.request.contextPath}/login" method="POST" style="margin-bottom: 1.5rem;">
            <input type="hidden" name="email" value="demo@tracker.com">
            <button type="submit" class="btn btn-secondary" style="width: 100%; padding: 0.65rem;">
                Continue as Alice Smith
            </button>
        </form>

        <div style="display: flex; align-items: center; margin: 1.25rem 0; color: var(--text-muted); font-size: 0.8rem;">
            <div style="flex: 1; height: 1px; background: var(--border);"></div>
            <span style="padding: 0 0.75rem;">OR ENTER EMAIL</span>
            <div style="flex: 1; height: 1px; background: var(--border);"></div>
        </div>

        <!-- Custom Email Login (No Password Required) -->
        <form action="${pageContext.request.contextPath}/login" method="POST">
            <div class="form-group">
                <label for="email" class="form-label">Email Address</label>
                <input type="email" 
                       id="email" 
                       name="email" 
                       class="form-control" 
                       placeholder="e.g. yourname@example.com" 
                       required>
                <span class="form-hint">Auto-creates profile if email is new</span>
            </div>

            <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 0.5rem;">
                Open Dashboard
            </button>
        </form>

        <div class="auth-footer" style="margin-top: 1.25rem;">
            Want to create a custom profile? 
            <a href="${pageContext.request.contextPath}/register">Add new profile</a>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
