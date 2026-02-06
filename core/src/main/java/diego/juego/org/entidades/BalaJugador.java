package diego.juego.org.entidades;

import com.badlogic.gdx.math.Rectangle;

/** Bala del jugador (sube). */
public class BalaJugador {
    public float x, y, w, h;
    public float velocidad = 1500f;
    public final Rectangle limites;

    public BalaJugador(float x, float y, float w, float h) {
        this.x = x; this.y = y; this.w = w; this.h = h;
        this.limites = new Rectangle(x, y, w, h);
    }

    public void actualizar(float delta) {
        y += velocidad * delta;
        limites.setPosition(x, y);
    }
}
