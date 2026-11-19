package controller;

import model.*;
import structures.*;

import java.util.Comparator;
import java.util.function.Consumer;

// Controla las operaciones del sistema
public class SistemaController {

    // Lista de materiales
    private final ListaEnlazada<Material> inventario =
            new ListaEnlazada<>();

    // Lista de préstamos activos
    private final ListaEnlazada<Prestamo> prestamos =
            new ListaEnlazada<>();

    // Cola de solicitudes FIFO
    private final Cola<Solicitud> solicitudes =
            new Cola<>();

    // Pila de acciones reversibles
    private final Pila<Accion> historial =
            new Pila<>();

    // Generador de folios
    private int siguienteFolio = 1;

    // Ordena materiales por ID
    private static final Comparator<Material> POR_ID =
            Comparator.comparingInt(Material::getId);

    // Busca un material obligatorio
    private Material exigirMaterial(int id) {
        Material m = buscarMaterial(id);

        if (m == null) {
            throw new IllegalArgumentException(
                    "Material inexistente: " + id);
        }

        return m;
    }

    // Busca un material por ID
    public Material buscarMaterial(int id) {
        return inventario.buscar(
                m -> m.getId() == id);
    }

    // Cuenta materiales
    public int totalMateriales() {
        return inventario.getTamanio();
    }

    // Cuenta préstamos activos
    public int totalPrestamos() {
        return prestamos.getTamanio();
    }

    // Cuenta solicitudes pendientes
    public int pendientes() {
        return solicitudes.getTamanio();
    }

    // Consulta el siguiente turno
    public Solicitud verSiguiente() {
        return solicitudes.verFrente();
    }

    // Consulta la última acción
    public Accion ultimaAccion() {
        return historial.verCima();
    }

    // Recorre el inventario
    public void recorrerMateriales(
            Consumer<Material> accion) {
        inventario.recorrer(accion);
    }

    // Recorre los préstamos
    public void recorrerPrestamos(
            Consumer<Prestamo> accion) {
        prestamos.recorrer(accion);
    }

    // Recorre las solicitudes
    public void recorrerSolicitudes(
            Consumer<Solicitud> accion) {
        solicitudes.recorrer(accion);
    }

    // Registra un material nuevo
    public void registrarMaterial(
            int id, String nombre,
            String categoria, int cantidad) {

        // Evita IDs repetidos
        if (buscarMaterial(id) != null) {
            throw new IllegalArgumentException(
                    "ID duplicado");
        }

        Material m = new Material(
                id, nombre, categoria, cantidad);

        // Inserta en la lista
        inventario.insertarOrdenado(m, POR_ID);

        // Guarda la acción
        historial.apilar(new Accion(
                Accion.Tipo.ALTA,
                m, 0, null, null));
    }

    // Elimina un material
    public void bajaMaterial(int id) {

        Material m = exigirMaterial(id);

        // Revisa préstamos activos
        boolean prestamoActivo =
                prestamos.buscar(
                        p -> p.getMaterialId() == id)
                        != null;

        // Revisa solicitudes pendientes
        boolean solicitudPendiente =
                solicitudes.contiene(
                        s -> s.getTipo() ==
                                Solicitud.Tipo.PRESTAMO
                                && s.getMaterialId() == id);

        // Impide eliminar materiales comprometidos
        if (prestamoActivo || solicitudPendiente) {
            throw new IllegalArgumentException(
                    "Hay prestamos activos o " +
                            "solicitudes pendientes");
        }

        inventario.eliminar(
                x -> x.getId() == id);

        // Guarda la baja para deshacer
        historial.apilar(new Accion(
                Accion.Tipo.BAJA,
                m, 0, null, null));
    }

    // Suma o resta existencias
    public void ajustarExistencias(
            int id, int diferencia) {

        Material m = exigirMaterial(id);

        long total =
                (long) m.getExistencias() + diferencia;

        // Evita existencias inválidas
        if (total < 0 || total > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "Stock fuera de rango");
        }

        int anterior = m.getExistencias();

        m.setExistencias((int) total);

