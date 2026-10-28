<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Dashboard" />
    <jsp:param name="activePage" value="dashboard" />
</jsp:include>

<div class="page-header">
    <div>
        <h1 class="page-title">Financial Dashboard</h1>
        <p class="page-subtitle">Monthly overview and spending analytics for <strong><c:out value="${friendlySelectedMonth}" /></strong></p>
    </div>

    <div style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
        <!-- Month Selector Form -->
        <form action="${pageContext.request.contextPath}/dashboard" method="GET" style="display: flex; gap: 0.5rem; align-items: center;">
            <input type="month" 
                   name="month" 
                   class="form-control auto-submit-month" 
                   value="${selectedMonth}" 
                   style="width: auto; padding: 0.45rem 0.75rem;">
            <noscript><button type="submit" class="btn btn-secondary btn-sm">Filter</button></noscript>
        </form>

        <a href="${pageContext.request.contextPath}/expenses?action=new" class="btn btn-primary">
            + Add Expense
        </a>
    </div>
</div>

<!-- 1. Key Statistics Cards Grid -->
<div class="stats-grid">
    <div class="stat-card">
        <div class="stat-header">
            <span class="stat-label">Total Spent</span>
            <div class="stat-icon icon-blue">₹</div>
        </div>
        <div class="stat-value">₹<fmt:formatNumber value="${totalExpenses}" pattern="#,##0.00" /></div>
        <div class="stat-footer">${expenseCount} transactions recorded</div>
    </div>

    <div class="stat-card">
        <div class="stat-header">
            <span class="stat-label">Monthly Budget</span>
            <div class="stat-icon icon-orange">🎯</div>
        </div>
        <div class="stat-value">
            <c:choose>
                <c:when test="${not empty budget}">
                    ₹<fmt:formatNumber value="${budget.amount}" pattern="#,##0.00" />
                </c:when>
                <c:otherwise>
                    <span style="color: var(--text-muted); font-size: 1.2rem;">Not Set</span>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="stat-footer">
            <c:choose>
                <c:when test="${not empty budget}">
                    Target limit for ${friendlySelectedMonth}
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/budget?month=${selectedMonth}">Set budget now &rarr;</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <div class="stat-card">
        <div class="stat-header">
            <span class="stat-label">Remaining Balance</span>
            <div class="stat-icon ${remainingBalance >= 0 ? 'icon-green' : 'icon-red'}">⚖️</div>
        </div>
        <div class="stat-value" style="color: ${remainingBalance < 0 ? 'var(--danger)' : 'var(--dark)'};">
            <c:choose>
                <c:when test="${not empty budget}">
                    ₹<fmt:formatNumber value="${remainingBalance}" pattern="#,##0.00" />
                </c:when>
                <c:otherwise>
                    <span style="color: var(--text-muted); font-size: 1.2rem;">--</span>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="stat-footer">
            <c:choose>
                <c:when test="${remainingBalance < 0}">
                    Exceeded by ₹<fmt:formatNumber value="${remainingBalance * -1}" pattern="#,##0.00" />
                </c:when>
                <c:when test="${not empty budget}">
                    Available to spend this month
                </c:when>
                <c:otherwise>
                    Set a budget to calculate balance
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <div class="stat-card">
        <div class="stat-header">
            <span class="stat-label">Budget Utilization</span>
            <div class="stat-icon ${budgetStatus == 'DANGER' ? 'icon-red' : (budgetStatus == 'WARNING' ? 'icon-orange' : 'icon-green')}">📊</div>
        </div>
        <div class="stat-value">
            <c:choose>
                <c:when test="${not empty budget}">
                    <fmt:formatNumber value="${rawBudgetPercentage}" pattern="0.0" />%
                </c:when>
                <c:otherwise>
                    <span style="color: var(--text-muted); font-size: 1.2rem;">N/A</span>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="stat-footer">
            <c:choose>
                <c:when test="${budgetStatus == 'DANGER'}">
                    <span style="color: var(--danger); font-weight: 600;">Threshold Exceeded!</span>
                </c:when>
                <c:when test="${budgetStatus == 'WARNING'}">
                    <span style="color: var(--warning); font-weight: 600;">80% Warning Triggered</span>
                </c:when>
                <c:when test="${budgetStatus == 'NORMAL'}">
                    <span style="color: var(--success); font-weight: 600;">Spending Within Limits</span>
                </c:when>
                <c:otherwise>
                    No monthly cap configured
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<!-- 2. Budget Alert & Progress Section -->
<c:choose>
    <c:when test="${budgetStatus == 'DANGER'}">
        <div class="budget-banner status-danger">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem;">
                <strong style="color: var(--danger); font-size: 1rem;">
                    ⚠️ Budget Exceeded! Spending Alert
                </strong>
                <span class="badge" style="background: #fee2e2; color: #b91c1c;">
                    <fmt:formatNumber value="${rawBudgetPercentage}" pattern="0.0" />% Used
                </span>
            </div>
            <p style="font-size: 0.88rem; color: #7f1d1d; margin-bottom: 0.5rem;">
                You have spent <strong>₹<fmt:formatNumber value="${totalExpenses}" pattern="#,##0.00" /></strong>, 
                exceeding your monthly limit of <strong>₹<fmt:formatNumber value="${budget.amount}" pattern="#,##0.00" /></strong> 
                by <strong>₹<fmt:formatNumber value="${remainingBalance * -1}" pattern="#,##0.00" /></strong>. 
                Please review your discretionary expenses.
            </p>
            <div class="budget-progress-container">
                <div class="progress-bar-bg">
                    <div class="progress-bar-fill progress-danger" style="width: 100%;"></div>
                </div>
            </div>
        </div>
    </c:when>

    <c:when test="${budgetStatus == 'WARNING'}">
        <div class="budget-banner status-warning">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem;">
                <strong style="color: #b45309; font-size: 1rem;">
                    ⚠️ Warning: 80% Budget Threshold Reached
                </strong>
                <span class="badge" style="background: #fef3c7; color: #92400e;">
                    <fmt:formatNumber value="${rawBudgetPercentage}" pattern="0.0" />% Used
                </span>
            </div>
            <p style="font-size: 0.88rem; color: #78350f; margin-bottom: 0.5rem;">
                You have reached <strong><fmt:formatNumber value="${rawBudgetPercentage}" pattern="0.0" />%</strong> of your 
                <strong>₹<fmt:formatNumber value="${budget.amount}" pattern="#,##0.00" /></strong> budget. 
                Only <strong>₹<fmt:formatNumber value="${remainingBalance}" pattern="#,##0.00" /></strong> remains for ${friendlySelectedMonth}.
            </p>
            <div class="budget-progress-container">
                <div class="progress-bar-bg">
                    <div class="progress-bar-fill progress-warning" style="width: ${budgetPercentage}%;"></div>
                </div>
            </div>
        </div>
    </c:when>

    <c:when test="${budgetStatus == 'NORMAL'}">
        <div class="budget-banner status-normal">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem;">
                <strong style="color: var(--success); font-size: 1rem;">
                    ✓ Budget On Track
                </strong>
                <span class="badge" style="background: #ecfdf5; color: #065f46;">
                    <fmt:formatNumber value="${rawBudgetPercentage}" pattern="0.0" />% Used
                </span>
            </div>
            <p style="font-size: 0.88rem; color: #064e3b; margin-bottom: 0.5rem;">
                You have utilized <strong>₹<fmt:formatNumber value="${totalExpenses}" pattern="#,##0.00" /></strong> 
                out of <strong>₹<fmt:formatNumber value="${budget.amount}" pattern="#,##0.00" /></strong>. 
                Remaining balance: <strong>₹<fmt:formatNumber value="${remainingBalance}" pattern="#,##0.00" /></strong>.
            </p>
            <div class="budget-progress-container">
                <div class="progress-bar-bg">
                    <div class="progress-bar-fill progress-normal" style="width: ${budgetPercentage}%;"></div>
                </div>
            </div>
        </div>
    </c:when>

    <c:otherwise>
        <div class="budget-banner">
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <div>
                    <strong>No Budget Configured for ${friendlySelectedMonth}</strong>
                    <p style="font-size: 0.85rem; color: var(--text-muted); margin-top: 0.2rem;">
                        Setting a monthly budget enables automatic 80% threshold warnings and expenditure monitoring.
                    </p>
                </div>
                <a href="${pageContext.request.contextPath}/budget?month=${selectedMonth}" class="btn btn-secondary btn-sm">
                    Configure Budget
                </a>
            </div>
        </div>
    </c:otherwise>
