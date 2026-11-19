package controller;

import database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Accion; // Importante para el tipo de acción
import model.Material;
import structures.HistorialManager; // Importa el manejador global
import structures.ListaEnlazada;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Comparator;

public class PanelInventarioController {

    @FXML private TableView<Material> tablaInventario;
    @FXML private TableColumn<Material, Integer> colId;
    @FXML private TableColumn<Material, String> colNombre;
    @FXML private TableColumn<Material, String> colCategoria;
    @FXML private TableColumn<Material, Integer> colExistencias;

    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCategoria;
    @FXML private TextField txtExistencias;

    // Tu estructura de datos enlazada personalizada en memoria
    private ListaEnlazada<Material> listaInventario = new ListaEnlazada<>();

    // Lista observable para que JavaFX pinte los elementos en el TableView
    private ObservableList<Material> observableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Enlazar columnas con los métodos getters de la clase Material
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colExistencias.setCellValueFactory(new PropertyValueFactory<>("existencias"));

        // 1. Cargar los datos desde XAMPP hacia tu lista enlazada al arrancar
        cargarMaterialesDesdeBD();

        // 2. Refrescar la tabla con los datos obtenidos
        actualizarVistaTabla();

        tablaInventario.setItems(observableList);
    }

    private void cargarMaterialesDesdeBD() {
        String sql = "SELECT id, nombre, categoria, existencias FROM Material";
        Connection conn = DatabaseConnection.getConnection();

        if (conn == null) {
            System.out.println("No se pudo establecer la conexión con la base de datos.");
            return;
        }

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String categoria = rs.getString("categoria");
                int existencias = rs.getInt("existencias");

                Material mat = new Material(id, nombre, categoria, existencias);
                // Insertar en tu estructura en memoria ordenada por ID
                listaInventario.insertarOrdenado(mat, Comparator.comparingInt(Material::getId));
            }
            System.out.println("Inventario cargado exitosamente desde XAMPP.");

        } catch (Exception e) {
            System.out.println("Error al cargar materiales de la BD: " + e.getMessage());
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void actualizarVistaTabla() {
        observableList.clear();
        listaInventario.recorrer(material -> observableList.add(material));
    }

    @FXML
    private void agregarMaterial() {
        try {
            // Leer valores de los campos de texto
            int id = Integer.parseInt(txtId.getText());
            String nombre = txtNombre.getText();
            String categoria = txtCategoria.getText();
            int existencias = Integer.parseInt(txtExistencias.getText());

            // 1. Crear el objeto Material (valida internamente)
            Material nuevoMaterial = new Material(id, nombre, categoria, existencias);

            // 2. Guardar en la base de datos de XAMPP (Persistencia)
            String sql = "INSERT INTO Material (id, nombre, categoria, existencias) VALUES (?, ?, ?, ?)";
            Connection conn = DatabaseConnection.getConnection();

            if (conn != null) {
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, id);
                    pstmt.setString(2, nombre);
                    pstmt.setString(3, categoria);
                    pstmt.setInt(4, existencias);
                    pstmt.executeUpdate();
                } catch (Exception e) {
                    System.out.println("Error al guardar en XAMPP (¿El ID ya existe?): " + e.getMessage());
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

            // 3. Insertarlo de forma ordenada en tu ListaEnlazada en memoria
            listaInventario.insertarOrdenado(nuevoMaterial, Comparator.comparingInt(Material::getId));

            // ---> 3.1. REGISTRAR AUTOMÁTICAMENTE EN LA PILA GLOBAL DE HISTORIAL <---
            Accion nuevaAccion = new Accion(Accion.Tipo.ALTA, nuevoMaterial, 0, null, null);
            HistorialManager.agregarAccion(nuevaAccion);

            // 4. Actualizar la vista de la tabla
            actualizarVistaTabla();

            // 5. Limpiar los campos de texto
            txtId.clear();
            txtNombre.clear();
            txtCategoria.clear();
            txtExistencias.clear();

            System.out.println("Material agregado exitosamente a XAMPP y a la lista enlazada.");

        } catch (NumberFormatException e) {
            System.out.println("Error: El ID y las existencias deben ser números enteros.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error de validación: " + e.getMessage());
        }
    }
}