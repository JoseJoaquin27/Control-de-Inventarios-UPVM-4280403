package structures;

import java.util.function.Consumer;

// Pila con funcionamiento LIFO
public class Pila<T> {

    // Último elemento agregado
    private Nodo<T> cima;

    // Cantidad de elementos
    private int tamanio;

    // Agrega un elemento a la cima
    public void apilar(T dato) {

        if (dato == null) {
            throw new IllegalArgumentException(
                    "Dato nulo");
        }

        Nodo<T> nuevo = new Nodo<>(dato);

        nuevo.setSiguiente(cima);
        cima = nuevo;

        tamanio++;
    }

    // Retira el último elemento
    public T desapilar() {

        if (cima == null) {
            return null;
        }

        T dato = cima.getDato();
        cima = cima.getSiguiente();

        tamanio--;
        return dato;
    }

    // Consulta la cima
    public T verCima() {
        return cima == null
                ? null : cima.getDato();
    }

    // Comprueba si está vacía
    public boolean estaVacia() {
        return cima == null;
    }

    // Devuelve el tamaño
    public int getTamanio() {
        return tamanio;
    }

    // Recorre la pila desde la cima hasta el fondo sin vaciarla (para la vista de la tabla)
    public void recorrer(Consumer<T> accion) {
        if (accion == null) return;
        Nodo<T> actual = cima;
        while (actual != null) {
            accion.accept(actual.getDato());
            actual = actual.getSiguiente();
        }
    }
}