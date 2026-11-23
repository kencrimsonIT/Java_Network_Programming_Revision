package Socket.TCP_IP;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Dao {
    private Connection conn;
    private Statement stmt;
    private PreparedStatement ps;
    private ResultSet rs;

    public Dao() {
        String driver = "net.ucanaccess.jdbc.UcanaccessDriver";
        String url = "jdbc:ucanacces://D:\\Network Programming\\Database\\student.accdb";

        try {
            Class.forName(driver);
            this.conn = DriverManager.getConnection(url);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
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

    public boolean checkUsername(String username) {
        String query = "SELECT username FROM users WHERE username = ?";
        try {
            this.ps = this.conn.prepareStatement(query);
            this.ps.setString(1, username);
            this.rs = this.ps.executeQuery();
            if (this.rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*public boolean checkSignIn(String username, String param) {
        String query = "SELECT username, password FROM users WHERE username = ? AND password = ?";
        try {
            this.ps = this.conn.prepareStatement(query);
            this.ps.setString(1, username);
            this.ps.setString(2, param);
            this.rs = this.ps.executeQuery();
            if (this.rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }*/

    public boolean checkSignIn(String username, String param) {
        String query = "SELECT username, password FROM users WHERE username = '" + username + "' and password = '" + param + "'";
        try {
            this.stmt = this.conn.createStatement();
            this.rs = this.stmt.executeQuery(query);
            if (this.rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Student> findStudentById(String param) {
        int stuId = Integer.parseInt(param);
        List<Student> studentList = new ArrayList<>();
        String query = "SELECT * FROM students WHERE id = ?";
        try {
            this.ps = this.conn.prepareStatement(query);
            this.ps.setInt(1, stuId);
            this.rs = this.ps.executeQuery();
            while (this.rs.next()) {
                Student student = new Student();
                student.setSid(this.rs.getInt("id"));
                student.setName(this.rs.getString("name"));
                student.setbYear(this.rs.getInt("year"));
                student.setGrade(this.rs.getDouble("grade"));
                studentList.add(student);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public List<Student> findStudentByName(String param) {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM students WHERE name = ?";
        try {
            this.ps = this.conn.prepareStatement(query);
            this.ps.setString(1, "%" + param);
            this.rs = this.ps.executeQuery();
            while (this.rs.next()) {
                Student student = new Student();
                student.setSid(this.rs.getInt("id"));
                student.setName(this.rs.getString("name"));
                student.setbYear(this.rs.getInt("year"));
                student.setGrade(this.rs.getDouble("grade"));
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
