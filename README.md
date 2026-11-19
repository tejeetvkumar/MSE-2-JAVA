# Order Management System

**CS336B – Object Oriented Programming with Java**  
**MSE-2 | Question 1 | CO4 | 10 Marks**

A menu-driven Java JDBC application to manage customer orders using MySQL.

---

## Project Structure

```
OrderManagementSystem/
    src/
        OrderManagement.java
    database/
        setup.sql
    pom.xml
    README.md
```

---

## 1. Database Setup

### Step 1: Install MySQL
Install MySQL Server and MySQL Connector/J (JDBC driver).

### Step 2: Create the database and table
Open MySQL command line or MySQL Workbench and run the SQL script:

```sql
SOURCE database/setup.sql;
```

Or manually:

```sql
CREATE DATABASE IF NOT EXISTS ecommerce_db;
USE ecommerce_db;

CREATE TABLE Orders (
    order_id      INT PRIMARY KEY,
    customer_name VARCHAR(100),
    product_name  VARCHAR(100),
    quantity      INT,
    unit_price    DOUBLE,
    status        VARCHAR(30)
);
```

The `setup.sql` file already inserts 5 sample records for testing.

---

## 2. Configure MySQL Credentials

Open `src/OrderManagement.java` and update the JDBC connection constants at the top of the file:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/ecommerce_db";
private static final String DB_USER = "root";      // your MySQL username
private static final String DB_PASSWORD = "password"; // your MySQL password
```

---

## 3. How to Run in VS Code

### Option A: Using Maven

1. Open the `OrderManagementSystem` folder in VS Code.
2. Install the "Extension Pack for Java" and "Maven for Java" extensions.
3. Open a terminal in VS Code.
4. Compile and run:

```powershell
mvn compile
mvn exec:java -Dexec.mainClass="OrderManagement"
```

Or package and run the JAR:

```powershell
mvn package
java -cp target/ordermanagementsystem-1.0-SNAPSHOT.jar:. OrderManagement
```

*(If you don't have Maven, add `mysql-connector-java-8.0.33.jar` to your classpath manually.)*

### Option B: Using javac and java directly

1. Download `mysql-connector-java-8.0.33.jar` from MySQL.
2. Compile:

```powershell
javac -cp "mysql-connector-java-8.0.33.jar" src/OrderManagement.java
```

3. Run:

```powershell
java -cp ".;mysql-connector-java-8.0.33.jar" OrderManagement
```

---

## 4. Menu Options

```
========== ORDER MANAGEMENT SYSTEM ==========
1. Insert Three Orders
2. Search Orders by Status
3. Increase Order Quantity
4. Update Order Status
5. Calculate Total Order Value and Delete Cancelled Order
0. Exit
=============================================
```

| Option | Functionality | Exam Part |
|--------|---------------|-----------|
| 1 | Insert three orders using PreparedStatement; handles duplicate IDs | (a) |
| 2 | Search by status + quantity > 2, sorted by unit_price DESC | (b) |
| 3 | Increase quantity = quantity + ?; reports if order missing | (c) |
| 4 | Update status (Pending/Shipped/Delivered); shows updated details | (d) |
| 5 | Total = SUM(quantity * unit_price); delete only if Cancelled | (e) |
| 0 | Exit the application | — |

---

## 5. Source Code Explanation

### Class: `OrderManagement`

The `main` method shows the menu in a `do-while` loop and dispatches to the appropriate method based on the user's choice.

### `displayMenu()`
Prints the numbered menu to the console.

### `readInt()`, `readLine()`, `readIntInput()`, `readNonEmptyInput()`, `readPositiveIntInput()`, `readPositiveDoubleInput()`
Helper methods that read user input with validation (rejecting non-numeric or empty input).

---

### (a) `insertThreeOrders()`
- Prompts the user for 3 orders (order_id, customer_name, product_name, quantity, unit_price, status).
- Uses `PreparedStatement` with `?` placeholders.
- Calls `executeUpdate()` for each insert.
- Catches `SQLIntegrityConstraintViolationException` to handle duplicate `order_id` (primary key violation) gracefully.
- Uses try-with-resources so `Connection` and `PreparedStatement` close automatically.

### (b) `searchOrdersByStatus()`
- Asks for a status.
- Query: `SELECT ... FROM Orders WHERE status = ? AND quantity > 2 ORDER BY unit_price DESC`
- Uses `executeQuery()` and iterates the `ResultSet`.
- Prints a formatted table. If no rows match, prints "No orders found...".

### (c) `increaseOrderQuantity()`
- Asks for an order ID and an additional quantity.
- First checks existence via `SELECT order_id FROM Orders WHERE order_id = ?`.
- If missing, prints "Order ID ... does not exist." and returns.
- Otherwise runs `UPDATE Orders SET quantity = quantity + ? WHERE order_id = ?` using `executeUpdate()`.
- Re-reads and displays the updated quantity.

### (d) `updateOrderStatus()`
- Asks for an order ID and a new status.
- Validates the status is Pending, Shipped, or Delivered (case-insensitive) via `isValidStatus()`.
- Rejects invalid statuses with a message.
- Checks the order exists; if not, reports it.
- Runs `UPDATE Orders SET status = ? WHERE order_id = ?`.
- Calls `displayOrderDetails()` to show the updated record.

### `isValidStatus()` / `normalizeStatus()` / `displayOrderDetails()`
- `isValidStatus()`: case-insensitive check against the three allowed statuses.
- `normalizeStatus()`: converts to proper case (Pending/Shipped/Delivered).
- `displayOrderDetails()`: SELECTs and prints one order's full details.

### (e) `calculateTotalAndDeleteCancelled()`
- **Total value:** `SELECT SUM(quantity * unit_price) AS total_value FROM Orders` via `Statement.executeQuery()`. Prints the result.
- **Delete:** Asks for an order ID.
  - If the order does not exist → "Order ID ... does not exist. Cannot delete."
  - If the order exists but status is not Cancelled → "Only Cancelled orders can be deleted. Deletion aborted."
  - If status is Cancelled → `DELETE FROM Orders WHERE order_id = ? AND status = 'Cancelled'` via `executeUpdate()`.

---

## 6. JDBC Classes Used

| Class | Purpose |
|-------|---------|
| `DriverManager` | Establishes the connection to MySQL via `getConnection(url, user, password)`. |
| `Connection` | Represents the database session; used to create statements and manage transactions. |
| `PreparedStatement` | Pre-compiles parameterized SQL with `?` placeholders; prevents SQL injection; used for INSERT, UPDATE, DELETE, and SELECT with parameters. |
| `Statement` | Used for the simple `SUM(...)` query with no parameters (`executeQuery`). |
| `ResultSet` | Holds the rows returned by a SELECT query; navigated with `rs.next()`. |
| `SQLException` | Caught for all database errors. |
| `SQLIntegrityConstraintViolationException` | Specific subclass caught to detect duplicate primary keys. |
| `try-with-resources` | Automatically closes `Connection`, `PreparedStatement`, `Statement`, and `ResultSet`. |

---

## 7. SQL Query Explanations

| Query | Purpose |
|-------|---------|
| `INSERT INTO Orders (...) VALUES (?, ?, ?, ?, ?, ?)` | Insert a new order record (part a). |
| `SELECT ... FROM Orders WHERE status = ? AND quantity > 2 ORDER BY unit_price DESC` | Search orders by status with quantity filter, descending price (part b). |
| `SELECT order_id FROM Orders WHERE order_id = ?` | Check whether an order exists (parts c, d, e). |
| `UPDATE Orders SET quantity = quantity + ? WHERE order_id = ?` | Increment quantity by a given amount (part c). |
| `UPDATE Orders SET status = ? WHERE order_id = ?` | Change an order's status (part d). |
| `SELECT SUM(quantity * unit_price) AS total_value FROM Orders` | Compute total order value (part e). |
| `DELETE FROM Orders WHERE order_id = ? AND status = 'Cancelled'` | Delete only Cancelled orders (part e). |

---

## 8. Expected Terminal Output

### Option 1 – Insert Three Orders
```
--- Insert Three Orders ---

