package controller;

import database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Accion;
import structures.HistorialManager;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class PanelHistorialController {

    @FXML private TableView<Accion> tablaHistorial;
    @FXML private TableColumn<Accion, Accion.Tipo> colTipo;
    @FXML private TableColumn<Accion, String> colDetalle;

    private ObservableList<Accion> observableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colDetalle.setCellValueFactory(new PropertyValueFactory<>("detalle"));

        tablaHistorial.setItems(observableList);
        actualizarTabla();
    }

    private void actualizarTabla() {
        observableList.clear();
        HistorialManager.getPilaGlobal().recorrer(accion -> observableList.add(accion));
    }

    @FXML
    private void deshacerAccion() {
        Accion ultimaAccion = HistorialManager.getPilaGlobal().desapilar();

        if (ultimaAccion != null) {
            HistorialManager.eliminarUltimoDeBd();
            Connection conn = DatabaseConnection.getConnection();

            try {
                if (ultimaAccion.getTipo() == Accion.Tipo.ALTA && ultimaAccion.getMaterial() != null) {
                    int idMaterial = ultimaAccion.getMaterial().getId();
                    String sql = "DELETE FROM material WHERE id = ?";
                    if (conn != null) {
                        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                            pstmt.setInt(1, idMaterial);
                            pstmt.executeUpdate();
                        }
                    }
                    System.out.println("Se deshizo el alta del material con ID: " + idMaterial);

                } else if (ultimaAccion.getTipo() == Accion.Tipo.ATENDER_PRESTAMO && ultimaAccion.getPrestamo() != null) {
                    int folio = ultimaAccion.getPrestamo().getFolio();
                    String sql = "DELETE FROM prestamo WHERE folio = ?";
                    if (conn != null) {
                        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                            pstmt.setInt(1, folio);
                            pstmt.executeUpdate();
                        }
                    }
                    System.out.println("Se deshizo el préstamo con folio: " + folio);

                } else if (ultimaAccion.getTipo() == Accion.Tipo.ATENDER_TRAMITE && ultimaAccion.getSolicitud() != null) {
                    int folio = ultimaAccion.getSolicitud().getFolio();
                    String sql = "DELETE FROM solicitud WHERE folio = ?";
                    if (conn != null) {
                        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                            pstmt.setInt(1, folio);
                            pstmt.executeUpdate();
                        }
                    }
                    System.out.println("Se deshizo el trámite con folio: " + folio);
                }

            } catch (Exception e) {
                System.out.println("Error al deshacer la acción en la BD: " + e.getMessage());
            } finally {
                try {
                    if (conn != null) conn.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            actualizarTabla();
            System.out.println("Acción deshecha con éxito.");

        } else {
            System.out.println("El historial está vacío, no hay acciones que deshacer.");
        }
    }
}