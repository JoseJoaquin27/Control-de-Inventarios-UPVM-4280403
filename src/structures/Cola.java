package structures;

import java.util.function.Consumer;
import java.util.function.Predicate;

// Cola con funcionamiento FIFO
public class Cola<T> {

    // Primer elemento
    private Nodo<T> frente;

    // Último elemento
    private Nodo<T> fin;

    // Cantidad de elementos
    private int tamanio;

    // Agrega al final de la cola
    public void encolar(T dato) {

        if (dato == null) {
            throw new IllegalArgumentException(
                    "Dato nulo");
        }

        Nodo<T> nuevo = new Nodo<>(dato);

        if (fin == null) {
            frente = nuevo;
        } else {
            fin.setSiguiente(nuevo);
        }

        fin = nuevo;
        tamanio++;
    }

    // Agrega al inicio para restaurar un turno
    public void encolarFrente(T dato) {

        if (dato == null) {
            throw new IllegalArgumentException(
                    "Dato nulo");
        }

        Nodo<T> nuevo = new Nodo<>(dato);

        nuevo.setSiguiente(frente);
        frente = nuevo;

        if (fin == null) {
            fin = nuevo;
        }

        tamanio++;
    }

    // Retira el primer elemento
    public T desencolar() {

        if (frente == null) {
            return null;
        }

        T dato = frente.getDato();

        frente = frente.getSiguiente();

        // Actualiza el final si queda vacía
        if (frente == null) {
            fin = null;
        }

        tamanio--;
        return dato;
    }

    // Consulta el primer elemento
    public T verFrente() {
        return frente == null
                ? null : frente.getDato();
    }

    // Comprueba si está vacía
    public boolean estaVacia() {
        return frente == null;
    }

    // Obtiene el tamaño
    public int getTamanio() {
        return tamanio;
    }

    // Recorre la cola
    public void recorrer(Consumer<T> accion) {

        for (Nodo<T> n = frente;
             n != null;
             n = n.getSiguiente()) {

            accion.accept(n.getDato());
        }
    }

    // Busca si existe un elemento
    public boolean contiene(Predicate<T> criterio) {

        for (Nodo<T> n = frente;
             n != null;
             n = n.getSiguiente()) {

            if (criterio.test(n.getDato())) {
                return true;
            }
        }

        return false;
    }
}
