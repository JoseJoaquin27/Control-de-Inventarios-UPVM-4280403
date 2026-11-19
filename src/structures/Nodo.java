package structures;

// Representa un nodo de una estructura enlazada
public class Nodo<T> {

    // Guarda el dato
    private T dato;

    // Apunta al siguiente nodo
    private Nodo<T> siguiente;

    // Crea un nodo
    public Nodo(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    // Obtiene el dato
    public T getDato() {
        return dato;
    }

    // Obtiene el siguiente nodo
    public Nodo<T> getSiguiente() {
        return siguiente;
    }

    // Conecta con otro nodo
    public void setSiguiente(Nodo<T> siguiente) {
        this.siguiente = siguiente;
    }
}

