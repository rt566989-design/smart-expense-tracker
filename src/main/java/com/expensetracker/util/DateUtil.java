package com.expensetracker.util;

import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * Helper utility for parsing, formatting, and validating dates and month-year strings.
 */
public class DateUtil {

    private static final DateTimeFormatter MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter FRIENDLY_MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");

    /**
     * Gets the current month in "YYYY-MM" format.
     */
    public static String getCurrentMonthYear() {
        return YearMonth.now().format(MONTH_YEAR_FORMATTER);
    }

    /**
     * Formats a YearMonth or string "YYYY-MM" into "MMMM yyyy" (e.g. "October 2026").
     */
    public static String formatMonthYear(String monthYear) {
        if (monthYear == null || !isValidMonthYear(monthYear)) {
            return monthYear != null ? monthYear : "";
        }
        try {
            YearMonth ym = YearMonth.parse(monthYear, MONTH_YEAR_FORMATTER);
            return ym.format(FRIENDLY_MONTH_FORMATTER);
        } catch (Exception e) {
            return monthYear;
        }
    }

    /**
     * Checks if a string is a valid "YYYY-MM" format.
     */
    public static boolean isValidMonthYear(String monthYear) {
        if (monthYear == null || !monthYear.matches("^\\d{4}-(0[1-9]|1[0-2])$")) {
            return false;
        }
        try {
            YearMonth.parse(monthYear, MONTH_YEAR_FORMATTER);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Safely parses a "YYYY-MM-DD" string into java.sql.Date.
     * If invalid, returns today's date.
     */
    public static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return Date.valueOf(LocalDate.now());
        }
        try {
            return Date.valueOf(dateStr.trim());
        } catch (IllegalArgumentException e) {
            return Date.valueOf(LocalDate.now());
        }
    }

    /**
     * Returns today's SQL date.
     */
    public static Date getTodayDate() {
        return Date.valueOf(LocalDate.now());
    }
}

