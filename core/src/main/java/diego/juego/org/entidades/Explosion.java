package diego.juego.org.entidades;

/**
 * Explosión temporal con fade-out y efecto pop.
 */
public class Explosion {
    public float x, y;
    public float size;

    private float time = 0f;
    private final float duration = 0.55f;

    public Explosion(float x, float y, float size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public void actualizar(float delta) { time += delta; }
    public boolean terminada() { return time >= duration; }

    public float alpha() {
        float t = time / duration;
        return 1f - t;
    }

    public float escalaPop() {
        float t = time / duration;
        return 0.8f + (t * 0.7f);
    }
}
