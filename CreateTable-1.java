package Supermarket;

import java.sql.Connection;
import java.sql.Statement;

public class CreateTable {
    public static void main(String[] args) throws Exception {
        Connection c = DBConnection.getConnection();
        
        String createProductsTable = """
            CREATE TABLE IF NOT EXISTS products (
                id INT PRIMARY KEY,
                name VARCHAR(100),
                price DOUBLE,
                stock_quantity INT
            )
            """;
            
        String createBillsTable = """
            CREATE TABLE IF NOT EXISTS bills (
                bill_id INT AUTO_INCREMENT PRIMARY KEY,
                product_id INT,
                quantity INT,
                total_amount DOUBLE,
                FOREIGN KEY (product_id) REFERENCES products(id)
            )
            """;

        Statement s = c.createStatement();
        s.executeUpdate(createProductsTable);
        s.executeUpdate(createBillsTable);
        System.out.println("Supermarket Tables Created Successfully");
    }
}
