package diego.juego.org;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

public class EscenaNivel2Intro implements Escena {

    private final Recursos recursos;
    private final Viewport viewport;
    private final GestorEscenas gestor;

    private final BitmapFont fuente = new BitmapFont();
    private final GlyphLayout layout = new GlyphLayout();

    public EscenaNivel2Intro(Recursos recursos, Viewport viewport, GestorEscenas gestor) {
        this.recursos = recursos;
        this.viewport = viewport;
        this.gestor = gestor;
    }

    @Override
    public void actualizar(float delta) {
        if (Gdx.input.justTouched()) {
            EstadoJuego.nivelActual = 2;
            gestor.cambiarA(new EscenaJuego(recursos, viewport, gestor));
        }
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        batch.setColor(0f, 0f, 0f, 0.75f);
        batch.draw(recursos.pixelBlanco, 0, 0, Main.ANCHO_MUNDO, Main.ALTO_MUNDO);
        batch.setColor(1f, 1f, 1f, 1f);

        fuente.getData().setScale(7f);
        dibujarCentrado(batch, "NIVEL 2", 1450f);

        fuente.getData().setScale(2.5f);
        dibujarCentrado(batch, "Prepárate...", 1200f);
        dibujarCentrado(batch, "Toca para continuar", 950f);

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
