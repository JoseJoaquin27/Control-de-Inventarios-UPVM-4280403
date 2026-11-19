package structures;

import database.DatabaseConnection;
import model.Accion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

public class HistorialManager {
    private static final Pila<Accion> pilaGlobal = new Pila<>();
    private static boolean cargadoDesdeBd = false;

    public static void agregarAccion(Accion accion) {
        agregarAccion(accion, true);
    }

    public static void agregarAccion(Accion accion, boolean guardarEnBd) {
        if (accion == null) return;
        pilaGlobal.apilar(accion);
        if (guardarEnBd) {
            insertarEnHistorial(accion);
        }
    }

    public static Pila<Accion> getPilaGlobal() {
        if (!cargadoDesdeBd) {
            cargarDesdeBd();
        }
        return pilaGlobal;
    }

    public static void cargarDesdeBd() {
        cargadoDesdeBd = true;
        String sql = "SELECT tipo, detalle, id_referencia FROM historial ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String tipoStr = rs.getString("tipo");
                String detalle = rs.getString("detalle");
                try {
                    Accion.Tipo tipo = Accion.Tipo.valueOf(tipoStr);
                    pilaGlobal.apilar(new Accion(tipo, detalle));
                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void insertarEnHistorial(Accion accion) {
        String sql = "INSERT INTO historial (tipo, detalle, id_referencia) VALUES (?, ?, ?)";
        Integer ref = null;
        if (accion.getMaterial() != null) ref = accion.getMaterial().getId();
        else if (accion.getPrestamo() != null) ref = accion.getPrestamo().getFolio();
        else if (accion.getSolicitud() != null) ref = accion.getSolicitud().getFolio();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accion.getTipo().name());
            pstmt.setString(2, accion.getDetalle());
            if (ref != null) {
                pstmt.setInt(3, ref);
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void eliminarUltimoDeBd() {
        String sql = "DELETE FROM historial ORDER BY id DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}