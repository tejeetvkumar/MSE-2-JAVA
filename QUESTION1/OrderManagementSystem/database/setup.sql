-- ============================================================
-- MSE-2 Q1: Order Management System - SQL Setup Script
-- Database: ecommerce_db
-- Table: Orders
-- ============================================================

-- Create the database (run this first in MySQL)
CREATE DATABASE IF NOT EXISTS ecommerce_db;

-- Use the database
USE ecommerce_db;

-- Create the Orders table with exact attributes and data types
CREATE TABLE IF NOT EXISTS Orders (
    order_id      INT PRIMARY KEY,
    customer_name VARCHAR(100),
    product_name  VARCHAR(100),
    quantity      INT,
    unit_price    DOUBLE,
    status        VARCHAR(30)
);

-- Insert sample records for testing
INSERT INTO Orders (order_id, customer_name, product_name, quantity, unit_price, status) VALUES
(101, 'Alice Smith', 'Laptop',       1, 55000.00, 'Pending'),
(102, 'Bob Johnson', 'Smartphone',  5,  25000.00, 'Shipped'),
(103, 'Carol Davis', 'Headphones',  3,   2000.00, 'Delivered'),
(104, 'David Wilson','Tablet',       4,  15000.00, 'Cancelled'),
(105, 'Eve Brown',   'Smartwatch',  2,   8000.00, 'Pending');

-- Verify the inserted records
SELECT * FROM Orders;

-- ============================================================
-- End of setup.sql
-- ============================================================
