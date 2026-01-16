package diego.juego.org;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Escena {
    void alMostrar();
    void actualizar(float delta);
    void dibujar(SpriteBatch batch);
    void alRedimensionar(int ancho, int alto);
    void alOcultar();
    void liberar();
}
