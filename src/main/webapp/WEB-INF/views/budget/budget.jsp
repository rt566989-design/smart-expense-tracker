<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Monthly Budget" />
    <jsp:param name="activePage" value="budget" />
</jsp:include>

<div class="page-header">
    <div>
        <h1 class="page-title">Monthly Budget Management</h1>
        <p class="page-subtitle">Configure spending targets and monitor alerts for <strong>${friendlySelectedMonth}</strong></p>
    </div>

    <!-- Month Selector -->
    <form action="${pageContext.request.contextPath}/budget" method="GET" style="display: flex; gap: 0.5rem; align-items: center;">
        <input type="month" 
               name="month" 
               class="form-control auto-submit-month" 
               value="${selectedMonth}" 
               style="width: auto; padding: 0.45rem 0.75rem;">
        <noscript><button type="submit" class="btn btn-secondary btn-sm">Switch Month</button></noscript>
    </form>
</div>

<!-- Main Budget Status Card -->
<div class="card" style="margin-bottom: 2rem;">
    <div class="card-header">
        <h2 class="card-title">Budget Health for ${friendlySelectedMonth}</h2>
        <c:choose>
            <c:when test="${status == 'DANGER'}">
                <span class="badge" style="background: #fee2e2; color: #b91c1c; font-size: 0.85rem;">
                    ⚠️ Limit Exceeded (<fmt:formatNumber value="${rawPercentage}" pattern="0.0" />%)
                </span>
            </c:when>
            <c:when test="${status == 'WARNING'}">
                <span class="badge" style="background: #fef3c7; color: #92400e; font-size: 0.85rem;">
                    ⚠️ 80% Warning Triggered (<fmt:formatNumber value="${rawPercentage}" pattern="0.0" />%)
                </span>
            </c:when>
            <c:when test="${status == 'NORMAL'}">
                <span class="badge" style="background: #ecfdf5; color: #065f46; font-size: 0.85rem;">
                    ✓ Within Budget (<fmt:formatNumber value="${rawPercentage}" pattern="0.0" />%)
                </span>
            </c:when>
            <c:otherwise>
                <span class="badge" style="background: #f1f5f9; color: #475569; font-size: 0.85rem;">
                    No Budget Defined
                </span>
            </c:otherwise>
        </c:choose>
    </div>
    <div class="card-body">
        <div class="stats-grid" style="margin-bottom: 1.25rem;">
            <div class="stat-card" style="box-shadow: none; background: #f8fafc;">
                <div class="stat-header">
                    <span class="stat-label">Budget Allocated</span>
                    <div class="stat-icon icon-orange">🎯</div>
                </div>
                <div class="stat-value">
                    ₹<fmt:formatNumber value="${budgetLimit}" pattern="#,##0.00" />
                </div>
                <div class="stat-footer">Monthly target for ${friendlySelectedMonth}</div>
            </div>

            <div class="stat-card" style="box-shadow: none; background: #f8fafc;">
                <div class="stat-header">
                    <span class="stat-label">Actual Spent</span>
                    <div class="stat-icon icon-blue">💸</div>
                </div>
                <div class="stat-value">
                    ₹<fmt:formatNumber value="${totalSpent}" pattern="#,##0.00" />
                </div>
                <div class="stat-footer">Cumulative transactions logged</div>
            </div>

            <div class="stat-card" style="box-shadow: none; background: #f8fafc;">
                <div class="stat-header">
                    <span class="stat-label">Remaining Balance</span>
                    <div class="stat-icon ${remaining >= 0 ? 'icon-green' : 'icon-red'}">⚖️</div>
                </div>
                <div class="stat-value" style="color: ${remaining < 0 ? 'var(--danger)' : 'var(--dark)'};">
                    ₹<fmt:formatNumber value="${remaining}" pattern="#,##0.00" />
                </div>
                <div class="stat-footer">
                    <c:choose>
                        <c:when test="${remaining < 0}">
                            Over budget by ₹<fmt:formatNumber value="${remaining * -1}" pattern="#,##0.00" />
                        </c:when>
                        <c:otherwise>
                            Available spending buffer
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <c:if test="${not empty currentBudget}">
            <div class="budget-progress-container">
                <div style="display: flex; justify-content: space-between; font-size: 0.85rem; margin-bottom: 0.35rem;">
                    <span>Budget Consumption</span>
                    <strong><fmt:formatNumber value="${rawPercentage}" pattern="0.0" />%</strong>
                </div>
                <div class="progress-bar-bg" style="height: 12px;">
                    <div class="progress-bar-fill ${status == 'DANGER' ? 'progress-danger' : (status == 'WARNING' ? 'progress-warning' : 'progress-normal')}" 
                         style="width: ${percentage}%;"></div>
                </div>
                <div class="progress-labels">
                    <span>₹0.00</span>
                    <span>80% Warning Marker</span>
                    <span>Limit: ₹<fmt:formatNumber value="${budgetLimit}" pattern="#,##0.00" /></span>
                </div>
            </div>
        </c:if>
    </div>