Enter details for order 1:
  Order ID: 101
  Customer Name: Alice Smith
  Product Name: Laptop
  Quantity: 1
  Unit Price: 55000.00
  Status: Pending
  Order 101 inserted successfully.

Enter details for order 2:
  ...
(Order 102 inserted successfully.)

Enter details for order 3:
  ...
(Order 103 inserted successfully.)

Insert operation completed.
```

Duplicate ID example:
```
  Order ID: 101
  Order ID 101 already exists (duplicate). Skipped.
```

### Option 2 – Search Orders by Status
```
--- Search Orders by Status ---
Enter order status to search: Shipped

Orders with status 'Shipped' and quantity > 2 (sorted by unit price descending):
--------------------------------------------------------------------------------
Order ID   Customer Name     Product Name        Quantity   Unit Price   Status  
--------------------------------------------------------------------------------
102        Bob Johnson       Smartphone          5          25000.00     Shipped 
--------------------------------------------------------------------------------
```

No match example:
```
No orders found with status 'Delivered' and quantity > 2.
```

### Option 3 – Increase Order Quantity
```
--- Increase Order Quantity ---
Enter Order ID: 102
Enter additional quantity to add: 3
Quantity of Order ID 102 increased by 3 successfully.
Updated quantity: 8
```

Nonexistent:
```
Enter Order ID: 999
Order ID 999 does not exist.
```

### Option 4 – Update Order Status
```
--- Update Order Status ---
Enter Order ID: 101
Enter new status (Pending/Shipped/Delivered): shipped
Status of Order ID 101 updated to 'Shipped' successfully.

