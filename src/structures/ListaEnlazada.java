package structures;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Predicate;

// Administra elementos mediante nodos
public class ListaEnlazada<T> {

    // Primer nodo de la lista
    private Nodo<T> cabeza;

    // Número de elementos
    private int tamanio;

    // Inserta elementos en orden
    public void insertarOrdenado(
            T dato, Comparator<T> comparador) {

        // Valida los datos
        if (dato == null || comparador == null) {
            throw new IllegalArgumentException(
                    "Dato o comparador nulo");
        }

        // Crea el nodo
        Nodo<T> nuevo = new Nodo<>(dato);

        // Inserta al inicio si corresponde
        if (cabeza == null ||
                comparador.compare(
                        dato, cabeza.getDato()) < 0) {

            nuevo.setSiguiente(cabeza);
            cabeza = nuevo;

        } else {

            // Busca la posición correcta
            Nodo<T> actual = cabeza;

            while (actual.getSiguiente() != null &&
                    comparador.compare(
                            actual.getSiguiente().getDato(),
                            dato) <= 0) {

                actual = actual.getSiguiente();
            }

            // Enlaza el nuevo nodo
            nuevo.setSiguiente(
                    actual.getSiguiente());

            actual.setSiguiente(nuevo);
        }

        tamanio++;
    }

    // Busca un elemento
    public T buscar(Predicate<T> criterio) {

        Nodo<T> actual = cabeza;

        // Recorre la lista
        while (actual != null) {

            if (criterio.test(actual.getDato())) {
                return actual.getDato();
            }

            actual = actual.getSiguiente();
        }

        return null;
    }

    // Elimina un elemento
    public T eliminar(Predicate<T> criterio) {

        Nodo<T> anterior = null;
        Nodo<T> actual = cabeza;

        while (actual != null) {

            // Comprueba si coincide
            if (criterio.test(actual.getDato())) {

                if (anterior == null) {
                    cabeza = actual.getSiguiente();
                } else {
                    anterior.setSiguiente(
                            actual.getSiguiente());
                }

                tamanio--;
                return actual.getDato();
            }

            anterior = actual;
            actual = actual.getSiguiente();
        }

        return null;
    }

    // Recorre todos los elementos
    public void recorrer(Consumer<T> accion) {

        Nodo<T> actual = cabeza;

        while (actual != null) {
            accion.accept(actual.getDato());
            actual = actual.getSiguiente();
        }
    }

    // Devuelve el tamaño
    public int getTamanio() {
        return tamanio;
    }
}
