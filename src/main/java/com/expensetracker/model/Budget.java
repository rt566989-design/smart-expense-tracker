package com.expensetracker.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * Budget Entity Model representing a user's monthly spending limit.
 */
public class Budget implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int userId;
    private String monthYear; // e.g. "2026-10"
    private BigDecimal amount;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Budget() {
    }

    public Budget(int userId, String monthYear, BigDecimal amount) {
        this.userId = userId;
        this.monthYear = monthYear;
        this.amount = amount;
    }

    public Budget(int id, int userId, String monthYear, BigDecimal amount, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.userId = userId;
        this.monthYear = monthYear;
        this.amount = amount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Formats amount as currency string (e.g. 25,000.00).
     */
    public String getFormattedAmount() {
        if (amount == null) return "0.00";
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(amount);
    }

    /**
     * Converts "YYYY-MM" (e.g. "2026-10") to friendly name (e.g. "October 2026").
     */
    public String getMonthDisplayName() {
        if (monthYear == null || !monthYear.matches("\\d{4}-\\d{2}")) {
            return monthYear != null ? monthYear : "";
        }
        try {
            YearMonth ym = YearMonth.parse(monthYear);
            return ym.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        } catch (Exception e) {
            return monthYear;
        }
    }

    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", userId=" + userId +
                ", monthYear='" + monthYear + '\'' +
                ", amount=" + amount +
                '}';
    }
}

