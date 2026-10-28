-- ====================================================================
-- Smart Expense Tracker - Sample Seed Data
-- Advanced Internet Programming Mini Project
-- ====================================================================

USE `expense_tracker_db`;

-- Clear existing data if re-seeding
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `expenses`;
TRUNCATE TABLE `budgets`;
TRUNCATE TABLE `users`;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Insert Sample Users
-- Password for john@example.com is: Password123
-- Password for demo@tracker.com is: Demo@123
INSERT INTO `users` (`id`, `full_name`, `email`, `password_hash`, `salt`, `created_at`) VALUES
(1, 'John Doe', 'john@example.com', 'f3fe1d8f50a88be652fc1d48142f1855579f4b0c4308a90c9f3b19c1ffbe0a5b', 'a1b2c3d4e5f60718', NOW()),
(2, 'Alice Smith', 'demo@tracker.com', '3d1c0bf3db4d861f628e6f8680e69c384c2341ac291341d7fd2a30b3176c0788', '9f8e7d6c5b4a3210', NOW());

-- 2. Insert Budgets for User 1 (John Doe)
-- Uses dynamic current month YYYY-MM so the demo is always up-to-date!
INSERT INTO `budgets` (`user_id`, `month_year`, `amount`, `created_at`) VALUES
(1, DATE_FORMAT(CURRENT_DATE, '%Y-%m'), 25000.00, NOW()),
(1, DATE_FORMAT(DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH), '%Y-%m'), 22000.00, DATE_SUB(NOW(), INTERVAL 1 MONTH)),
(2, DATE_FORMAT(CURRENT_DATE, '%Y-%m'), 18000.00, NOW());

-- 3. Insert Diverse Sample Expenses for User 1 (John Doe)
-- Expenses for the current month across multiple categories and payment methods
INSERT INTO `expenses` (`user_id`, `amount`, `category`, `expense_date`, `payment_method`, `description`, `created_at`) VALUES
-- Current Month Expenses (Total ~ 20,850 -> ~83.4% of 25,000 budget, triggers the 80% warning!)
(1, 4500.00, 'Food', CURRENT_DATE, 'UPI', 'Monthly groceries at Supermarket', NOW()),
(1, 1200.00, 'Food', DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), 'Debit Card', 'Dinner with team at Bistro', NOW()),
(1, 3200.00, 'Bills', DATE_SUB(CURRENT_DATE, INTERVAL 3 DAY), 'Net Banking', 'Electricity & Water utilities', NOW()),
(1, 999.00,  'Bills', DATE_SUB(CURRENT_DATE, INTERVAL 4 DAY), 'Credit Card', 'High-speed broadband bill', NOW()),
(1, 2400.00, 'Travel', DATE_SUB(CURRENT_DATE, INTERVAL 6 DAY), 'UPI', 'Metro card recharge & Cab rides', NOW()),
(1, 3500.00, 'Shopping', DATE_SUB(CURRENT_DATE, INTERVAL 8 DAY), 'Credit Card', 'Formal shirts & winter jacket', NOW()),
(1, 4200.00, 'Education', DATE_SUB(CURRENT_DATE, INTERVAL 11 DAY), 'Net Banking', 'Online Cloud Certification course', NOW()),
(1, 850.00,  'Other', DATE_SUB(CURRENT_DATE, INTERVAL 14 DAY), 'Cash', 'Pharmacy medications & first aid', NOW()),

-- Previous Month Expenses for Monthly History Comparison
(1, 5200.00, 'Food', DATE_SUB(CURRENT_DATE, INTERVAL 32 DAY), 'UPI', 'Previous month groceries', DATE_SUB(NOW(), INTERVAL 32 DAY)),
(1, 3100.00, 'Bills', DATE_SUB(CURRENT_DATE, INTERVAL 35 DAY), 'Net Banking', 'Electricity bill', DATE_SUB(NOW(), INTERVAL 35 DAY)),
(1, 4000.00, 'Shopping', DATE_SUB(CURRENT_DATE, INTERVAL 40 DAY), 'Credit Card', 'Electronics festival purchase', DATE_SUB(NOW(), INTERVAL 40 DAY)),
(1, 2100.00, 'Travel', DATE_SUB(CURRENT_DATE, INTERVAL 42 DAY), 'Cash', 'Train tickets for hometown visit', DATE_SUB(NOW(), INTERVAL 42 DAY));

-- 4. Sample Expenses for User 2 (Alice Smith)
INSERT INTO `expenses` (`user_id`, `amount`, `category`, `expense_date`, `payment_method`, `description`, `created_at`) VALUES
(2, 2800.00, 'Food', CURRENT_DATE, 'UPI', 'Organic market grocery', NOW()),
(2, 1500.00, 'Travel', DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), 'Debit Card', 'Fuel refill for scooter', NOW()),
(2, 3000.00, 'Bills', DATE_SUB(CURRENT_DATE, INTERVAL 5 DAY), 'Net Banking', 'Apartment maintenance fee', NOW());

