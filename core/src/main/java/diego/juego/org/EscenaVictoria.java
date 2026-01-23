package diego.juego.org;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

public class EscenaVictoria implements Escena {

    private final Recursos recursos;
    private final Viewport viewport;
    private final GestorEscenas gestor;
    private final int puntuacion;

    private final BitmapFont fuente = new BitmapFont();
    private final GlyphLayout layout = new GlyphLayout();

    private float timer = 0f;

    public EscenaVictoria(Recursos recursos, Viewport viewport, GestorEscenas gestor, int puntuacion) {
        this.recursos = recursos;
        this.viewport = viewport;
        this.gestor = gestor;
        this.puntuacion = puntuacion;
    }

    @Override
    public void actualizar(float delta) {
        timer += delta;

        // Tras 2 segundos pasa a pantalla "Nivel 2"
        if (timer >= 2.0f) {
            gestor.cambiarA(new EscenaNivel2Intro(recursos, viewport, gestor));
        }
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        batch.setColor(0f, 0f, 0f, 0.7f);
        batch.draw(recursos.pixelBlanco, 0, 0, Main.ANCHO_MUNDO, Main.ALTO_MUNDO);
        batch.setColor(1f, 1f, 1f, 1f);

        fuente.getData().setScale(6f);
        dibujarCentrado(batch, "¡VICTORIA!", 1500f);

        fuente.getData().setScale(3f);
        dibujarCentrado(batch, "Puntuación: " + puntuacion, 1300f);

        fuente.getData().setScale(1.0f);
    }

    private void dibujarCentrado(SpriteBatch batch, String txt, float y) {
        layout.setText(fuente, txt);
        fuente.draw(batch, layout,
            (Main.ANCHO_MUNDO - layout.width) / 2f,
            y
        );
    }

    @Override public void alMostrar() {}
    @Override public void alRedimensionar(int a, int b) {}
    @Override public void alOcultar() {}
    @Override public void liberar() { fuente.dispose(); }
}