        // Guarda el stock anterior
        historial.apilar(new Accion(
                Accion.Tipo.AJUSTE,
                m, anterior, null, null));
    }

    // Registra una solicitud de préstamo
    public int solicitarPrestamo(
            String estudiante, int id, int cantidad) {

        // Valida los datos
        if (estudiante == null ||
                estudiante.isBlank() || cantidad <= 0) {
            throw new IllegalArgumentException(
                    "Estudiante o cantidad invalida");
        }

        exigirMaterial(id);

        int folio = siguienteFolio;

        // Agrega la solicitud a la cola
        solicitudes.encolar(new Solicitud(
                folio,
                Solicitud.Tipo.PRESTAMO,
                estudiante.trim(),
                "Prestamo de material " + id,
                id,
                cantidad));

        siguienteFolio++;

        return folio;
    }

    // Registra un trámite escolar
    public int solicitarTramite(
            String estudiante, String tramite) {

        if (estudiante == null ||
                estudiante.isBlank() ||
                tramite == null || tramite.isBlank()) {

            throw new IllegalArgumentException(
                    "Estudiante o tramite invalido");
        }

        int folio = siguienteFolio;

        // Encola el trámite
        solicitudes.encolar(new Solicitud(
                folio,
                Solicitud.Tipo.TRAMITE,
                estudiante.trim(),
                tramite.trim(),
                0,
                0));

        siguienteFolio++;

        return folio;
    }

    // Atiende la primera solicitud
    public String atenderSiguiente() {

        Solicitud s = solicitudes.verFrente();

        // Comprueba si hay solicitudes
        if (s == null) {
            return "No hay solicitudes pendientes";
        }

        // Atiende un trámite escolar
        if (s.getTipo() == Solicitud.Tipo.TRAMITE) {

            solicitudes.desencolar();

            historial.apilar(new Accion(
                    Accion.Tipo.ATENDER_TRAMITE,
                    null, 0, null, s));

            return "Tramite atendido: " + s;
        }

        Material m = exigirMaterial(
                s.getMaterialId());

        // Verifica existencias disponibles
        if (m.getExistencias() < s.getCantidad()) {
            return "Stock insuficiente para folio "
                    + s.getFolio()
                    + ". Sigue primero en la cola.";
        }

        int anterior = m.getExistencias();

        // Crea el préstamo activo
        Prestamo p = new Prestamo(
                s.getFolio(),
                s.getMaterialId(),
                s.getCantidad(),
                s.getEstudiante());

        // Descuenta las existencias
        m.setExistencias(
                anterior - s.getCantidad());

        // Registra el préstamo
        prestamos.insertarOrdenado(
                p,
                Comparator.comparingInt(
                        Prestamo::getFolio));

        // Retira la solicitud atendida
        solicitudes.desencolar();

        // Guarda la operación
        historial.apilar(new Accion(
                Accion.Tipo.ATENDER_PRESTAMO,
                m, anterior, p, s));

        return "Prestamo entregado: " + p;
    }

    // Registra la devolución de un préstamo
    public void devolver(int folio) {

        Prestamo p = prestamos.buscar(
                x -> x.getFolio() == folio);

        // Comprueba que exista
        if (p == null) {
            throw new IllegalArgumentException(
                    "Prestamo inexistente o ya devuelto");
        }

        Material m = exigirMaterial(
                p.getMaterialId());

        long total = (long) m.getExistencias()
                + p.getCantidad();

        if (total > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "Stock fuera de rango");
        }

        int anterior = m.getExistencias();

        // Devuelve las existencias
        m.setExistencias((int) total);

        // Retira el préstamo activo
        prestamos.eliminar(
                x -> x.getFolio() == folio);

        // Guarda la devolución
        historial.apilar(new Accion(
                Accion.Tipo.DEVOLUCION,
                m, anterior, p, null));
    }

    // Revierte la última operación
    public String deshacer() {

        Accion a = historial.verCima();

        if (a == null) {
            return "No hay operaciones para deshacer";
        }

        Material m = a.getMaterial();

        switch (a.getTipo()) {

            // Revierte el registro
            case ALTA -> {

                boolean activo = prestamos.buscar(
                        p -> p.getMaterialId() ==
                                m.getId()) != null;

                boolean pendiente =
                        solicitudes.contiene(
                                s -> s.getTipo() ==
                                        Solicitud.Tipo.PRESTAMO
                                        && s.getMaterialId()
                                        == m.getId());

                if (activo || pendiente) {
                    return "No se puede revertir el alta: "
                            + "hay prestamos o solicitudes";
                }

                inventario.eliminar(
                        x -> x.getId() == m.getId());
            }

            // Recupera el material eliminado
            case BAJA -> {
                inventario.insertarOrdenado(m, POR_ID);
            }

            // Recupera el stock anterior
            case AJUSTE -> {
                m.setExistencias(
                        a.getStockAnterior());
            }

            // Revierte la entrega del préstamo
            case ATENDER_PRESTAMO -> {

                Prestamo p = a.getPrestamo();

                if (prestamos.buscar(
                        x -> x.getFolio() ==
                                p.getFolio()) == null) {
                    return "Prestamo no activo";
                }

                prestamos.eliminar(
                        x -> x.getFolio() ==
                                p.getFolio());

                m.setExistencias(
                        a.getStockAnterior());

                // Restaura el turno
                solicitudes.encolarFrente(
                        a.getSolicitud());
            }

            // Restaura el trámite pendiente
            case ATENDER_TRAMITE -> {
                solicitudes.encolarFrente(
                        a.getSolicitud());
            }

            // Revierte la devolución
            case DEVOLUCION -> {

                Prestamo p = a.getPrestamo();

                if (prestamos.buscar(
                        x -> x.getFolio() ==
                                p.getFolio()) != null) {
                    return "Prestamo duplicado";
                }

                prestamos.insertarOrdenado(
                        p,
                        Comparator.comparingInt(
                                Prestamo::getFolio));

                m.setExistencias(
                        a.getStockAnterior());
            }
        }

        // Retira la acción revertida
        historial.desapilar();

        return "Se revirtio: " + a.getTipo();
    }
}
