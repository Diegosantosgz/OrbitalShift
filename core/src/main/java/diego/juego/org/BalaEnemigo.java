package diego.juego.org;

import com.badlogic.gdx.math.Rectangle;

/** Bala enemiga (baja). */
public class BalaEnemigo {
    public float x, y, w, h;
    public float velocidad = 900f;
    public final Rectangle limites;

    public BalaEnemigo(float x, float y, float w, float h) {
        this.x = x; this.y = y; this.w = w; this.h = h;
        this.limites = new Rectangle(x, y, w, h);
    }

    public void actualizar(float delta) {
        y -= velocidad * delta;
        limites.setPosition(x, y);
    }

    public boolean fueraPantalla() {
        return y + h < 0;
    }
}
