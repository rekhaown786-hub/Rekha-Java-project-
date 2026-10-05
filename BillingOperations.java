package Supermarket;

import java.sql.*;

public class BillingOperations {
    private static final Connection c = DBConnection.getConnection();

    // Add a new product to inventory
    public static void addProduct(int id, String name, double price, int stock) {
        String query = "INSERT INTO products VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = c.prepareStatement(query);
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setDouble(3, price);
            ps.setInt(4, stock);
            
            ps.executeUpdate();
            System.out.println("Product added successfully: " + name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View all products in stock
    public static void viewProducts() {
        String query = "SELECT * FROM products";
        try {
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            
            System.out.println("\n--- AVAILABLE PRODUCTS ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                        " | Name: " + rs.getString("name") +
                        " | Price: $" + rs.getDouble("price") +
                        " | Stock: " + rs.getInt("stock_quantity"));
            }
            System.out.println("---------------------------\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Generate a bill for a product purchase and update stock
    public static void generateBill(int productId, int quantity) {
        String checkStockQuery = "SELECT name, price, stock_quantity FROM products WHERE id = ?";
        String insertBillQuery = "INSERT INTO bills (product_id, quantity, total_amount) VALUES (?, ?, ?)";
        String updateStockQuery = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE id = ?";

        try {
            PreparedStatement psCheck = c.prepareStatement(checkStockQuery);
            psCheck.setInt(1, productId);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int currentStock = rs.getInt("stock_quantity");

                if (currentStock >= quantity) {
                    double totalAmount = price * quantity;

                    // 1. Save Bill to Database
                    PreparedStatement psBill = c.prepareStatement(insertBillQuery);
                    psBill.setInt(1, productId);
                    psBill.setInt(2, quantity);
                    psBill.setDouble(3, totalAmount);
                    psBill.executeUpdate();

                    // 2. Reduce Inventory Stock
                    PreparedStatement psStock = c.prepareStatement(updateStockQuery);
                    psStock.setInt(1, quantity);
                    psStock.setInt(2, productId);
                    psStock.executeUpdate();

                    // 3. Print Receipt
                    System.out.println("\n==============================");
                    System.out.println("        RECEIPT PRINT         ");
                    System.out.println("Product: " + name);
                    System.out.println("Price per unit: $" + price);
                    System.out.println("Quantity: " + quantity);
                    System.out.println("TOTAL BILL AMOUNT: $" + totalAmount);
                    System.out.println("==============================\n");
                } else {
                    System.out.println("Insufficient stock! Available quantity: " + currentStock);
                }
            } else {
                System.out.println("Product ID " + productId + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View bill history
    public static void viewBills() {
        String query = "SELECT b.bill_id, p.name, b.quantity, b.total_amount FROM bills b JOIN products p ON b.product_id = p.id";
        try {
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            
            System.out.println("\n--- BILLING HISTORY ---");
            while (rs.next()) {
                System.out.println("Bill ID: " + rs.getInt("bill_id") +
                        " | Product: " + rs.getString("name") +
                        " | Qty: " + rs.getInt("quantity") +
                        " | Total: $" + rs.getDouble("total_amount"));
            }
            System.out.println("------------------------\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // 1. Add sample product inventory
        addProduct(1, "Milk 1L", 2.50, 50);
        addProduct(2, "Bread Loaf", 1.80, 40);
        addProduct(3, "Cereal Box", 4.20, 20);

        // 2. Display initial inventory
        viewProducts();

        // 3. Generate bills for sales
        generateBill(1, 4);  // 4 units of Milk
        generateBill(3, 2);  // 2 units of Cereal

        // 4. Display stock after sales
        viewProducts();

        // 5. Display bill log summary
        viewBills();
    }
}
