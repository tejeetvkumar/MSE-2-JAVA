/*
 * MSE-2 Q1: Order Management System
 * CS336B - Object Oriented Programming with Java
 * 
 * A menu-driven Java JDBC application to manage customer orders
 * using MySQL database.
 * 
 * Operations:
 *   (a) Database Connection and Insert Records
 *   (b) Search Orders by Status (quantity > 2, sorted by unit_price DESC)
 *   (c) Increase Order Quantity
 *   (d) Update Order Status (Pending, Shipped, Delivered)
 *   (e) Calculate Total Order Value and Delete Cancelled Order
 */

import java.sql.*;
import java.util.Scanner;

/**
 * OrderManagement is the main class containing the menu-driven
 * JDBC application for the Order Management System.
 */
public class OrderManagement {

    // JDBC connection parameters - update these to match your MySQL setup
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ecommerce_db";
    private static final String DB_USER = "root";      // <-- change to your MySQL username if needed
    // Password is read from the MYSQL_PASSWORD environment variable (configurable, not hardcoded).
    // Set it before running, e.g. PowerShell:  $env:MYSQL_PASSWORD="yourpassword"
    // If not set, the program will prompt you to enter it at runtime.
    private static String DB_PASSWORD = null;

    // Scanner for reading user input
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Resolves the MySQL password from the MYSQL_PASSWORD environment variable.
     * If the variable is not set, prompts the user to enter it at runtime.
     * This avoids hardcoding or inventing a password in the source code.
     */
    private static String resolvePassword() {
        if (DB_PASSWORD != null) {
            return DB_PASSWORD;
        }
        String envPassword = System.getenv("MYSQL_PASSWORD");
        if (envPassword != null && !envPassword.isEmpty()) {
            DB_PASSWORD = envPassword;
            return DB_PASSWORD;
        }
        System.out.print("Enter MySQL password for user 'root' (leave empty for none): ");
        String input = scanner.nextLine().trim();
        DB_PASSWORD = input.isEmpty() ? null : input;
        return DB_PASSWORD;
    }

