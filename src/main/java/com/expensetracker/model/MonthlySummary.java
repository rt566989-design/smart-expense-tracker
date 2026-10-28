package com.expensetracker.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * Data Transfer Object representing month-wise aggregated expense totals.
 */
public class MonthlySummary implements Serializable {
    private static final long serialVersionUID = 1L;

    private String monthYear; // "YYYY-MM"
    private BigDecimal totalAmount;
    private int expenseCount;

    public MonthlySummary() {
    }

    public MonthlySummary(String monthYear, BigDecimal totalAmount, int expenseCount) {
        this.monthYear = monthYear;
        this.totalAmount = totalAmount;
        this.expenseCount = expenseCount;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getExpenseCount() {
        return expenseCount;
    }

    public void setExpenseCount(int expenseCount) {
        this.expenseCount = expenseCount;
    }

    public String getFormattedTotalAmount() {
        if (totalAmount == null) return "0.00";
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(totalAmount);
    }

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
}

