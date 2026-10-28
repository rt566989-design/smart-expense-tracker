package com.expensetracker.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Data Transfer Object representing category-wise aggregated expense totals.
 */
public class CategorySummary implements Serializable {
    private static final long serialVersionUID = 1L;

    private String category;
    private BigDecimal totalAmount;
    private double percentage;
    private int count;

    public CategorySummary() {
    }

    public CategorySummary(String category, BigDecimal totalAmount, double percentage, int count) {
        this.category = category;
        this.totalAmount = totalAmount;
        this.percentage = percentage;
        this.count = count;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getFormattedTotalAmount() {
        if (totalAmount == null) return "0.00";
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(totalAmount);
    }

    public String getFormattedPercentage() {
        DecimalFormat df = new DecimalFormat("0.0");
        return df.format(percentage) + "%";
    }

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
}