    public static void main(String[] args) {
        int choice;

        // Resolve MySQL password from environment variable or prompt (not hardcoded).
        resolvePassword();

        do {
            displayMenu();
            System.out.print("Enter your choice: ");
            choice = readInt();

            switch (choice) {
                case 1:
                    insertThreeOrders();
                    break;
                case 2:
                    searchOrdersByStatus();
                    break;
                case 3:
                    increaseOrderQuantity();
                    break;
                case 4:
                    updateOrderStatus();
                    break;
                case 5:
                    calculateTotalAndDeleteCancelled();
                    break;
                case 0:
                    System.out.println("Exiting the application. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            System.out.println();
        } while (choice != 0);

        scanner.close();
    }

    /**
     * Displays the main menu to the user.
     */
    private static void displayMenu() {
        System.out.println("========== ORDER MANAGEMENT SYSTEM ==========");
        System.out.println("1. Insert Three Orders");
        System.out.println("2. Search Orders by Status");
        System.out.println("3. Increase Order Quantity");
        System.out.println("4. Update Order Status");
        System.out.println("5. Calculate Total Order Value and Delete Cancelled Order");
        System.out.println("0. Exit");
        System.out.println("=============================================");
    }

    /**
     * Reads an integer from the user, handling invalid input gracefully.
     */
    private static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    /**
     * Reads a non-empty line of text from the user.
     */
    private static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    // -----------------------------------------------------------------
    // (a) Database Connection and Insert Records (2 Marks)
    // -----------------------------------------------------------------

    /**
     * Inserts three order records into the Orders table using PreparedStatement.
     * Handles duplicate order IDs gracefully.
     */
    public static void insertThreeOrders() {
        System.out.println("\n--- Insert Three Orders ---");

        String sql = "INSERT INTO Orders (order_id, customer_name, product_name, quantity, unit_price, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Collect three orders from user input
            for (int i = 1; i <= 3; i++) {
                System.out.println("\nEnter details for order " + i + ":");

                int orderId = readIntInput("  Order ID: ");
                String customerName = readNonEmptyInput("  Customer Name: ");
                String productName = readNonEmptyInput("  Product Name: ");
                int quantity = readPositiveIntInput("  Quantity: ");
                double unitPrice = readPositiveDoubleInput("  Unit Price: ");
                String status = readNonEmptyInput("  Status: ");

                pstmt.setInt(1, orderId);
                pstmt.setString(2, customerName);
                pstmt.setString(3, productName);
                pstmt.setInt(4, quantity);
                pstmt.setDouble(5, unitPrice);
                pstmt.setString(6, status);

                try {
                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("  Order " + orderId + " inserted successfully.");
                    }
                } catch (SQLIntegrityConstraintViolationException e) {
                    System.out.println("  Order ID " + orderId + " already exists (duplicate). Skipped.");
                }
            }

            System.out.println("\nInsert operation completed.");

        } catch (SQLException e) {
            System.err.println("Database error while inserting orders: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // (b) Search Orders (2 Marks)
    // -----------------------------------------------------------------

    /**
     * Searches orders by status where quantity > 2, sorted by unit_price DESC.
     */
    public static void searchOrdersByStatus() {
        System.out.println("\n--- Search Orders by Status ---");

        String status = readNonEmptyInput("Enter order status to search: ");

        String sql = "SELECT order_id, customer_name, product_name, quantity, unit_price, status "
                   + "FROM Orders "
                   + "WHERE status = ? AND quantity > 2 "
                   + "ORDER BY unit_price DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                boolean found = false;

                System.out.println("\nOrders with status '" + status + "' and quantity > 2 "
                                   + "(sorted by unit price descending):");
                System.out.println("--------------------------------------------------------------------------------");
                System.out.printf("%-10s %-20s %-20s %-10s %-12s %-12s%n",
                        "Order ID", "Customer Name", "Product Name", "Quantity", "Unit Price", "Status");
                System.out.println("--------------------------------------------------------------------------------");

                while (rs.next()) {
                    found = true;
                    int orderId = rs.getInt("order_id");
                    String customerName = rs.getString("customer_name");
                    String productName = rs.getString("product_name");
                    int quantity = rs.getInt("quantity");
                    double unitPrice = rs.getDouble("unit_price");
                    String orderStatus = rs.getString("status");

                    System.out.printf("%-10d %-20s %-20s %-10d %-12.2f %-12s%n",
                            orderId, customerName, productName, quantity, unitPrice, orderStatus);
                }

                System.out.println("--------------------------------------------------------------------------------");

                if (!found) {
                    System.out.println("No orders found with status '" + status + "' and quantity > 2.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Database error while searching orders: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // (c) Increase Order Quantity (2 Marks)
    // -----------------------------------------------------------------

    /**
     * Increases the quantity of an existing order by a given amount.
     * Displays a message if the order does not exist.
     */
    public static void increaseOrderQuantity() {
        System.out.println("\n--- Increase Order Quantity ---");

        int orderId = readIntInput("Enter Order ID: ");
        int additionalQuantity = readPositiveIntInput("Enter additional quantity to add: ");

        String checkSql = "SELECT order_id FROM Orders WHERE order_id = ?";
        String updateSql = "UPDATE Orders SET quantity = quantity + ? WHERE order_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            // Check if the order exists
            checkStmt.setInt(1, orderId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Order ID " + orderId + " does not exist.");
                    return;
                }
            }

            // Increase the quantity
            updateStmt.setInt(1, additionalQuantity);
            updateStmt.setInt(2, orderId);
            int rowsAffected = updateStmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Quantity of Order ID " + orderId
                                   + " increased by " + additionalQuantity + " successfully.");

                // Display updated quantity
                String selectSql = "SELECT quantity FROM Orders WHERE order_id = ?";
                try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                    selectStmt.setInt(1, orderId);
                    try (ResultSet rs = selectStmt.executeQuery()) {
                        if (rs.next()) {
                            System.out.println("Updated quantity: " + rs.getInt("quantity"));
                        }
                    }
                }
            } else {
                System.out.println("Failed to update order ID " + orderId + ".");
            }

        } catch (SQLException e) {
            System.err.println("Database error while increasing quantity: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // (d) Update Order Status (2 Marks)
    // -----------------------------------------------------------------

    /**
     * Updates the status of an existing order if the new status is valid
     * (Pending, Shipped, or Delivered, case-insensitive). Displays updated details.
     */
    public static void updateOrderStatus() {
        System.out.println("\n--- Update Order Status ---");

        int orderId = readIntInput("Enter Order ID: ");
        String newStatus = readNonEmptyInput("Enter new status (Pending/Shipped/Delivered): ");

        // Validate the new status (case-insensitive)
        if (!isValidStatus(newStatus)) {
            System.out.println("Invalid status '" + newStatus + "'. "
                             + "Valid statuses are: Pending, Shipped, Delivered.");
            return;
        }

        // Normalize the status to proper case for storage
        String normalizedStatus = normalizeStatus(newStatus);

        String checkSql = "SELECT order_id, customer_name, product_name, quantity, unit_price, status "
                        + "FROM Orders WHERE order_id = ?";
        String updateSql = "UPDATE Orders SET status = ? WHERE order_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            // Check if the order exists
            checkStmt.setInt(1, orderId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Order ID " + orderId + " does not exist.");
                    return;
                }
            }

            // Update the status
            updateStmt.setString(1, normalizedStatus);
            updateStmt.setInt(2, orderId);
            int rowsAffected = updateStmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Status of Order ID " + orderId + " updated to '"
                                   + normalizedStatus + "' successfully.");

                // Display updated order details
                displayOrderDetails(conn, orderId);
            } else {
                System.out.println("Failed to update order ID " + orderId + ".");
            }

        } catch (SQLException e) {
            System.err.println("Database error while updating status: " + e.getMessage());
        }
    }

