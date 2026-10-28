<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="500 Internal Server Error" />
</jsp:include>

<div class="empty-state" style="padding: 5rem 1rem;">
    <div class="empty-icon" style="font-size: 4rem; color: var(--danger);">500</div>
    <h1 class="page-title" style="margin-bottom: 0.5rem;">Internal Server Error</h1>
    <p class="empty-desc" style="max-width: 520px; margin-bottom: 1.5rem;">
        An unexpected error occurred while processing your request. 
        Please verify that your MySQL database server is running and database configuration in <code>db.properties</code> is correct.
    </p>

    <c:if test="${not empty pageContext.exception}">
        <div style="max-width: 650px; margin: 1.5rem auto; text-align: left; background: #fff1f2; border: 1px solid #fecdd3; border-radius: var(--radius-sm); padding: 1rem; font-size: 0.82rem; color: #9f1239; overflow-x: auto;">
            <strong>Error Details:</strong>
            <pre style="margin-top: 0.5rem; font-family: monospace;">${pageContext.exception.message}</pre>
        </div>
    </c:if>

    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">
        &larr; Return to Dashboard
    </a>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
