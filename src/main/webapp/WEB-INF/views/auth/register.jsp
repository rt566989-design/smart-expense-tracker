<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Create Profile" />
</jsp:include>

<div class="auth-wrapper">
    <div class="auth-card">
        <div class="auth-header">
            <h1 class="auth-title">Create Profile</h1>
            <p class="auth-subtitle">No password required. Instant setup!</p>
        </div>

        <form action="${pageContext.request.contextPath}/register" method="POST">
            <div class="form-group">
                <label for="fullName" class="form-label">Full Name</label>
                <input type="text" 
                       id="fullName" 
                       name="fullName" 
                       class="form-control" 
                       placeholder="e.g. Rahul Sharma" 
                       value="<c:out value='${enteredFullName}' />" 
                       required 
                       autofocus>
            </div>

            <div class="form-group">
                <label for="email" class="form-label">Email Address</label>
                <input type="email" 
                       id="email" 
                       name="email" 
                       class="form-control" 
                       placeholder="e.g. rahul@example.com" 
                       value="<c:out value='${enteredEmail}' />" 
                       required>
                <span class="form-hint">Used to identify your expense records</span>
            </div>

            <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 0.5rem; padding: 0.75rem;">
                Create Profile &amp; Go to Dashboard
            </button>
        </form>

        <div class="auth-footer">
            Already have a profile? 
            <a href="${pageContext.request.contextPath}/login">Switch profile</a>
        </div>

        <jsp:include page="/WEB-INF/views/common/footer.jsp" />