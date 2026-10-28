-- ====================================================================
-- Smart Expense Tracker - Database Schema
-- Advanced Internet Programming Mini Project
-- Database: MySQL 5.7+ / 8.0+
-- ====================================================================

-- 1. Create Database if not exists
CREATE DATABASE IF NOT EXISTS `expense_tracker_db`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `expense_tracker_db`;

-- 2. Drop existing tables in reverse order of foreign key dependencies
DROP TABLE IF EXISTS `expenses`;
DROP TABLE IF EXISTS `budgets`;
DROP TABLE IF EXISTS `users`;

-- 3. Users Table
-- Stores user registration details with salted SHA-256 password hashes
CREATE TABLE `users` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `full_name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `password_hash` VARCHAR(128) NOT NULL,
    `salt` VARCHAR(64) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `chk_email_format` CHECK (`email` LIKE '%_@__%.__%')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Budgets Table
-- Stores monthly budget target per user. Each user can have only one budget record per month (YYYY-MM).
CREATE TABLE `budgets` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `month_year` VARCHAR(7) NOT NULL, -- Format: 'YYYY-MM' (e.g. '2026-10')
    `amount` DECIMAL(10, 2) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_budgets_user` FOREIGN KEY (`user_id`) 
        REFERENCES `users`(`id`) ON DELETE CASCADE,
    CONSTRAINT `uq_user_month_budget` UNIQUE (`user_id`, `month_year`),
    CONSTRAINT `chk_budget_amount` CHECK (`amount` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Expenses Table
-- Stores each expense item linked to a specific user
CREATE TABLE `expenses` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `amount` DECIMAL(10, 2) NOT NULL,
    `category` VARCHAR(50) NOT NULL, -- e.g. Food, Travel, Bills, Shopping, Education, Other
    `expense_date` DATE NOT NULL,
    `payment_method` VARCHAR(50) NOT NULL, -- Cash, Credit Card, Debit Card, UPI, Net Banking
    `description` VARCHAR(255) NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_expenses_user` FOREIGN KEY (`user_id`) 
        REFERENCES `users`(`id`) ON DELETE CASCADE,
    CONSTRAINT `chk_expense_amount` CHECK (`amount` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Indexes for optimized reporting and filtering
CREATE INDEX `idx_expenses_user_date` ON `expenses` (`user_id`, `expense_date`);
CREATE INDEX `idx_expenses_user_category` ON `expenses` (`user_id`, `category`);
CREATE INDEX `idx_budgets_user_month` ON `budgets` (`user_id`, `month_year`);

