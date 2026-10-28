<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="404 Page Not Found" />
</jsp:include>

<div class="empty-state" style="padding: 5rem 1rem;">
    <div class="empty-icon" style="font-size: 4rem; color: var(--primary);">404</div>
    <h1 class="page-title" style="margin-bottom: 0.5rem;">Page Not Found</h1>
    <p class="empty-desc" style="max-width: 480px; margin-bottom: 1.5rem;">
        The requested URL was not found on this server. Please check the address or return to the dashboard.
    </p>
    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">
        &larr; Return to Dashboard
    </a>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
