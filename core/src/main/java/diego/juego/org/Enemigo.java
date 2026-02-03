package diego.juego.org;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Enemigo {

    public float x, y, w, h;
    public float velocidadY = 180f;
    public float velocidadX = 0f;
    public final Rectangle limites;

    private float timerDisparo = 0f;
    private final float delayDisparo;

    private float timerCambioDir = 0f;
    private float tiempoCambioDir;

    protected int vida;

    public Enemigo(float x, float y, float w, float h, float delayDisparo) {
        this(x, y, w, h, delayDisparo, 2); // NORMAL = 2 impactos
    }

    public Enemigo(float x, float y, float w, float h, float delayDisparo, int vida) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.delayDisparo = delayDisparo;
        this.vida = vida;

        this.limites = new Rectangle(x, y, w, h);

        randomizarMovimientoLateral(true);
    }

    public void actualizar(float delta) {
        y -= velocidadY * delta;
        x += velocidadX * delta;

        timerCambioDir += delta;
        if (timerCambioDir >= tiempoCambioDir) {
            randomizarMovimientoLateral(false);
        }

        if (x < 0f) {
            x = 0f;
            velocidadX = Math.abs(velocidadX);
        } else if (x + w > Main.ANCHO_MUNDO) {
            x = Main.ANCHO_MUNDO - w;
            velocidadX = -Math.abs(velocidadX);
        }

        timerDisparo += delta;
        limites.setPosition(x, y);
    }

    private void randomizarMovimientoLateral(boolean primeraVez) {
        timerCambioDir = 0f;
        tiempoCambioDir = MathUtils.random(0.6f, 1.8f);

        float nuevaVelX = MathUtils.random(-260f, 260f);
        if (Math.abs(nuevaVelX) < 60f) {
            nuevaVelX = 60f * Math.signum(nuevaVelX == 0 ? 1 : nuevaVelX);
        }
        if (primeraVez) nuevaVelX *= 0.7f;

        velocidadX = nuevaVelX;
    }

    public boolean puedeDisparar() { return timerDisparo >= delayDisparo; }
    public void resetDisparo() { timerDisparo = 0f; }
    public boolean fueraPantalla() { return y + h < 0; }

    public void recibirImpacto() { vida--; }
    public boolean estaMuerto() { return vida <= 0; }
    public int getVida() { return vida; }

    public Texture getTextura(Recursos recursos) {
        return recursos.enemigoNormal;
    }
}
