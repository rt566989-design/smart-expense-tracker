<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle != null ? pageTitle : 'Smart Expense Tracker'} | Mini Project</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<header class="navbar">
    <div class="nav-container">
        <a href="${pageContext.request.contextPath}/dashboard" class="brand-logo">
            <div class="brand-icon">₹</div>
            <span>Smart Expense Tracker</span>
        </a>

        <c:choose>
            <c:when test="${not empty sessionScope.user}">
                <ul class="nav-links">
                    <li>
                        <a href="${pageContext.request.contextPath}/dashboard" 
                           class="nav-link ${activePage == 'dashboard' ? 'active' : ''}">
                            Dashboard
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/expenses" 
                           class="nav-link ${activePage == 'expenses' ? 'active' : ''}">
                            Expenses
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/budget" 
                           class="nav-link ${activePage == 'budget' ? 'active' : ''}">
                            Budget
                        </a>
                    </li>
                </ul>

                <div class="nav-user">
                    <span class="user-badge">
                        User: <span class="user-name"><c:out value="${sessionScope.user.fullName}" /></span>
                    </span>
                    <a href="${pageContext.request.contextPath}/login" class="btn-secondary btn-sm" style="border-radius: var(--radius-sm); padding: 0.35rem 0.65rem;" title="Switch user profile">
                        Switch User
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="nav-links">
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary btn-sm">Select Profile</a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</header>

<main class="main-wrapper">
    <jsp:include page="/WEB-INF/views/common/alerts.jsp" />
