package diego.juego.org;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

public class EscenaNuevoRecord implements Escena {

    private final Recursos recursos;
    private final Viewport viewport;
    private final GestorEscenas gestor;
    private final int score;
    private final Runnable alTerminar; // qué hacer después (volver menú / nivel2 / etc)

    private final BitmapFont fuente = new BitmapFont();
    private final GlyphLayout layout = new GlyphLayout();

    private boolean dialogoLanzado = false;

    public EscenaNuevoRecord(Recursos recursos, Viewport viewport, GestorEscenas gestor, int score, Runnable alTerminar) {
        this.recursos = recursos;
        this.viewport = viewport;
        this.gestor = gestor;
        this.score = score;
        this.alTerminar = alTerminar;
    }

    @Override public void alMostrar() {}

    @Override
    public void actualizar(float delta) {
        // BACK/ESC -> cancelar y seguir
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (alTerminar != null) alTerminar.run();
            return;
        }

        // lanzamos una vez el diálogo nativo
        if (!dialogoLanzado) {
            dialogoLanzado = true;

            Gdx.input.getTextInput(new TextInputListener() {
                @Override
                public void input(String text) {
                    EstadoJuego.insertarEnTop10(score, text);
                    if (alTerminar != null) alTerminar.run();
                }

                @Override
                public void canceled() {
                    // si cancela, guardamos con "---" o no guardamos; yo lo guardo igual
                    EstadoJuego.insertarEnTop10(score, "---");
                    if (alTerminar != null) alTerminar.run();
                }
            }, "NUEVO RÉCORD", "AAA", "3 iniciales");
        }
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        batch.setColor(0f, 0f, 0f, 0.75f);
        batch.draw(recursos.pixelBlanco, 0, 0, Main.ANCHO_MUNDO, Main.ALTO_MUNDO);
        batch.setColor(1f, 1f, 1f, 1f);

        fuente.getData().setScale(6f);
        dibujarCentrado(batch, "¡NUEVO RÉCORD!", 1500f);

        fuente.getData().setScale(3.2f);
        dibujarCentrado(batch, "Puntuación: " + score, 1300f);

        fuente.getData().setScale(2.0f);
        dibujarCentrado(batch, "Escribe tus 3 iniciales", 1050f);
        dibujarCentrado(batch, "(se abrirá un cuadro de texto)", 950f);

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
