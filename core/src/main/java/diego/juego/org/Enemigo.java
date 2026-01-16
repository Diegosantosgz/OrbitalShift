package diego.juego.org;

import com.badlogic.gdx.math.Rectangle;

/** Enemigo básico que baja y dispara cada cierto tiempo. */
public class Enemigo {
    public float x, y, w, h;
    public float velocidad = 180f;
    public final Rectangle limites;

    private float timerDisparo = 0f;
    private final float delayDisparo;

    public Enemigo(float x, float y, float w, float h, float delayDisparo) {
        this.x = x; this.y = y; this.w = w; this.h = h;
        this.delayDisparo = delayDisparo;
        this.limites = new Rectangle(x, y, w, h);
    }

    public void actualizar(float delta) {
        y -= velocidad * delta;
        timerDisparo += delta;
        limites.setPosition(x, y);
    }

    public boolean puedeDisparar() { return timerDisparo >= delayDisparo; }
    public void resetDisparo() { timerDisparo = 0f; }
    public boolean fueraPantalla() { return y + h < 0; }
}
