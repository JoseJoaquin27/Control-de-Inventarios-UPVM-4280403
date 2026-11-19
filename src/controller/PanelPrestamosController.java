package controller;

import database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Accion; // Importante para el historial
import model.Prestamo;
import structures.Cola;
import structures.HistorialManager; // Importante para el historial

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class PanelPrestamosController {

    @FXML
    private TableView<Prestamo> tablaPrestamos;

    @FXML
    private TableColumn<Prestamo, Integer> colFolio;

    @FXML
    private TableColumn<Prestamo, Integer> colMaterialId;

    @FXML
    private TableColumn<Prestamo, Integer> colCantidad;

    @FXML
    private TableColumn<Prestamo, String> colEstudiante;

    @FXML
    private TextField txtFolio;

    @FXML
    private TextField txtMaterialId;

    @FXML
    private TextField txtCantidad;

    @FXML
    private TextField txtEstudiante;

    // Estructura de datos Cola personalizada para los préstamos en memoria
    private Cola<Prestamo> colaPrestamos = new Cola<>();

    private ObservableList<Prestamo> observableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Enlazar columnas con los getters de la clase Prestamo
        colFolio.setCellValueFactory(new PropertyValueFactory<>("folio"));
        colMaterialId.setCellValueFactory(new PropertyValueFactory<>("materialId"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colEstudiante.setCellValueFactory(new PropertyValueFactory<>("estudiante"));

        tablaPrestamos.setItems(observableList);

        // 1. Cargar los préstamos desde XAMPP al arrancar
        cargarPrestamosDesdeBD();

        // 2. Refrescar la vista de la tabla
        actualizarTabla();
    }

    private void cargarPrestamosDesdeBD() {
        String sql = "SELECT folio, materialId, cantidad, estudiante FROM Prestamo";
        Connection conn = DatabaseConnection.getConnection();

        if (conn == null) {
            System.out.println("No se pudo establecer la conexión con la base de datos.");
            return;
        }

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int folio = rs.getInt("folio");
                int materialId = rs.getInt("materialId");
                int cantidad = rs.getInt("cantidad");
                String estudiante = rs.getString("estudiante");

                Prestamo prestamo = new Prestamo(folio, materialId, cantidad, estudiante);
                // Encolar en la estructura en memoria
                colaPrestamos.encolar(prestamo);
            }
            System.out.println("Préstamos cargados exitosamente desde XAMPP.");

        } catch (Exception e) {
            System.out.println("Error al cargar préstamos de la BD: " + e.getMessage());
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void encolarPrestamo() {
        try {
            int folio = Integer.parseInt(txtFolio.getText());
            int materialId = Integer.parseInt(txtMaterialId.getText());
            int cantidad = Integer.parseInt(txtCantidad.getText());
            String estudiante = txtEstudiante.getText();

            // 1. Crear el objeto con la estructura que definiste en model.Prestamo
            Prestamo nuevoPrestamo = new Prestamo(folio, materialId, cantidad, estudiante);

            // 2. Guardar en la base de datos de XAMPP (Persistencia)
            String sql = "INSERT INTO Prestamo (folio, materialId, cantidad, estudiante) VALUES (?, ?, ?, ?)";
            Connection conn = DatabaseConnection.getConnection();

            if (conn != null) {
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, folio);
                    pstmt.setInt(2, materialId);
                    pstmt.setInt(3, cantidad);
                    pstmt.setString(4, estudiante);
                    pstmt.executeUpdate();
                } catch (Exception e) {
                    System.out.println("Error al guardar préstamo en XAMPP (¿El folio ya existe?): " + e.getMessage());
                    return;
                } finally {
                    try {
                        conn.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                System.out.println("No se pudo conectar a la base de datos para guardar.");
                return;
            }

            // 3. Encolar en tu estructura de datos en memoria
            colaPrestamos.encolar(nuevoPrestamo);

            // ---> 3.1. REGISTRAR AUTOMÁTICAMENTE EN LA PILA GLOBAL DE HISTORIAL <---
            Accion nuevaAccion = new Accion(Accion.Tipo.ATENDER_PRESTAMO, null, 0, nuevoPrestamo, null);
            HistorialManager.agregarAccion(nuevaAccion);

            // 4. Actualizar la vista de la tabla
            actualizarTabla();

            // Limpiar campos
            txtFolio.clear();
            txtMaterialId.clear();
            txtCantidad.clear();
            txtEstudiante.clear();

            System.out.println("Préstamo encolado y guardado en XAMPP exitosamente.");

        } catch (NumberFormatException e) {
            System.out.println("Error: Folio, ID de material y Cantidad deben ser números enteros.");
        }
    }

    @FXML
    private void atenderPrestamo() {
        // Desencola el primer elemento de la fila (Principio FIFO)
        Prestamo prestamoAtendido = colaPrestamos.desencolar();

        if (prestamoAtendido != null) {
            // Eliminar de la base de datos de XAMPP según el folio atendido
            String sql = "DELETE FROM Prestamo WHERE folio = ?";
            Connection conn = DatabaseConnection.getConnection();

            if (conn != null) {
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, prestamoAtendido.getFolio());
                    pstmt.executeUpdate();
                } catch (Exception e) {
                    System.out.println("Error al eliminar el préstamo atendido de XAMPP: " + e.getMessage());
                } finally {
                    try {
                        conn.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            System.out.println("Préstamo atendido: " + prestamoAtendido.getEstudiante());
            actualizarTabla();
        } else {
            System.out.println("La cola de préstamos está vacía.");
        }
    }

    private void actualizarTabla() {
        observableList.clear();
        // Recorre la cola usando tu método personalizado para reflejarlo en la tabla gráfica
        colaPrestamos.recorrer(prestamo -> observableList.add(prestamo));
    }
}