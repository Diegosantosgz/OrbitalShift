package diego.juego.org.escenas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;

import diego.juego.org.Escena;
import diego.juego.org.GestorEscenas;
import diego.juego.org.Main;
import diego.juego.org.recursos.Recursos;

public class EscenaAyuda implements Escena {

    private final Recursos recursos;
    private final Viewport viewport;
    private final GestorEscenas gestor;

    private final BitmapFont fuente = new BitmapFont();
    private final GlyphLayout layout = new GlyphLayout();

    public EscenaAyuda(Recursos recursos, Viewport viewport, GestorEscenas gestor) {
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
        float W = viewport.getWorldWidth();
        float H = viewport.getWorldHeight();

        // fondo oscuro
        batch.setColor(0f, 0f, 0f, 0.65f);
        batch.draw(recursos.pixelBlanco, 0, 0, W, H);
        batch.setColor(1f, 1f, 1f, 1f);

        // ===== TÍTULO =====
        fuente.getData().setScale(Math.max(3.5f, H / 350f)); // escala relativa
        String titulo = recursos.textos.t("help_title");
        layout.setText(fuente, titulo);
        fuente.draw(batch, layout, (W - layout.width) / 2f, H - H * 0.12f);

        // ===== CUERPO (WRAP + CENTER) =====
        fuente.getData().setScale(Math.max(1.6f, H / 900f));

        float margen = W * 0.08f;             // 8% de margen lateral
        float maxWidth = W - margen * 2f;     // ancho útil para el wrap
        float yTop = H - H * 0.25f;           // empieza debajo del título

        String body = recursos.textos.t("help_body");

        layout.setText(
            fuente,
            body,
            Color.WHITE,
            maxWidth,
            Align.center,
            true   // wrap
        );

        // OJO: draw x,y aquí usa x=izquierda del bloque (margen) y y=arriba del bloque
        fuente.draw(batch, layout, margen, yTop);

        fuente.getData().setScale(1.0f);
    }

    private void dibujarCentrado(SpriteBatch batch, String texto, float centroX, float y) {
        layout.setText(fuente, texto);
        float x = centroX - layout.width / 2f;
        fuente.draw(batch, layout, x, y);
    }
    private void dibujarMultilineaCentrada(SpriteBatch batch, String texto, float centroX, float yTop, float lineHeight) {
        String[] lineas = texto.split("\n");
        float y = yTop;

        for (String linea : lineas) {
            layout.setText(fuente, linea);
            float x = centroX - layout.width / 2f;
            fuente.draw(batch, layout, x, y);
            y -= lineHeight;
        }
    }

    @Override public void alRedimensionar(int ancho, int alto) { viewport.update(ancho,alto, true); }
    @Override public void alOcultar() { }
    @Override public void liberar() { fuente.dispose(); }
}
