<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="${pageTitle != null ? pageTitle : 'Manage Expense'}" />
    <jsp:param name="activePage" value="expenses" />
</jsp:include>

<div class="page-header">
    <div>
        <h1 class="page-title"><c:out value="${pageTitle}" /></h1>
        <p class="page-subtitle">Enter expense details to update your records and monthly tracking</p>
    </div>

    <a href="${pageContext.request.contextPath}/expenses" class="btn btn-secondary">
        &larr; Back to Expenses
    </a>
</div>

<div style="max-width: 650px; margin: 0 auto;">
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">Expense Details</h2>
        </div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/expenses" method="POST">
                <input type="hidden" name="action" value="${formAction}">
                <c:if test="${not empty expense.id and expense.id > 0}">
                    <input type="hidden" name="id" value="${expense.id}">
                </c:if>

                <div class="form-grid-2">
                    <!-- Amount Input -->
                    <div class="form-group">
                        <label for="amount" class="form-label">Amount (₹) *</label>
                        <input type="number" 
                               id="amount" 
                               name="amount" 
                               class="form-control" 
                               placeholder="e.g. 450.00" 
                               step="0.01" 
                               min="0.01" 
                               value="${expense.amount}" 
                               required 
                               autofocus>
                        <span class="form-hint">Must be greater than 0</span>
                    </div>

                    <!-- Date Picker -->
                    <div class="form-group">
                        <label for="expenseDate" class="form-label">Expense Date *</label>
                        <input type="date" 
                               id="expenseDate" 
                               name="expenseDate" 
                               class="form-control" 
                               value="${not empty expense.expenseDate ? expense.expenseDate : defaultDate}" 
                               required>
                    </div>
                </div>

                <div class="form-grid-2">
                    <!-- Category Dropdown -->
                    <div class="form-group">
                        <label for="category" class="form-label">Category *</label>
                        <select id="category" name="category" class="form-control" required>
                            <option value="">-- Select Category --</option>
                            <c:forEach var="cat" items="${categories}">
                                <option value="${cat}" ${expense.category == cat ? 'selected' : ''}>
                                    ${cat}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <!-- Payment Method Dropdown -->
                    <div class="form-group">
                        <label for="paymentMethod" class="form-label">Payment Method *</label>
                        <select id="paymentMethod" name="paymentMethod" class="form-control" required>
                            <option value="">-- Select Method --</option>
                            <c:forEach var="pm" items="${paymentMethods}">
                                <option value="${pm}" ${expense.paymentMethod == pm ? 'selected' : ''}>
                                    ${pm}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <!-- Description / Notes -->
                <div class="form-group">
                    <label for="description" class="form-label">Description / Merchant (Optional)</label>
                    <input type="text" 
                           id="description" 
                           name="description" 
                           class="form-control" 
                           placeholder="e.g. Team dinner at Italian Bistro, Uber to airport" 
                           maxlength="255" 
                           value="<c:out value='${expense.description}' />">
                    <span class="form-hint">Maximum 255 characters</span>
                </div>

                <!-- Submit / Cancel Controls -->
                <div style="display: flex; gap: 0.75rem; margin-top: 1.5rem;">
                    <button type="submit" class="btn btn-primary" style="flex: 1;">
                        ${formAction == 'update' ? 'Save Changes' : 'Record Expense'}
                    </button>
                    <a href="${pageContext.request.contextPath}/expenses" class="btn btn-secondary">
                        Cancel
                    </a>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
