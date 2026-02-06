package diego.juego.org.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import diego.juego.org.Main;

/**
 * Control multitouch: un dedo mueve en mitad izquierda; otros disparan en mitad derecha.
 */
public class ControlTouchpad {

    private int dedoMovimiento = -1;
    private boolean activo = false;

    private float baseX, baseY;
    private float knobX, knobY;

    private final float radioPad;
    private final float radioKnob;

    private float dx = 0f, dy = 0f;

    public ControlTouchpad(float radioPad, float radioKnob) {
        this.radioPad = radioPad;
        this.radioKnob = radioKnob;
    }

    public void reset() {
        activo = false;
        dedoMovimiento = -1;
        dx = dy = 0f;
    }

    public void actualizar(Viewport viewport) {
        // activar
        if (!activo) {
            for (int p = 0; p < 5; p++) {
                if (!Gdx.input.isTouched(p)) continue;

                Vector3 v = new Vector3(Gdx.input.getX(p), Gdx.input.getY(p), 0);
                viewport.unproject(v);

                if (v.x < Main.ANCHO_MUNDO * 0.5f) {
                    dedoMovimiento = p;
                    activo = true;

                    baseX = v.x;
                    baseY = v.y;
                    knobX = baseX;
                    knobY = baseY;

                    dx = dy = 0f;
                    break;
                }
            }
        }

        // actualizar
        if (activo) {
            if (dedoMovimiento >= 0 && Gdx.input.isTouched(dedoMovimiento)) {
                Vector3 v = new Vector3(Gdx.input.getX(dedoMovimiento), Gdx.input.getY(dedoMovimiento), 0);
                viewport.unproject(v);

                float vx = v.x - baseX;
                float vy = v.y - baseY;

                float len = (float) Math.sqrt(vx * vx + vy * vy);
                if (len > radioPad) {
                    float s = radioPad / len;
                    vx *= s;
                    vy *= s;
                }

                knobX = baseX + vx;
                knobY = baseY + vy;

                dx = vx / radioPad;
                dy = vy / radioPad;

            } else {
                reset();
            }
        }
    }

    public boolean estaActivo() { return activo; }
    public int getDedoMovimiento() { return dedoMovimiento; }

    public float getDx() { return dx; }
    public float getDy() { return dy; }

    public float getBaseX() { return baseX; }
    public float getBaseY() { return baseY; }
    public float getKnobX() { return knobX; }
    public float getKnobY() { return knobY; }

    public float getRadioPad() { return radioPad; }
    public float getRadioKnob() { return radioKnob; }
}