Updated Order Details:
----------------------------------------
Order ID     : 101
Customer     : Alice Smith
Product      : Laptop
Quantity     : 1
Unit Price   : 55000.00
Status       : Shipped
----------------------------------------
```

Invalid status:
```
Enter new status (Pending/Shipped/Delivered): Processing
Invalid status 'Processing'. Valid statuses are: Pending, Shipped, Delivered.
```

Nonexistent:
```
Enter Order ID: 999
Order ID 999 does not exist.
```

### Option 5 – Calculate Total and Delete Cancelled Order
```
--- Calculate Total Order Value and Delete Cancelled Order ---
Total order value (SUM of quantity * unit_price): 180000.00

Enter Order ID to delete: 104
Order ID 104 (Cancelled) deleted successfully.
```

Non-Cancelled:
```
Enter Order ID to delete: 101
Order ID 101 has status 'Pending'. Only Cancelled orders can be deleted. Deletion aborted.
```

Nonexistent:
```
Enter Order ID to delete: 999
Order ID 999 does not exist. Cannot delete.
```

---

## 9. Common Errors and Solutions

| Error | Cause | Solution |
|-------|-------|----------|
| `CommunicationsException: Connection refused` | MySQL not running or wrong port | Start MySQL; verify `localhost:3306` and credentials. |
| `Access denied for user` | Wrong username/password | Update `DB_USER` and `DB_PASSWORD` in the Java file. |
| `No suitable driver found` | MySQL Connector/J JAR missing from classpath | Add `mysql-connector-java-8.0.33.jar` to classpath or use Maven. |
| `Unknown database 'ecommerce_db'` | Database not created | Run `database/setup.sql` first to create `ecommerce_db`. |
| `Table 'ecommerce_db.Orders' doesn't exist` | Table not created | Run `setup.sql` which creates the `Orders` table. |
| `Duplicate entry '101' for key 'PRIMARY'` | Inserting an existing order_id | The code catches this and prints a friendly message instead of crashing. |
| `NumberFormatException` | User typed non-numeric input | Helper methods loop until a valid number is entered. |

---

## 10. Viva Questions with Answers

**Q1: What is JDBC?**
A: JDBC (Java Database Connectivity) is an API that allows Java applications to interact with relational databases using SQL. It provides classes like `DriverManager`, `Connection`, `Statement`, `PreparedStatement`, and `ResultSet`.

**Q2: Why use PreparedStatement instead of Statement?**
A: `PreparedStatement` pre-compiles SQL and uses `?` placeholders for parameters. It prevents SQL injection, handles data type conversion automatically, and is more efficient for repeated execution.

**Q3: What does executeUpdate() return?**
A: It returns an `int` — the number of rows affected by INSERT, UPDATE, or DELETE. For DDL statements it returns 0.

**Q4: What does executeQuery() return?**
A: It returns a `ResultSet` containing the rows produced by a SELECT query.

**Q5: What is try-with-resources?**
A: A Java construct (`try (Resource r = ...)`) that automatically closes resources implementing `AutoCloseable` (like `Connection`, `Statement`, `ResultSet`) at the end of the block, even if an exception occurs.

**Q6: How are duplicate primary keys handled?**
A: By catching `SQLIntegrityConstraintViolationException` (a subclass of `SQLException`) thrown when an INSERT violates the PRIMARY KEY constraint.

**Q7: What are the valid statuses for updating?**
A: Pending, Shipped, and Delivered (checked case-insensitively).

**Q8: Why must deletion check status = 'Cancelled'?**
A: The exam requires that only Cancelled orders can be deleted. The DELETE query includes `AND status = 'Cancelled'`, and the code distinguishes between a nonexistent order and a non-Cancelled order.

**Q9: How is total order value calculated?**
A: Using `SELECT SUM(quantity * unit_price) FROM Orders`, which multiplies each order's quantity by its unit price and sums the result.

**Q10: What is the purpose of ORDER BY unit_price DESC in search?**
A: It sorts the matching search results by unit price in descending order (highest price first), as required by the exam.

---

## Mapping to Examination Question (Parts a–e)

| Exam Part | Requirement | Implemented In |
|-----------|-------------|----------------|
| (a) | Connection + insert 3 records via PreparedStatement; handle duplicate IDs | `insertThreeOrders()` |
| (b) | Search by status + quantity > 2, ORDER BY unit_price DESC | `searchOrdersByStatus()` |
| (c) | Increase quantity by given amount; `quantity = quantity + ?`; report missing order | `increaseOrderQuantity()` |
| (d) | Update status if order exists and status is Pending/Shipped/Delivered; show updated details | `updateOrderStatus()` |
| (e) | Total via `SUM(quantity * unit_price)`; delete only if Cancelled; distinguish missing vs non-Cancelled | `calculateTotalAndDeleteCancelled()` |

All five parts (a–e) are fully implemented in the menu-driven application.
