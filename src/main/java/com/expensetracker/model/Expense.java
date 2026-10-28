package com.expensetracker.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Expense Entity Model representing a recorded expense transaction.
 */
public class Expense implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int userId;
    private BigDecimal amount;
    private String category;
    private Date expenseDate;
    private String paymentMethod;
    private String description;
    private Timestamp createdAt;

    public Expense() {
    }

    public Expense(int userId, BigDecimal amount, String category, Date expenseDate, String paymentMethod, String description) {
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.expenseDate = expenseDate;
        this.paymentMethod = paymentMethod;
        this.description = description;
    }

    public Expense(int id, int userId, BigDecimal amount, String category, Date expenseDate, String paymentMethod, String description, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.expenseDate = expenseDate;
        this.paymentMethod = paymentMethod;
        this.description = description;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(Date expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns amount formatted as currency (e.g. 1,250.00).
     */
    public String getFormattedAmount() {
        if (amount == null) return "0.00";
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(amount);
    }

    /**
     * Returns human-readable date (e.g. 29 Oct 2026).
     */
    public String getFormattedDate() {
        if (expenseDate == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        return sdf.format(expenseDate);
    }

    /**
     * Helper to return CSS badge class based on category.
     */
    public String getCategoryBadgeClass() {
        if (category == null) return "badge-secondary";
        switch (category.toLowerCase()) {
            case "food":
                return "badge-food";
            case "travel":
                return "badge-travel";
            case "bills":
                return "badge-bills";
            case "shopping":
                return "badge-shopping";
            case "education":
                return "badge-education";
            default:
                return "badge-other";
        }
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", userId=" + userId +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", expenseDate=" + expenseDate +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

