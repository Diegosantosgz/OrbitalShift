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
import diego.juego.org.recursos.Recursos;

public class EscenaAyuda implements Escena {

    private final Recursos recursos;
    private final Viewport viewport;
    private final GestorEscenas gestor;

    private final BitmapFont fuenteTitulo;
    private final BitmapFont fuenteCuerpo;
    private final GlyphLayout layout;

    public EscenaAyuda(Recursos recursos, Viewport viewport, GestorEscenas gestor) {
        this.recursos = recursos;
        this.viewport = viewport;
        this.gestor = gestor;

        this.fuenteTitulo = recursos.fuentes.getTitulo();
        this.fuenteCuerpo = recursos.fuentes.getNormal();
        this.layout = new GlyphLayout();
    }

    @Override
    public void alMostrar() { }

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
        float W = viewport.getWorldWidth();
        float H = viewport.getWorldHeight();

        batch.setColor(0f, 0f, 0f, 0.65f);
        batch.draw(recursos.pixelBlanco, 0, 0, W, H);
        batch.setColor(1f, 1f, 1f, 1f);

        // ===== TÍTULO =====
        dibujarTextoCentrado(batch, fuenteTitulo, recursos.textos.t("help_title"), W / 2f, H - H * 0.12f);

        // ===== CUERPO (WRAP + CENTER) =====
        float margen = W * 0.08f;
        float maxWidth = W - margen * 2f;
        float yTop = H - H * 0.25f;

        String body = recursos.textos.t("help_body");

        layout.setText(
            fuenteCuerpo,
            body,
            Color.WHITE,
            maxWidth,
            Align.center,
            true
        );

        fuenteCuerpo.draw(batch, layout, margen, yTop);
    }

    private void dibujarTextoCentrado(SpriteBatch batch, BitmapFont font, String texto, float centroX, float y) {
        layout.setText(font, texto);
        float x = centroX - layout.width / 2f;
        font.draw(batch, layout, x, y);
    }

    @Override
    public void alRedimensionar(int ancho, int alto) {
        viewport.update(ancho, alto, true);
    }

    @Override
    public void alOcultar() { }

    @Override
    public void liberar() {
        // No liberar fuentes aquí (son compartidas y las libera Recursos.liberar()).
    }
}
