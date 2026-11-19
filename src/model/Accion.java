package model;

public class Accion {

    public enum Tipo {
        ALTA,
        BAJA,
        AJUSTE,
        ATENDER_PRESTAMO,
        ATENDER_TRAMITE,
        DEVOLUCION
    }

    private final Tipo tipo;
    private final Material material;
    private final int stockAnterior;
    private final Prestamo prestamo;
    private final Solicitud solicitud;
    private final String detallePersistido;

    public Accion(Tipo tipo, Material material,
                  int stockAnterior, Prestamo prestamo,
                  Solicitud solicitud) {
        this.tipo = tipo;
        this.material = material;
        this.stockAnterior = stockAnterior;
        this.prestamo = prestamo;
        this.solicitud = solicitud;
        this.detallePersistido = null;
    }

    public Accion(Tipo tipo, String detallePersistido) {
        this.tipo = tipo;
        this.material = null;
        this.stockAnterior = 0;
        this.prestamo = null;
        this.solicitud = null;
        this.detallePersistido = detallePersistido;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public Material getMaterial() {
        return material;
    }

    public int getStockAnterior() {
        return stockAnterior;
    }

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public Solicitud getSolicitud() {
        return solicitud;
    }

    public String getDetalle() {
        if (detallePersistido != null && !detallePersistido.isBlank()) {
            return detallePersistido;
        }
        if (material != null) {
            return "Material agregado: " + material.getNombre() + " (ID: " + material.getId() + ")";
        } else if (prestamo != null) {
            return "Préstamo - Folio: " + prestamo.getFolio() + " | Estudiante: " + prestamo.getEstudiante();
        } else if (solicitud != null) {
            return "Folio " + solicitud.getFolio() + " | " + solicitud.getTipo() + " | " + solicitud.getEstudiante() + " | " + solicitud.getDescripcion();
        }
        return "Sin detalles adicionales";
    }

    @Override
    public String toString() {
        return tipo +
                (solicitud != null
                        ? " (folio " +
                        solicitud.getFolio() + ")"
                        : "");
    }
}