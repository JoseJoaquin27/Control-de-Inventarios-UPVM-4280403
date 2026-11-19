package model;

// Representa un material del inventario
public class Material {

    // Datos del material
    private final int id;
    private String nombre;
    private String categoria;
    private int existencias;

    // Crea y valida un material
    public Material(int id, String nombre,
                    String categoria, int existencias) {

        // Comprueba que los datos sean válidos
        if (id <= 0 ||
                nombre == null || nombre.trim().isEmpty() ||
                categoria == null || categoria.trim().isEmpty() ||
                existencias < 0) {

            throw new IllegalArgumentException(
                    "Datos de material invalidos");
        }

        // Guarda los datos
        this.id = id;
        this.nombre = nombre.trim();
        this.categoria = categoria.trim();
        this.existencias = existencias;
    }

    // Obtiene el ID
    public int getId() {
        return id;
    }

    // Obtiene el nombre
    public String getNombre() {
        return nombre;
    }

    // Obtiene la categoría
    public String getCategoria() {
        return categoria;
    }

    // Obtiene las existencias
    public int getExistencias() {
        return existencias;
    }

    // Modifica el nombre
    public void setNombre(String nombre) {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Nombre invalido");
        }

        this.nombre = nombre.trim();
    }

    // Modifica la categoría
    public void setCategoria(String categoria) {

        if (categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Categoria invalida");
        }

        this.categoria = categoria.trim();
    }

    // Actualiza las existencias
    public void setExistencias(int cantidad) {

        if (cantidad < 0) {
            throw new IllegalArgumentException(
                    "Stock negativo");
        }

        this.existencias = cantidad;
    }

    // Muestra los datos del material
    @Override
    public String toString() {

        return String.format(
                "%-5d %-25s %-18s %5d",
                id, nombre, categoria, existencias);
    }
}

