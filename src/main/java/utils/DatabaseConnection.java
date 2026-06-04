package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    private static final String URL = "jdbc:mysql://localhost:3306/steam_clone";
    private static final String USER = "root";
    private static final String PASS = ""; 

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("✅ Koneksi Database Berhasil!");
        } catch (SQLException e) {
            System.out.println("❌ Koneksi Database Gagal: " + e.getMessage());
        }
        return conn;
    }
}