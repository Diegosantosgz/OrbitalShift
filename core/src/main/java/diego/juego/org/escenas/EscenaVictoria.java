package diego.juego.org.escenas;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

import diego.juego.org.Escena;
import diego.juego.org.GestorEscenas;
import diego.juego.org.Main;
import diego.juego.org.recursos.Recursos;

public class EscenaVictoria implements Escena {

    private final Recursos recursos;
    private final Viewport viewport;
    private final GestorEscenas gestor;
    private final int puntuacion;

    private final BitmapFont fuenteTitulo;
    private final BitmapFont fuenteNormal;
    private final GlyphLayout layout;

    private float timer = 0f;

    public EscenaVictoria(Recursos recursos, Viewport viewport, GestorEscenas gestor, int puntuacion) {
        this.recursos = recursos;
        this.viewport = viewport;
        this.gestor = gestor;
        this.puntuacion = puntuacion;

        this.fuenteTitulo = recursos.fuentes.getTituloPeque();
        this.fuenteNormal = recursos.fuentes.getNormal();
        this.layout = new GlyphLayout();
    }

    @Override
    public void actualizar(float delta) {
        timer += delta;

        if (timer >= 2.0f) {
            gestor.cambiarA(new EscenaNivel2Intro(recursos, viewport, gestor));
        }
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        batch.setColor(0f, 0f, 0f, 0.7f);
        batch.draw(recursos.pixelBlanco, 0, 0, Main.ANCHO_MUNDO, Main.ALTO_MUNDO);
        batch.setColor(1f, 1f, 1f, 1f);

        dibujarCentrado(batch, fuenteTitulo, recursos.textos.t("victory_title"), 1500f);

        dibujarCentrado(
            batch,
            fuenteNormal,
            recursos.textos.t("victory_score", puntuacion),
            1300f
        );
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
