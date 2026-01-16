package diego.juego.org;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

/** Enemigo básico que baja y se mueve lateralmente de forma aleatoria. */
public class Enemigo {

    public float x, y, w, h;
    public float velocidadY = 180f;       // baja
    public float velocidadX = 0f;         // lateral (random)
    public final Rectangle limites;

    private float timerDisparo = 0f;
    private final float delayDisparo;

    // --- movimiento lateral random ---
    private float timerCambioDir = 0f;
    private float tiempoCambioDir;        // cada cuánto cambia (random)

    public Enemigo(float x, float y, float w, float h, float delayDisparo) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.delayDisparo = delayDisparo;
        this.limites = new Rectangle(x, y, w, h);

        // inicializar movimiento lateral aleatorio
        randomizarMovimientoLateral(true);
    }

    /** Actualiza movimiento, temporizadores y límites. */
    public void actualizar(float delta) {
        // bajar siempre
        y -= velocidadY * delta;

        // movimiento lateral
        x += velocidadX * delta;

        // cambiar dirección de vez en cuando
        timerCambioDir += delta;
        if (timerCambioDir >= tiempoCambioDir) {
            randomizarMovimientoLateral(false);
        }

        // rebotar en bordes
        if (x < 0f) {
            x = 0f;
            velocidadX = Math.abs(velocidadX);
        } else if (x + w > Main.ANCHO_MUNDO) {
            x = Main.ANCHO_MUNDO - w;
            velocidadX = -Math.abs(velocidadX);
        }

        // disparo
        timerDisparo += delta;

        // límites
        limites.setPosition(x, y);
    }

    private void randomizarMovimientoLateral(boolean primeraVez) {
        timerCambioDir = 0f;

        // cada 0.6 a 1.8s cambia (ajústalo)
        tiempoCambioDir = MathUtils.random(0.6f, 1.8f);

        // velocidad lateral aleatoria (-220..220 aprox). Si quieres más loco, sube el rango.
        float nuevaVelX = MathUtils.random(-260f, 260f);

        // evitar que se quede casi quieto lateralmente
        if (Math.abs(nuevaVelX) < 60f) {
            nuevaVelX = 60f * Math.signum(nuevaVelX == 0 ? 1 : nuevaVelX);
        }

        // primera vez: un pelín menos brusco para que no parezca raro
        if (primeraVez) nuevaVelX *= 0.7f;

        velocidadX = nuevaVelX;
    }

    public boolean puedeDisparar() { return timerDisparo >= delayDisparo; }
    public void resetDisparo() { timerDisparo = 0f; }
    public boolean fueraPantalla() { return y + h < 0; }
}
