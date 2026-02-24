package diego.juego.org.entidades;

import com.badlogic.gdx.math.Rectangle;

/** Bala enemiga (baja). */
/** Bala enemiga (baja). */
public class BalaEnemigo {
    public float x, y, w, h;
    private float vx;
    private float vy;
    public float velocidad = 900f;
    public final Rectangle limites;

    public BalaEnemigo(float x, float y, float w, float h) {
        this.x = x; this.y = y; this.w = w; this.h = h;

        //  IMPORTANTE: bala normal baja recta
        this.vx = 0f;
        this.vy = -velocidad;

        this.limites = new Rectangle(x, y, w, h);
    }

    public BalaEnemigo(float x, float y, float w, float h, float vx, float vy) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.vx = vx;
        this.vy = vy;
        this.limites = new Rectangle(x, y, w, h);
    }

    public void actualizar(float delta) {
        x += vx * delta;
        y += vy * delta;
        limites.setPosition(x, y);
    }

    public boolean fueraPantalla() {
        return y + h < 0;
    }
}
