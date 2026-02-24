package diego.juego.org.escenas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import diego.juego.org.Escena;
import diego.juego.org.GestorEscenas;
import diego.juego.org.Main;
import diego.juego.org.recursos.Recursos;

public final class EscenaVictoriaFinal implements Escena {

    private final Recursos recursos;
    private final Viewport viewport;
    private final GestorEscenas gestorEscenas;
    private final int puntuacion;

    private final BitmapFont fuente = new BitmapFont();
    private final GlyphLayout layout = new GlyphLayout();

    private final Rectangle btnMenu = new Rectangle(0, 0, 620f, 140f);

    public EscenaVictoriaFinal(Recursos recursos, Viewport viewport, GestorEscenas gestorEscenas, int puntuacion) {
        this.recursos = recursos;
        this.viewport = viewport;
        this.gestorEscenas = gestorEscenas;
        this.puntuacion = puntuacion;

        btnMenu.set(
            (Main.ANCHO_MUNDO - btnMenu.width) / 2f,
            360f,
            btnMenu.width,
            btnMenu.height
        );
    }

    @Override
    public void alMostrar() {
        // Si quieres: parar música o poner música de victoria
        // recursos.reproducirMusicaVictoria();
    }

    @Override
    public void actualizar(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gestorEscenas.cambiarA(new EscenaMenu(recursos, viewport, gestorEscenas));
            return;
        }

        if (!Gdx.input.justTouched()) return;

        Vector3 v = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(v);

        if (btnMenu.contains(v.x, v.y)) {
            gestorEscenas.cambiarA(new EscenaMenu(recursos, viewport, gestorEscenas));
        }
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        // Fondo
        batch.setColor(0f, 0f, 0f, 0.85f);
        batch.draw(recursos.pixelBlanco, 0, 0, Main.ANCHO_MUNDO, Main.ALTO_MUNDO);
        batch.setColor(1f, 1f, 1f, 1f);

        fuente.getData().setScale(6.0f);
        dibujarTextoCentrado(batch, recursos.textos.t("victory_title"), Main.ANCHO_MUNDO / 2f, Main.ALTO_MUNDO - 260f);

        fuente.getData().setScale(2.8f);
        dibujarTextoCentrado(batch, recursos.textos.t("victory_final_msg"), Main.ANCHO_MUNDO / 2f, Main.ALTO_MUNDO - 430f);

        fuente.getData().setScale(3.2f);
        dibujarTextoCentrado(batch, recursos.textos.t("score") + ": " + puntuacion, Main.ANCHO_MUNDO / 2f, Main.ALTO_MUNDO - 560f);

        fuente.getData().setScale(1.0f);

        dibujarBoton(batch, btnMenu, recursos.textos.t("victory_back_menu"));
    }

    private void dibujarTextoCentrado(SpriteBatch batch, String texto, float centroX, float y) {
        layout.setText(fuente, texto);
        float x = centroX - layout.width / 2f;
        fuente.draw(batch, layout, x, y);
    }

    private void dibujarBoton(SpriteBatch batch, Rectangle r, String texto) {
        batch.setColor(0.15f, 0.65f, 1f, 0.85f);
        batch.draw(recursos.pixelBlanco, r.x, r.y, r.width, r.height);
        batch.setColor(1f, 1f, 1f, 1f);

        fuente.getData().setScale(2.6f);
        layout.setText(fuente, texto);
        float tx = r.x + (r.width - layout.width) / 2f;
        float ty = r.y + (r.height / 2f) + (layout.height / 2f);
        fuente.draw(batch, layout, tx, ty);
        fuente.getData().setScale(1.0f);
    }

    @Override public void alRedimensionar(int ancho, int alto) { viewport.update(ancho, alto, true); }
    @Override public void alOcultar() {}
    @Override public void liberar() { fuente.dispose(); }
}
