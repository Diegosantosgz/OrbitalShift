package diego.juego.org.entidades;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public final class BossNivel2 {

    private static final int VIDA_MAX = 200;

    private final Rectangle limites;
    private final Array<BalaBoss> bufferBalas = new Array<>();

    private int vida = VIDA_MAX;
    private boolean activo = true;

    private float timerRafaga = 0f;
    private float intervaloRafaga = 0.85f;

    // ===== ENTRADA DEL BOSS (NUEVO) =====
    private boolean entrando = true;
    private float targetY;           // y final donde se queda (la de antes)
    private float velEntrada = 220f; // velocidad de bajada (ajústala si quieres)

    public BossNivel2(float mundoAncho, float mundoAlto) {
        float w = mundoAncho * 0.92f;
        float h = mundoAlto * 0.26f;

        float x = (mundoAncho - w) / 2f;

        // La Y final EXACTA que ya tenías
        targetY = mundoAlto - h - (mundoAlto * 0.02f);

        // Empieza fuera de pantalla por arriba (sin cambiar tamaño ni nada)
        float startY = mundoAlto + h + 40f;

        limites = new Rectangle(x, startY, w, h);
    }

    public void actualizar(float delta) {
        if (!activo) return;

        // ===== mover hacia abajo poco a poco (NUEVO) =====
        if (entrando) {
            limites.y -= velEntrada * delta;
            if (limites.y <= targetY) {
                limites.y = targetY;
                entrando = false;
            }
        }

        // ===== TU DISPARO TAL CUAL =====
        timerRafaga += delta;
        if (timerRafaga >= intervaloRafaga) {
            timerRafaga = 0f;
            generarRafaga();
        }
    }

    private void generarRafaga() {
        int cantidad = MathUtils.random(3, 5);

        // Tamaño de las balas del Boss
        float balaW = 64f;
        float balaH = 110f;

        for (int i = 0; i < cantidad; i++) {
            float spawnX = MathUtils.random(limites.x, limites.x + limites.width - balaW);
            float spawnY = limites.y;

            float angulo = 270f + MathUtils.random(-22f, 22f);
            // Velocidad de las balas del Boss
            float velocidad = MathUtils.random(350f, 600f);

            float vx = MathUtils.cosDeg(angulo) * velocidad;
            float vy = MathUtils.sinDeg(angulo) * velocidad;

            bufferBalas.add(new BalaBoss(spawnX, spawnY, vx, vy, balaW, balaH));
        }
    }

    public Array<BalaBoss> extraerBalasGeneradas() {
        Array<BalaBoss> out = new Array<>(bufferBalas);
        bufferBalas.clear();
        return out;
    }

    public void recibirImpacto() {
        if (!activo) return;
        vida--;
        if (vida <= 0) {
            vida = 0;
            activo = false;
        }
    }

    public boolean estaActivo() {
        return activo;
    }

    public boolean estaMuerto() {
        return !activo && vida <= 0;
    }

    public int getVida() {
        return vida;
    }

    public int getVidaMax() {
        return VIDA_MAX;
    }

    public Rectangle getLimites() {
        return limites;
    }
}
