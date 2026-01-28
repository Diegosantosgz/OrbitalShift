package diego.juego.org;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

public class EscenaNuevoRecord implements Escena {

    private final Recursos recursos;
    private final Viewport viewport;
    private final GestorEscenas gestor;

    private final int score;
    private final Runnable onFinish;

    private final BitmapFont fuente = new BitmapFont();
    private final GlyphLayout layout = new GlyphLayout();

    private final char[] letras = new char[]{'A','A','A'};
    private int idx = 0;

    public EscenaNuevoRecord(Recursos recursos, Viewport viewport, GestorEscenas gestor, int score, Runnable onFinish) {
        this.recursos = recursos;
        this.viewport = viewport;
        this.gestor = gestor;
        this.score = score;
        this.onFinish = onFinish;
    }

    @Override public void alMostrar() {}

    @Override
    public void actualizar(float delta) {
        // Navegación “arcade” con teclas:
        // Letras A-Z: escribe
        // Backspace: borra
        // Enter: confirmar
        // Flechas: mover cursor (opcional)
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT))  idx = Math.max(0, idx - 1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) idx = Math.min(2, idx + 1);

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            letras[idx] = 'A';
            idx = Math.max(0, idx - 1);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_ENTER)) {
            confirmar();
            return;
        }

        // Captura de letras (A-Z)
        for (int key = Input.Keys.A; key <= Input.Keys.Z; key++) {
            if (Gdx.input.isKeyJustPressed(key)) {
                char c = (char) ('A' + (key - Input.Keys.A));
                letras[idx] = c;
                if (idx < 2) idx++;
                break;
            }
        }

        // En móvil es más cómodo permitir "TOCA para confirmar" cuando ya hay 3 letras
        if (Gdx.input.justTouched()) {
            // si quieres que toque confirme siempre:
            confirmar();
        }
    }

    private void confirmar() {
        String iniciales = "" + letras[0] + letras[1] + letras[2];
        EstadoJuego.insertarScore(iniciales, score);
        if (onFinish != null) onFinish.run();
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        batch.setColor(0f, 0f, 0f, 0.75f);
        batch.draw(recursos.pixelBlanco, 0, 0, Main.ANCHO_MUNDO, Main.ALTO_MUNDO);
        batch.setColor(1f, 1f, 1f, 1f);

        fuente.getData().setScale(5.5f);
        dibujarCentrado(batch, "¡NUEVO RÉCORD!", 1500f);

        fuente.getData().setScale(3.0f);
        dibujarCentrado(batch, "PUNTUACIÓN: " + score, 1340f);

        fuente.getData().setScale(6.0f);
        String texto = letras[0] + "  " + letras[1] + "  " + letras[2];
        dibujarCentrado(batch, texto, 1100f);

        // indicador del cursor
        fuente.getData().setScale(3.0f);
        String cursor = (idx == 0 ? "^" : " ") + "   " + (idx == 1 ? "^" : " ") + "   " + (idx == 2 ? "^" : " ");
        dibujarCentrado(batch, cursor, 1030f);

        fuente.getData().setScale(2.0f);
        dibujarCentrado(batch, "ESCRIBE 3 LETRAS (A-Z)", 860f);
        dibujarCentrado(batch, "ENTER o TOCA PARA CONFIRMAR", 780f);

        fuente.getData().setScale(1.0f);
    }

    private void dibujarCentrado(SpriteBatch batch, String txt, float y) {
        layout.setText(fuente, txt);
        fuente.draw(batch, layout, (Main.ANCHO_MUNDO - layout.width) / 2f, y);
    }

    @Override public void alRedimensionar(int a, int b) {}
    @Override public void alOcultar() {}
    @Override public void liberar() { fuente.dispose(); }
}
