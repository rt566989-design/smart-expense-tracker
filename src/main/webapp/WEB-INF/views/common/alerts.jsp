<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- 1. Explicit Request Attribute Error Message --%>
<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger">
        <strong>Error:</strong> <c:out value="${errorMessage}" />
    </div>
</c:if>

<%-- 2. Explicit Request Attribute Success Message --%>
<c:if test="${not empty successMessage}">
    <div class="alert alert-success">
        <strong>Success:</strong> <c:out value="${successMessage}" />
    </div>
</c:if>

<%-- 3. Query Parameter-based Success Alerts --%>
<c:if test="${not empty param.success}">
    <div class="alert alert-success">
        <strong>Success:</strong>
        <c:choose>
            <c:when test="${param.success == 'added'}">
                Expense record was added successfully.
            </c:when>
            <c:when test="${param.success == 'updated'}">
                Expense record was updated successfully.
            </c:when>
            <c:when test="${param.success == 'deleted'}">
                Record was deleted successfully.
            </c:when>
            <c:when test="${param.success == 'saved'}">
                Monthly budget has been updated successfully.
            </c:when>
            <c:when test="${param.success == 'registered'}">
                Account created successfully! Please log in with your credentials.
            </c:when>
            <c:when test="${param.success == 'logged_out'}">
                You have been securely logged out of your session.
            </c:when>
            <c:otherwise>
                Operation completed successfully.
            </c:otherwise>
        </c:choose>
    </div>
</c:if>

<%-- 4. Query Parameter-based Error Alerts --%>
<c:if test="${not empty param.error}">
    <div class="alert alert-danger">
        <strong>Notice:</strong>
        <c:choose>
            <c:when test="${param.error == 'auth_required'}">
                Please log in to access your expenses and budget tracker.
            </c:when>
            <c:when test="${param.error == 'session_expired'}">
                Your session has expired. Please log in again to continue.
            </c:when>
            <c:when test="${param.error == 'not_found'}">
                The requested record could not be found or access was denied.
            </c:when>
            <c:when test="${param.error == 'invalid_id'}">
                Invalid record identifier specified.
            </c:when>
            <c:when test="${param.error == 'delete_failed'}">
                Could not delete the record. Please check database permissions.
            </c:when>
            <c:when test="${param.error == 'save_failed'}">
                Could not save monthly budget. Please verify input amounts.
            </c:when>
            <c:when test="${param.error == 'non_positive_amount'}">
                Budget amount must be a positive number greater than zero.
            </c:when>
            <c:otherwise>
                An unexpected error occurred. Please try again.
            </c:otherwise>
        </c:choose>
    </div>
</c:if>
