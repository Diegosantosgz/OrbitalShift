package diego.juego.org.escenas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

import diego.juego.org.Escena;
import diego.juego.org.GestorEscenas;
import diego.juego.org.Main;
import diego.juego.org.recursos.Recursos;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;

public class EscenaCreditos implements Escena {

    private final Recursos recursos;
    private final Viewport viewport;
    private final GestorEscenas gestor;

    private final BitmapFont fuente = new BitmapFont();
    private final GlyphLayout layout = new GlyphLayout();

    public EscenaCreditos(Recursos recursos, Viewport viewport, GestorEscenas gestor) {
        this.recursos = recursos;
        this.viewport = viewport;
        this.gestor = gestor;
    }

    @Override public void alMostrar() { }

    @Override
    public void actualizar(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)
            || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)
            || Gdx.input.justTouched()) {

            gestor.cambiarA(new EscenaMenu(recursos, viewport, gestor));
        }
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        batch.setColor(0f, 0f, 0f, 0.65f);
        batch.draw(recursos.pixelBlanco, 0, 0, Main.ANCHO_MUNDO, Main.ALTO_MUNDO);
        batch.setColor(1f, 1f, 1f, 1f);

        fuente.getData().setScale(5.5f);
        dibujarCentradoTitulo(batch, recursos.textos.t("credits_title"), Main.ANCHO_MUNDO / 2f, 1450f);

        fuente.getData().setScale(2.2f);

// ancho del bloque de texto (ajusta si quieres más/menos margen)
        float maxAncho = Main.ANCHO_MUNDO * 0.85f;

// “yTop” donde empieza el bloque (bájalo/subelo a gusto)
        dibujarCentradoMultilinea(batch, recursos.textos.t("credits_body"), Main.ANCHO_MUNDO / 2f, 1200f, maxAncho);


        fuente.getData().setScale(1.0f);
    }

    private void dibujarCentradoTitulo(SpriteBatch batch, String texto, float centroX, float y) {
        layout.setText(fuente, texto);
        float x = centroX - layout.width / 2f;
        fuente.draw(batch, layout, x, y);
    }

    private void dibujarCentradoMultilinea(SpriteBatch batch, String texto, float centroX, float yTop, float maxAncho) {
        // wrap = true + Align.center => respeta \n y además parte líneas si son muy largas
        layout.setText(fuente, texto, com.badlogic.gdx.graphics.Color.WHITE, maxAncho,
            com.badlogic.gdx.utils.Align.center, true);

        // yTop es “arriba”; font.draw usa baseline, así que bajamos con layout.height
        float x = centroX - maxAncho / 2f;
        float y = yTop;
        fuente.draw(batch, layout, x, y);
    }

    @Override public void alRedimensionar(int ancho, int alto) { viewport.update(ancho,alto, true); }
    @Override public void alOcultar() { }
    @Override public void liberar() { fuente.dispose(); }
}