</c:choose>

<div style="display: grid; grid-template-columns: 2fr 1fr; gap: 1.5rem; align-items: start;">
    <!-- Left Column: Category Breakdown & Spending History -->
    <div>
        <!-- Category-wise Spending Breakdown -->
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">Category Breakdown (${friendlySelectedMonth})</h2>
                <a href="${pageContext.request.contextPath}/expenses?month=${selectedMonth}" style="font-size: 0.85rem;">
                    View in Expenses &rarr;
                </a>
            </div>
            <div class="card-body" style="padding: 0;">
                <c:choose>
                    <c:when test="${not empty categorySummaries}">
                        <div class="table-responsive">
                            <table class="custom-table">
                                <thead>
                                    <tr>
                                        <th>Category</th>
                                        <th>Total Spent</th>
                                        <th>Share of Total</th>
                                        <th style="min-width: 140px;">Distribution</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="cat" items="${categorySummaries}">
                                        <tr>
                                            <td>
                                                <span class="badge ${cat.categoryBadgeClass}">
                                                    <c:out value="${cat.category}" />
                                                </span>
                                                <span style="font-size: 0.75rem; color: var(--text-muted); margin-left: 0.3rem;">
                                                    (${cat.count} txns)
                                                </span>
                                            </td>
                                            <td style="font-weight: 600;">₹<c:out value="${cat.formattedTotalAmount}" /></td>
                                            <td style="font-size: 0.85rem;"><c:out value="${cat.formattedPercentage}" /></td>
                                            <td>
                                                <div class="progress-bar-bg" style="height: 7px;">
                                                    <div class="progress-bar-fill progress-normal" style="width: ${cat.percentage}%;"></div>
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
                            <div class="empty-icon">📂</div>
                            <h3 class="empty-title">No Expenses Found</h3>
                            <p class="empty-desc">
                                There are no expenses recorded for ${friendlySelectedMonth}. 
                                Add an expense to see category analysis here.
                            </p>
                            <a href="${pageContext.request.contextPath}/expenses?action=new" class="btn btn-primary btn-sm">
                                + Add Expense
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Monthly Spending History -->
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">Monthly Spending History (Past Months)</h2>
            </div>
            <div class="card-body" style="padding: 0;">
                <c:choose>
                    <c:when test="${not empty monthlyHistory}">
                        <div class="table-responsive">
                            <table class="custom-table">
                                <thead>
                                    <tr>
                                        <th>Month</th>
                                        <th>Total Expenditure</th>
                                        <th>Transactions</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="mh" items="${monthlyHistory}">
                                        <tr>
                                            <td style="font-weight: 500;">
                                                <c:out value="${mh.monthDisplayName}" />
                                                <c:if test="${mh.monthYear == selectedMonth}">
                                                    <span class="badge" style="background:#e0f2fe; color:#0369a1; font-size: 0.7rem; margin-left: 0.3rem;">Current</span>
                                                </c:if>
                                            </td>
                                            <td style="font-weight: 600;">₹<c:out value="${mh.formattedTotalAmount}" /></td>
                                            <td>${mh.expenseCount}</td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/dashboard?month=${mh.monthYear}" class="btn btn-secondary btn-sm">
                                                    Inspect
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <div class="empty-icon">📈</div>
                            <h3 class="empty-title">No History Available</h3>
                            <p class="empty-desc">Past monthly expenditure history will appear here as you log transactions over time.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Right Column: Recent Transactions & Quick Actions -->
    <div>
        <div class="card">
            <div class="card-header">
                <h2 class="card-title">Quick Actions</h2>
            </div>
            <div class="card-body" style="display: flex; flex-direction: column; gap: 0.75rem;">
                <a href="${pageContext.request.contextPath}/expenses?action=new" class="btn btn-primary">
                    + Record New Expense
                </a>
                <a href="${pageContext.request.contextPath}/budget?month=${selectedMonth}" class="btn btn-secondary">
                    ⚙️ Set/Update Monthly Budget
                </a>
                <a href="${pageContext.request.contextPath}/expenses" class="btn btn-secondary">
                    📋 View All Expenses List
                </a>
            </div>
        </div>

        <div class="card">
            <div class="card-header">
                <h2 class="card-title">Recent Transactions</h2>
                <a href="${pageContext.request.contextPath}/expenses" style="font-size: 0.85rem;">View All &rarr;</a>
            </div>
            <div class="card-body" style="padding: 0;">
                <c:choose>
                    <c:when test="${not empty recentExpenses}">
                        <div class="table-responsive">
                            <table class="custom-table">
                                <thead>
                                    <tr>
                                        <th>Date &amp; Details</th>
                                        <th style="text-align: right;">Amount</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="item" items="${recentExpenses}">
                                        <tr>
                                            <td>
                                                <div style="font-weight: 600; font-size: 0.88rem;">
                                                    <c:out value="${not empty item.description ? item.description : item.category}" />
                                                </div>
                                                <div style="font-size: 0.75rem; color: var(--text-muted); margin-top: 0.15rem;">
                                                    <span class="badge ${item.categoryBadgeClass}">${item.category}</span>
                                                    &bull; ${item.formattedDate} &bull; ${item.paymentMethod}
                                                </div>
                                            </td>
                                            <td style="text-align: right; font-weight: 700; color: var(--dark);">
                                                ₹${item.formattedAmount}
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state" style="padding: 2rem 1rem;">
                            <div class="empty-icon" style="font-size: 2rem;">💳</div>
                            <h3 class="empty-title" style="font-size: 0.95rem;">No Recent Expenses</h3>
                            <p class="empty-desc" style="font-size: 0.8rem;">Click below to log your first transaction.</p>
                            <a href="${pageContext.request.contextPath}/expenses?action=new" class="btn btn-primary btn-sm">
                                Add Expense
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
