package diego.juego.org;

/**
 * Gestiona el cambio entre escenas de forma segura.
 */
public class GestorEscenas {

    private Escena escenaActual;

    public void cambiarA(Escena nueva) {
        if (escenaActual != null) {
            escenaActual.alOcultar();
            escenaActual.liberar();
        }
        escenaActual = nueva;
        if (escenaActual != null) escenaActual.alMostrar();
    }

    public Escena getEscenaActual() {
        return escenaActual;
    }

    public void liberar() {
        if (escenaActual != null) {
            escenaActual.alOcultar();
            escenaActual.liberar();
            escenaActual = null;
        }
    }
}
