package Test_1.Task2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Dao {
    private Connection conn;
    private PreparedStatement ps, select, update;
    private ResultSet rs;

    public Dao() {
        String driver = "net.ucanaccess.jsbc.UcanaccessDriver";
        String url = "jdbc:ucanaccess://D:\\Network Programming\\Database\\product.accdb";

        try {
            Class.forName(driver);
            this.conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (this.conn != null) this.conn.close();
            if (this.ps != null) this.ps.close();
            if (this.rs != null) this.rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addProduct(Product product) {
        String query = "INSERT INTO products(productID, name, count, price) VALUES (?, ?, ?, ?)";
        try {
             this.ps = this.conn.prepareStatement(query);
             this.ps.setInt(1, product.getProductID());
             this.ps.setString(2, product.getName());
             this.ps.setInt(3, product.getCount());
             this.ps.setDouble(4, product.getPrice());

             this.ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String buyProduct(List<Integer> productID) {
        String selectQuery = "SELECT count FROM products WHERE productID = ?";
        String updateQuery = "UPDATE products SET count = count - 1 WHERE productID = ?";
        try {
            this.conn.setAutoCommit(false);
            this.select = this.conn.prepareStatement(selectQuery);
            this.update = this.conn.prepareStatement(updateQuery);
            for (Integer id: productID) {
                select.setInt(1, id);
                ResultSet rs = this.select.executeQuery();
                if (!rs.next()) { //Non-existed products
                    this.conn.rollback();
                    return "Product " + id + " not found";
                }
                if (rs.getInt("count") <= 0) { //Out of stock products
                    this.conn.rollback();
                    return "Product " + id + " is out of stock";
                }
                update.setInt(1, id);
                this.update.executeUpdate();
            }
            this.conn.commit();
            return "OK";
        } catch (SQLException e) {
            try {
                this.conn.rollback();
            } catch (SQLException ignore) {}
            return "ERROR: " + e.getMessage();
        } finally {
            try {
                this.conn.setAutoCommit(true);
            } catch (SQLException ignore) {}
        }
    }

    public List<Product> findByPrice(double from, double to) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products WHERE price BETWEEN ? AND ?";
        try {
            this.ps = this.conn.prepareStatement(query);
            this.ps.setDouble(1, from);
            this.ps.setDouble(2, to);

            this.rs = this.ps.executeQuery();
            while (this.rs.next()) {
                Product product = new Product();
                product.setProductID(this.rs.getInt("productID"));
                product.setName(this.rs.getString("name"));
                product.setCount(this.rs.getInt("count"));
                product.setPrice(this.rs.getDouble("price"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Product> findByName(String name) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products WHERE name LIKE ?";
        try {
            this.ps = this.conn.prepareStatement(query);
            this.ps.setString(1, name);
            this.rs = this.ps.executeQuery();
            while (this.rs.next()) {
                Product product = new Product();
                product.setProductID(this.rs.getInt("productID"));
                product.setName(this.rs.getString("name"));
                product.setCount(this.rs.getInt("count"));
                product.setPrice(this.rs.getDouble("price"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
}
