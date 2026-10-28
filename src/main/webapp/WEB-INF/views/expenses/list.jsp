<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="My Expenses" />
    <jsp:param name="activePage" value="expenses" />
</jsp:include>

<div class="page-header">
    <div>
        <h1 class="page-title">Expense Records</h1>
        <p class="page-subtitle">Track, filter, and manage all your logged transactions</p>
    </div>

    <a href="${pageContext.request.contextPath}/expenses?action=new" class="btn btn-primary">
        + Add New Expense
    </a>
</div>

<!-- Filters Bar Card -->
<div class="card" style="margin-bottom: 1.5rem;">
    <div class="card-body" style="padding: 1rem 1.25rem;">
        <form action="${pageContext.request.contextPath}/expenses" method="GET" style="display: flex; gap: 0.75rem; flex-wrap: wrap; align-items: flex-end;">
            <input type="hidden" name="action" value="list">

            <!-- Month Filter -->
            <div style="flex: 1; min-width: 150px;">
                <label class="form-label" style="font-size: 0.8rem; margin-bottom: 0.2rem;">Month</label>
                <input type="month" 
                       name="month" 
                       class="form-control" 
                       value="<c:out value='${selectedMonth}' />" 
                       style="padding: 0.45rem 0.65rem;">
            </div>

            <!-- Category Filter -->
            <div style="flex: 1; min-width: 150px;">
                <label class="form-label" style="font-size: 0.8rem; margin-bottom: 0.2rem;">Category</label>
                <select name="category" class="form-control" style="padding: 0.45rem 0.65rem;">
                    <option value="ALL">All Categories</option>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat}" ${selectedCategory == cat ? 'selected' : ''}>${cat}</option>
                    </c:forEach>
                </select>
            </div>

            <!-- Sort By -->
            <div style="flex: 1; min-width: 160px;">
                <label class="form-label" style="font-size: 0.8rem; margin-bottom: 0.2rem;">Sort Order</label>
                <select name="sortBy" class="form-control" style="padding: 0.45rem 0.65rem;">
                    <option value="date_desc" ${selectedSortBy == 'date_desc' ? 'selected' : ''}>Date (Newest First)</option>
                    <option value="date_asc" ${selectedSortBy == 'date_asc' ? 'selected' : ''}>Date (Oldest First)</option>
                    <option value="amount_desc" ${selectedSortBy == 'amount_desc' ? 'selected' : ''}>Amount (Highest First)</option>
                    <option value="amount_asc" ${selectedSortBy == 'amount_asc' ? 'selected' : ''}>Amount (Lowest First)</option>
                </select>
            </div>

            <!-- Search Keyword -->
            <div style="flex: 2; min-width: 180px;">
                <label class="form-label" style="font-size: 0.8rem; margin-bottom: 0.2rem;">Search</label>
                <input type="text" 
                       name="search" 
                       class="form-control" 
                       placeholder="Search description or payment..." 
                       value="<c:out value='${searchQuery}' />" 
                       style="padding: 0.45rem 0.65rem;">
            </div>

            <!-- Action Buttons -->
            <div style="display: flex; gap: 0.4rem;">
                <button type="submit" class="btn btn-primary" style="padding: 0.45rem 0.9rem;">
                    Filter
                </button>
                <a href="${pageContext.request.contextPath}/expenses" class="btn btn-secondary" style="padding: 0.45rem 0.75rem;">
                    Reset
                </a>
            </div>
        </form>
    </div>
</div>

<!-- Expenses Table Card -->
<div class="card">
    <div class="card-header">
        <h2 class="card-title">
            Expense List (${expenses.size()} ${expenses.size() == 1 ? 'record' : 'records'})
        </h2>
    </div>
    <div class="card-body" style="padding: 0;">
        <c:choose>
            <c:when test="${not empty expenses}">
                <div class="table-responsive">
                    <table class="custom-table">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Date</th>
                                <th>Category</th>
                                <th>Description</th>
                                <th>Payment Method</th>
                                <th style="text-align: right;">Amount</th>
                                <th style="text-align: center; width: 140px;">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:set var="totalSum" value="0" />
                            <c:forEach var="e" items="${expenses}" varStatus="status">
                                <c:set var="totalSum" value="${totalSum + e.amount}" />
                                <tr>
                                    <td style="color: var(--text-muted); font-size: 0.8rem;">
                                        ${status.index + 1}
                                    </td>
                                    <td style="font-weight: 500; white-space: nowrap;">
                                        ${e.formattedDate}
                                    </td>
                                    <td>
                                        <span class="badge ${e.categoryBadgeClass}">
                                            <c:out value="${e.category}" />
                                        </span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty e.description}">
                                                <c:out value="${e.description}" />
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: var(--text-muted); font-style: italic;">No description</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <span style="font-size: 0.85rem; color: var(--secondary);">
                                            <c:out value="${e.paymentMethod}" />
                                        </span>
                                    </td>
                                    <td style="text-align: right; font-weight: 700; color: var(--dark); white-space: nowrap;">
                                        ₹${e.formattedAmount}
                                    </td>
                                    <td style="text-align: center;">
                                        <div style="display: inline-flex; gap: 0.4rem; justify-content: center;">
                                            <a href="${pageContext.request.contextPath}/expenses?action=edit&id=${e.id}" 
                                               class="btn btn-secondary btn-sm" 
                                               title="Edit this expense">
                                                Edit
                                            </a>

                                            <!-- POST deletion form for secure CSRF / HTTP method semantics -->
                                            <form action="${pageContext.request.contextPath}/expenses" 
                                                  method="POST" 
                                                  onsubmit="return confirmAction('Are you sure you want to delete this expense record?');" 
                                                  style="display: inline;">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="id" value="${e.id}">
                                                <button type="submit" class="btn btn-danger btn-sm" title="Delete record">
                                                    Delete
                                                </button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                        <tfoot>
                            <tr style="background-color: #f1f5f9; font-weight: 700;">
                                <td colspan="5" style="text-align: right; padding-right: 1.5rem;">
                                    Total of Filtered Expenses:
                                </td>
                                <td style="text-align: right; color: var(--primary); font-size: 1.05rem;">
                                    ₹<fmt:formatNumber value="${totalSum}" pattern="#,##0.00" />
                                </td>
                                <td></td>
                            </tr>
                        </tfoot>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <div class="empty-icon">🔍</div>
                    <h3 class="empty-title">No Expenses Found</h3>
                    <p class="empty-desc">
                        No expenses match your active filter criteria. 
                        Try resetting filters or log a new transaction.
                    </p>
                    <a href="${pageContext.request.contextPath}/expenses?action=new" class="btn btn-primary btn-sm">
                        + Add Expense
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