    /**
     * Checks whether the given status is one of the valid statuses
     * (Pending, Shipped, Delivered) in a case-insensitive manner.
     */
    private static boolean isValidStatus(String status) {
        if (status == null || status.isEmpty()) {
            return false;
        }
        String upper = status.toUpperCase();
        return upper.equals("PENDING") || upper.equals("SHIPPED") || upper.equals("DELIVERED");
    }

    /**
     * Normalizes a valid status string to its proper-cased form.
     */
    private static String normalizeStatus(String status) {
        String upper = status.toUpperCase();
        if (upper.equals("PENDING")) return "Pending";
        if (upper.equals("SHIPPED")) return "Shipped";
        if (upper.equals("DELIVERED")) return "Delivered";
        return status;
    }

    /**
     * Displays the full details of an order given its order ID.
     */
    private static void displayOrderDetails(Connection conn, int orderId) throws SQLException {
        String sql = "SELECT order_id, customer_name, product_name, quantity, unit_price, status "
                   + "FROM Orders WHERE order_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\nUpdated Order Details:");
                    System.out.println("----------------------------------------");
                    System.out.println("Order ID     : " + rs.getInt("order_id"));
                    System.out.println("Customer     : " + rs.getString("customer_name"));
                    System.out.println("Product      : " + rs.getString("product_name"));
                    System.out.println("Quantity     : " + rs.getInt("quantity"));
                    System.out.println("Unit Price   : " + rs.getDouble("unit_price"));
                    System.out.println("Status       : " + rs.getString("status"));
                    System.out.println("----------------------------------------");
                }
            }
        }
    }

    // -----------------------------------------------------------------
    // (e) Calculate Total and Delete Order (2 Marks)
    // -----------------------------------------------------------------

    /**
     * Calculates the total order value using SUM(quantity * unit_price),
     * then deletes an order only if its status is Cancelled.
     * Distinguishes between a nonexistent order and a non-Cancelled order.
     */
    public static void calculateTotalAndDeleteCancelled() {
        System.out.println("\n--- Calculate Total Order Value and Delete Cancelled Order ---");

        // Part 1: Calculate total order value
        String totalSql = "SELECT SUM(quantity * unit_price) AS total_value FROM Orders";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(totalSql)) {

            double total = 0.0;
            if (rs.next()) {
                total = rs.getDouble("total_value");
            }
            System.out.printf("Total order value (SUM of quantity * unit_price): %.2f%n", total);

        } catch (SQLException e) {
            System.err.println("Database error while calculating total: " + e.getMessage());
            return;
        }

        // Part 2: Delete a Cancelled order
        int orderId = readIntInput("\nEnter Order ID to delete: ");

        String checkSql = "SELECT order_id, status FROM Orders WHERE order_id = ?";
        String deleteSql = "DELETE FROM Orders WHERE order_id = ? AND status = 'Cancelled'";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {

            // Check if the order exists and what its status is
            checkStmt.setInt(1, orderId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Order ID " + orderId + " does not exist. Cannot delete.");
                    return;
                }

                String existingStatus = rs.getString("status");

                if (!existingStatus.equalsIgnoreCase("Cancelled")) {
                    System.out.println("Order ID " + orderId + " has status '" + existingStatus
                                     + "'. Only Cancelled orders can be deleted. Deletion aborted.");
                    return;
                }
            }

            // Delete the order (status is Cancelled)
            deleteStmt.setInt(1, orderId);
            int rowsAffected = deleteStmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Order ID " + orderId + " (Cancelled) deleted successfully.");
            } else {
                System.out.println("Order ID " + orderId + " could not be deleted.");
            }

        } catch (SQLException e) {
            System.err.println("Database error while deleting order: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // Helper methods for reading validated user input
    // -----------------------------------------------------------------

    private static int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input. Please enter a valid integer.");
            }
        }
    }

    private static String readNonEmptyInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("  Input cannot be empty. Please try again.");
        }
    }

    private static int readPositiveIntInput(String prompt) {
        while (true) {
            int value = readIntInput(prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("  Value must be greater than 0. Please try again.");
        }
    }

    private static double readPositiveDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value > 0) {
                    return value;
                }
                System.out.println("  Value must be greater than 0. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input. Please enter a valid number.");
            }
        }
    }
}