</div>

<div style="display: grid; grid-template-columns: 1fr 1.5fr; gap: 1.5rem; align-items: start;">
    <!-- Set / Update Budget Form -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">
                ${not empty currentBudget ? 'Update Budget' : 'Set New Budget'}
            </h2>
        </div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/budget" method="POST">
                <div class="form-group">
                    <label for="monthYear" class="form-label">Target Month *</label>
                    <input type="month" 
                           id="monthYear" 
                           name="monthYear" 
                           class="form-control" 
                           value="${selectedMonth}" 
                           required>
                    <span class="form-hint">Format: YYYY-MM</span>
                </div>

                <div class="form-group">
                    <label for="amount" class="form-label">Monthly Budget Limit (₹) *</label>
                    <input type="number" 
                           id="amount" 
                           name="amount" 
                           class="form-control" 
                           placeholder="e.g. 25000.00" 
                           step="0.01" 
                           min="0.01" 
                           value="${not empty currentBudget ? currentBudget.amount : ''}" 
                           required>
                    <span class="form-hint">Enter your planned spending cap</span>
                </div>

                <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 0.5rem;">
                    ${not empty currentBudget ? 'Update Budget Limit' : 'Save Budget Target'}
                </button>
            </form>
        </div>
    </div>

    <!-- All Configured Budgets Table -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">All Configured Budgets</h2>
        </div>
        <div class="card-body" style="padding: 0;">
            <c:choose>
                <c:when test="${not empty allBudgets}">
                    <div class="table-responsive">
                        <table class="custom-table">
                            <thead>
                                <tr>
                                    <th>Month</th>
                                    <th>Budget Limit</th>
                                    <th>Last Updated</th>
                                    <th style="text-align: center;">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="b" items="${allBudgets}">
                                    <tr>
                                        <td style="font-weight: 600;">
                                            ${b.monthDisplayName}
                                            <c:if test="${b.monthYear == selectedMonth}">
                                                <span class="badge" style="background:#e0f2fe; color:#0369a1; font-size: 0.7rem;">Active</span>
                                            </c:if>
                                        </td>
                                        <td style="font-weight: 700;">
                                            ₹${b.formattedAmount}
                                        </td>
                                        <td style="font-size: 0.8rem; color: var(--text-muted);">
                                            <fmt:formatDate value="${b.updatedAt != null ? b.updatedAt : b.createdAt}" pattern="dd MMM yyyy" />
                                        </td>
                                        <td style="text-align: center;">
                                            <div style="display: inline-flex; gap: 0.4rem;">
                                                <a href="${pageContext.request.contextPath}/budget?month=${b.monthYear}" class="btn btn-secondary btn-sm">
                                                    Select
                                                </a>

                                                <form action="${pageContext.request.contextPath}/budget" method="POST" onsubmit="return confirmAction('Delete budget for this month?');" style="display:inline;">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="id" value="${b.id}">
                                                    <button type="submit" class="btn btn-danger btn-sm">
                                                        Delete
                                                    </button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <div class="empty-icon">🎯</div>
                        <h3 class="empty-title">No Budgets Defined</h3>
                        <p class="empty-desc">
                            Set your first monthly budget using the form to start tracking your financial health.
                        </p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
