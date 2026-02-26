package diego.juego.org.escenas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

import diego.juego.org.Escena;
import diego.juego.org.estado.EstadoJuego;
import diego.juego.org.GestorEscenas;
import diego.juego.org.Main;
import diego.juego.org.recursos.Recursos;

public class EscenaNivel2Intro implements Escena {

    private final Recursos recursos;
    private final Viewport viewport;
    private final GestorEscenas gestor;

    private final BitmapFont fuenteTitulo;
    private final BitmapFont fuenteNormal;
    private final GlyphLayout layout;

    public EscenaNivel2Intro(Recursos recursos, Viewport viewport, GestorEscenas gestor) {
        this.recursos = recursos;
        this.viewport = viewport;
        this.gestor = gestor;

        this.fuenteTitulo = recursos.fuentes.getTituloPeque();
        this.fuenteNormal = recursos.fuentes.getNormal();
        this.layout = new GlyphLayout();
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

        dibujarCentrado(batch, fuenteTitulo, recursos.textos.t("level2_title"), 1450f);
        dibujarCentrado(batch, fuenteNormal, recursos.textos.t("level2_ready"), 1200f);
        dibujarCentrado(batch, fuenteNormal, recursos.textos.t("level2_tap"), 950f);
    }

    private void dibujarCentrado(SpriteBatch batch, BitmapFont font, String txt, float y) {
        layout.setText(font, txt);
        float x = (Main.ANCHO_MUNDO - layout.width) / 2f;
        font.draw(batch, layout, x, y);
    }

    @Override public void alMostrar() {}
    @Override public void alRedimensionar(int ancho, int alto) { viewport.update(ancho, alto, true); }
    @Override public void alOcultar() {}

    @Override
    public void liberar() {
        // No liberar fuentes aquí (son compartidas).
    }
}
