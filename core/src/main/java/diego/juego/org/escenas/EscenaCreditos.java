package diego.juego.org.escenas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

import diego.juego.org.Escena;
import diego.juego.org.GestorEscenas;
import diego.juego.org.Main;
import diego.juego.org.recursos.Recursos;

public class EscenaCreditos implements Escena {

    private final Recursos recursos;
    private final Viewport viewport;
    private final GestorEscenas gestor;

    private final BitmapFont fuenteTitulo;
    private final BitmapFont fuenteCuerpo;
    private final GlyphLayout layout;

    public EscenaCreditos(Recursos recursos, Viewport viewport, GestorEscenas gestor) {
        this.recursos = recursos;
        this.viewport = viewport;
        this.gestor = gestor;

        this.fuenteTitulo = recursos.fuentes.getTitulo();
        this.fuenteCuerpo = recursos.fuentes.getNormal();
        this.layout = new GlyphLayout();
    }

    @Override
    public void alMostrar() {
        if (recursos.textos != null) recursos.textos.recargar();
    }

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

        dibujarTextoCentrado(batch, fuenteTitulo, recursos.textos.t("credits_title"), Main.ANCHO_MUNDO / 2f, 1450f);

        float maxAncho = Main.ANCHO_MUNDO * 0.85f;
        dibujarMultilineaCentrada(batch, fuenteCuerpo, recursos.textos.t("credits_body"), Main.ANCHO_MUNDO / 2f, 1200f, maxAncho);
    }

    private void dibujarTextoCentrado(SpriteBatch batch, BitmapFont font, String texto, float centroX, float y) {
        layout.setText(font, texto);
        float x = centroX - layout.width / 2f;
        font.draw(batch, layout, x, y);
    }

    private void dibujarMultilineaCentrada(SpriteBatch batch, BitmapFont font, String texto, float centroX, float yTop, float maxAncho) {
        layout.setText(font, texto, Color.WHITE, maxAncho, Align.center, true);

        float x = centroX - maxAncho / 2f;
        float y = yTop;
        font.draw(batch, layout, x, y);
    }

    @Override
    public void alRedimensionar(int ancho, int alto) {
        viewport.update(ancho, alto, true);
    }

    @Override public void alOcultar() { }

    @Override
    public void liberar() {
        // No liberar fuentes aquí (son compartidas y las libera Recursos.liberar()).
    }
}
