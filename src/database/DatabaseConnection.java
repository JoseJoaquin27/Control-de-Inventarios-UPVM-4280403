package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/sistema_upvm?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Por defecto en XAMPP está vacío

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Error de conexión a la base de datos: " + e.getMessage());
            return null;
        }
    }

    // Método temporal para probar la conexión
    public static void main(String[] args) {
        Connection con = getConnection();
        if (con != null) {
            System.out.println("¡Conexión exitosa con la base de datos de XAMPP!");
        } else {
            System.out.println("No se pudo conectar.");
        }
    }
}