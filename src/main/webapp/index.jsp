<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Smart Expense Tracker" />
</jsp:include>

<div style="text-align: center; max-width: 820px; margin: 3rem auto 4rem; padding: 0 1rem;">
    <div style="display: inline-block; padding: 0.35rem 1rem; background-color: var(--primary-light); color: var(--primary); border-radius: 999px; font-size: 0.85rem; font-weight: 600; margin-bottom: 1rem;">
        Advanced Internet Programming Mini Project
    </div>
    
    <h1 style="font-size: 2.75rem; font-weight: 800; color: var(--dark); line-height: 1.2; margin-bottom: 1.25rem;">
        Take Control of Your Spending with <span style="color: var(--primary);">Smart Expense Tracker</span>
    </h1>
    
    <p style="font-size: 1.15rem; color: var(--secondary); max-width: 650px; margin: 0 auto 2rem; line-height: 1.6;">
        Track personal expenses, manage monthly budgets, and monitor real-time spending warnings.
        <strong>No passwords or login required!</strong>
    </p>

    <div style="display: flex; gap: 1rem; justify-content: center; flex-wrap: wrap;">
        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary" style="padding: 0.85rem 2rem; font-size: 1.1rem; font-weight: 600;">
            🚀 Open Dashboard Directly
        </a>
        <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary" style="padding: 0.85rem 1.5rem; font-size: 1rem;">
            Switch Profile
        </a>
    </div>

    <!-- Quick Info Box -->
    <div style="margin-top: 2.5rem; display: inline-block; text-align: left; background: #ffffff; border: 1px solid var(--border); padding: 1.25rem 1.75rem; border-radius: var(--radius); box-shadow: var(--shadow-sm);">
        <div style="font-weight: 700; color: var(--dark); margin-bottom: 0.3rem; font-size: 0.95rem;">
            ⚡ Direct Instant Access Enabled:
        </div>
        <div style="font-size: 0.85rem; color: var(--secondary);">
            Default user <strong>John Doe</strong> is automatically loaded with October 2026 expenses &amp; 80% warning test data.
        </div>
    </div>
</div>

<!-- Features Overview Grid -->
<div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 1.5rem; margin-bottom: 3.5rem;">
    <div class="card" style="padding: 1.5rem;">
        <div class="stat-icon icon-blue" style="width: 44px; height: 44px; font-size: 1.3rem; margin-bottom: 1rem;">
            💳
        </div>
        <h3 style="font-size: 1.15rem; font-weight: 700; margin-bottom: 0.5rem; color: var(--dark);">
            Expense Tracking
        </h3>
        <p style="font-size: 0.88rem; color: var(--text-muted); line-height: 1.5;">
            Log transactions with amount, category (Food, Travel, Bills, etc.), date, payment method, and description. Full CRUD management.
        </p>
    </div>

    <div class="card" style="padding: 1.5rem;">
        <div class="stat-icon icon-orange" style="width: 44px; height: 44px; font-size: 1.3rem; margin-bottom: 1rem;">
            🎯
        </div>
        <h3 style="font-size: 1.15rem; font-weight: 700; margin-bottom: 0.5rem; color: var(--dark);">
            Monthly Budgets
        </h3>
        <p style="font-size: 0.88rem; color: var(--text-muted); line-height: 1.5;">
            Establish unique monthly spending targets. Visual alerts trigger automatically when expenditures reach 80% or exceed 100%.
        </p>
    </div>

    <div class="card" style="padding: 1.5rem;">
        <div class="stat-icon icon-green" style="width: 44px; height: 44px; font-size: 1.3rem; margin-bottom: 1rem;">
            📊
        </div>
        <h3 style="font-size: 1.15rem; font-weight: 700; margin-bottom: 0.5rem; color: var(--dark);">
            Visual Analytics
        </h3>
        <p style="font-size: 0.88rem; color: var(--text-muted); line-height: 1.5;">
            Category-wise percentage breakdown with visual progress meters, transaction histories, and monthly comparative summaries.
        </p>
    </div>

    <div class="card" style="padding: 1.5rem;">
        <div class="stat-icon icon-red" style="width: 44px; height: 44px; font-size: 1.3rem; margin-bottom: 1rem;">
            ⚡
        </div>
        <h3 style="font-size: 1.15rem; font-weight: 700; margin-bottom: 0.5rem; color: var(--dark);">
            Instant 1-Click Access
        </h3>
        <p style="font-size: 0.88rem; color: var(--text-muted); line-height: 1.5;">
            No password friction. Effortlessly switch profiles or add custom users with zero login hassle.
        </p>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
