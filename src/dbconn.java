import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbconn {
    public static Connection getConnection() {
        String url = "jdbc:mysql://localhost:3307/student_management"; // ✅ connect to student DB
        String username = "root"; // ✅ your MySQL username
        String password = "root"; // ✅ your MySQL password

        Connection con = null;
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create the connection
            con = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Database connected successfully!");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed.");
            e.printStackTrace();
        }
        return con;
    }

    // Optional: test the connection directly
    public static void main(String[] args) {
        getConnection();
    }
}
