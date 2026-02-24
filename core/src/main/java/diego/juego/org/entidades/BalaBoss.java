package diego.juego.org.entidades;

import com.badlogic.gdx.math.Rectangle;

public final class BalaBoss {
    public float x, y;
    public float vx, vy;
    public float w, h;
    public final Rectangle limites;

    public BalaBoss(float x, float y, float vx, float vy, float w, float h) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.w = w;
        this.h = h;
        this.limites = new Rectangle(x, y, w, h);
    }

    public void actualizar(float delta) {
        x += vx * delta;
        y += vy * delta;
        limites.setPosition(x, y);
    }

    public boolean fueraPantalla(float anchoMundo, float altoMundo) {
        return (y + h < 0) || (y > altoMundo) || (x + w < 0) || (x > anchoMundo);
    }
}
