package model;

// Representa una solicitud
public class Solicitud {

    // Tipos de solicitudes
    public enum Tipo {
        PRESTAMO,
        TRAMITE
    }

    // Información de la solicitud
    private final int folio;
    private final Tipo tipo;
    private final String estudiante;
    private final String descripcion;
    private final int materialId;
    private final int cantidad;

    // Crea una solicitud
    public Solicitud(int folio, Tipo tipo,
                     String estudiante, String descripcion,
                     int materialId, int cantidad) {

        this.folio = folio;
        this.tipo = tipo;
        this.estudiante = estudiante;
        this.descripcion = descripcion;
        this.materialId = materialId;
        this.cantidad = cantidad;
    }

    // Obtiene el folio
    public int getFolio() {
        return folio;
    }

    // Obtiene el tipo
    public Tipo getTipo() {
        return tipo;
    }

    // Obtiene el estudiante
    public String getEstudiante() {
        return estudiante;
    }

    // Obtiene la descripción
    public String getDescripcion() {
        return descripcion;
    }

    // Obtiene el ID del material
    public int getMaterialId() {
        return materialId;
    }

    // Obtiene la cantidad
    public int getCantidad() {
        return cantidad;
    }

    // Muestra los datos de la solicitud
    @Override
    public String toString() {
        return "Folio " + folio +
                " | " + tipo +
                " | " + estudiante +
                " | " + descripcion;
    }
}
