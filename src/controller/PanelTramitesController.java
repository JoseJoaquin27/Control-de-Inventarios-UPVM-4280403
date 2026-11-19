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
import model.Solicitud;
import structures.Cola;
import structures.HistorialManager; // Importante para el historial

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class PanelTramitesController {

    @FXML
    private TableView<Solicitud> tablaTramites;

    @FXML
    private TableColumn<Solicitud, Integer> colFolio;

    @FXML
    private TableColumn<Solicitud, String> colEstudiante;

    @FXML
    private TableColumn<Solicitud, String> colDescripcion;

    @FXML
    private TableColumn<Solicitud, Integer> colMaterialId;

    @FXML
    private TableColumn<Solicitud, Integer> colCantidad;

    @FXML
    private TextField txtFolio;

    @FXML
    private TextField txtEstudiante;

    @FXML
    private TextField txtDescripcion;

    @FXML
    private TextField txtMaterialId;

    @FXML
    private TextField txtCantidad;

    // Estructura de datos para almacenar los trámites en memoria
    private Cola<Solicitud> colaTramites = new Cola<>();
    private ObservableList<Solicitud> observableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colFolio.setCellValueFactory(new PropertyValueFactory<>("folio"));
        colEstudiante.setCellValueFactory(new PropertyValueFactory<>("estudiante"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colMaterialId.setCellValueFactory(new PropertyValueFactory<>("materialId"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        tablaTramites.setItems(observableList);

        // 1. Cargar los trámites desde XAMPP al arrancar
        cargarTramitesDesdeBD();

        // 2. Refrescar la vista de la tabla
        actualizarTabla();
    }

    private void cargarTramitesDesdeBD() {
        String sql = "SELECT folio, estudiante, descripcion, materialId, cantidad FROM Solicitud";
        Connection conn = DatabaseConnection.getConnection();

        if (conn == null) {
            System.out.println("No se pudo establecer la conexión con la base de datos.");
            return;
        }

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int folio = rs.getInt("folio");
                String estudiante = rs.getString("estudiante");
                String descripcion = rs.getString("descripcion");
                int materialId = rs.getInt("materialId");
                int cantidad = rs.getInt("cantidad");

                Solicitud nuevaSolicitud = new Solicitud(
                        folio,
                        Solicitud.Tipo.TRAMITE,
                        estudiante,
                        descripcion,
                        materialId,
                        cantidad
                );
                colaTramites.encolar(nuevaSolicitud);
            }
            System.out.println("Trámites cargados exitosamente desde XAMPP.");

        } catch (Exception e) {
            System.out.println("Error al cargar trámites de la BD: " + e.getMessage());
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void registrarTramite() {
        try {
            int folio = Integer.parseInt(txtFolio.getText());
            String estudiante = txtEstudiante.getText();
            String descripcion = txtDescripcion.getText();
            int materialId = Integer.parseInt(txtMaterialId.getText());
            int cantidad = Integer.parseInt(txtCantidad.getText());

            // Crear la solicitud
            Solicitud nuevaSolicitud = new Solicitud(
                    folio,
                    Solicitud.Tipo.TRAMITE,
                    estudiante,
                    descripcion,
                    materialId,
                    cantidad
            );

            // Guardar en la base de datos de XAMPP (Persistencia)
            String sql = "INSERT INTO Solicitud (folio, estudiante, descripcion, materialId, cantidad) VALUES (?, ?, ?, ?, ?)";
            Connection conn = DatabaseConnection.getConnection();

            if (conn != null) {
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, folio);
                    pstmt.setString(2, estudiante);
                    pstmt.setString(3, descripcion);
                    pstmt.setInt(4, materialId);
                    pstmt.setInt(5, cantidad);
                    pstmt.executeUpdate();
                } catch (Exception e) {
                    System.out.println("Error al guardar trámite en XAMPP (¿El folio ya existe?): " + e.getMessage());
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

            // Almacenar en la estructura en memoria
            colaTramites.encolar(nuevaSolicitud);

            // ---> REGISTRAR AUTOMÁTICAMENTE EN LA PILA GLOBAL DE HISTORIAL <---
            Accion nuevaAccion = new Accion(Accion.Tipo.ATENDER_TRAMITE, null, 0, null, nuevaSolicitud);
            HistorialManager.agregarAccion(nuevaAccion);

            // Actualizar tabla
            actualizarTabla();

            // Limpiar campos
            txtFolio.clear();
            txtEstudiante.clear();
            txtDescripcion.clear();
            txtMaterialId.clear();
            txtCantidad.clear();

            System.out.println("Trámite registrado y guardado en XAMPP exitosamente.");

        } catch (NumberFormatException e) {
            System.out.println("Error: Folio, ID de Material y Cantidad deben ser numéricos.");
        }
    }

    private void actualizarTabla() {
        observableList.clear();
        colaTramites.recorrer(solicitud -> observableList.add(solicitud));
    }
}