package model;

// Representa un préstamo activo
public class Prestamo {

    // Datos del préstamo
    private final int folio;
    private final int materialId;
    private final int cantidad;
    private final String estudiante;

    // Crea el préstamo
    public Prestamo(int folio, int materialId,
                    int cantidad, String estudiante) {

        this.folio = folio;
        this.materialId = materialId;
        this.cantidad = cantidad;
        this.estudiante = estudiante;
    }

    // Obtiene el folio
    public int getFolio() {
        return folio;
    }

    // Obtiene el material
    public int getMaterialId() {
        return materialId;
    }

    // Obtiene la cantidad
    public int getCantidad() {
        return cantidad;
    }

    // Obtiene el estudiante
    public String getEstudiante() {
        return estudiante;
    }

    // Muestra los datos del préstamo
    @Override
    public String toString() {
        return "Folio " + folio +
                " | Material " + materialId +
                " | Cantidad " + cantidad +
                " | " + estudiante;
    }
}
