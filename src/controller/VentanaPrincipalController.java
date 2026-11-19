package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class VentanaPrincipalController {

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        // Carga la pestaña de inicio automáticamente al abrir la aplicación
        mostrarInicio();
    }

    @FXML
    public void mostrarInicio() {
        // Apunta exactamente a inicio.fxml (con minúscula)
        cargarVista("/view/inicio.fxml");
    }

    @FXML
    public void mostrarInventario() {
        cargarVista("/view/PanelInventario.fxml");
    }

    @FXML
    public void mostrarPrestamos() {
        // Conectado al panel de préstamos usando tu estructura de Cola
        cargarVista("/view/PanelPrestamos.fxml");
    }

    @FXML
    public void mostrarTramites() {
        // Conectado al panel de trámites
        cargarVista("/view/PanelTramites.fxml");
    }

    @FXML
    public void mostrarHistorial() {
        // Conectado al panel de historial
        cargarVista("/view/PanelHistorial.fxml");
    }

    private void cargarVista(String fxmlPath) {
        try {
            Pane vista = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(vista);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}