package diego.juego.org;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Escena de opciones: permite activar/desactivar el multitouch.
 */
public class EscenaOpciones implements Escena {

    private final Recursos recursos;
    private final Viewport viewport;
    private final GestorEscenas gestorEscenas;

    private final BitmapFont fuente;
    private final GlyphLayout layout;

    private final Rectangle btnToggleMultitouch;

    public EscenaOpciones(Recursos recursos, Viewport viewport, GestorEscenas gestorEscenas) {
        this.recursos = recursos;
        this.viewport = viewport;
        this.gestorEscenas = gestorEscenas;

        this.fuente = new BitmapFont();
        this.layout = new GlyphLayout();

        // Misma posición/tamaño que tenías en tu Main
        this.btnToggleMultitouch = new Rectangle((Main.ANCHO_MUNDO - 720f) / 2f, 900f, 720f, 120f);
    }

    @Override
    public void alMostrar() {
        // Nada especial
    }

    @Override
    public void actualizar(float delta) {
        procesarEntrada();
    }

    private void procesarEntrada() {
        // BACK / ESC -> volver al menú
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gestorEscenas.cambiarA(new EscenaMenu(recursos, viewport, gestorEscenas));
            return;
        }

        if (!Gdx.input.justTouched()) return;

        Vector3 v = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(v);
        float x = v.x;
        float y = v.y;

        // Si toca el botón, alternar multitouch y quedarse en opciones
        if (btnToggleMultitouch.contains(x, y)) {
            EstadoJuego.multitouchActivado = !EstadoJuego.multitouchActivado;
            return;
        }

        // Si toca fuera, volver al menú
        gestorEscenas.cambiarA(new EscenaMenu(recursos, viewport, gestorEscenas));
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        // Fondo oscurecido
        batch.setColor(0f, 0f, 0f, 0.65f);
        batch.draw(recursos.pixelBlanco, 0, 0, Main.ANCHO_MUNDO, Main.ALTO_MUNDO);
        batch.setColor(1f, 1f, 1f, 1f);

        // Título
        fuente.getData().setScale(6.0f);
        dibujarTextoCentrado(batch, "OPCIONES", Main.ANCHO_MUNDO / 2f, 1450f);

        // Subtítulo
        fuente.getData().setScale(2.4f);
        dibujarTextoCentrado(batch, "Multitouch (mover + disparar)", Main.ANCHO_MUNDO / 2f, 1200f);

        // Botón toggle
        dibujarBotonToggle(batch,
            btnToggleMultitouch,
            "MULTITOUCH: " + (EstadoJuego.multitouchActivado ? "ON" : "OFF"),
            EstadoJuego.multitouchActivado
        );

        // Ayuda
        fuente.getData().setScale(1.8f);
        dibujarTextoCentrado(batch, "Toca el botón para cambiar", Main.ANCHO_MUNDO / 2f, 650f);
        dibujarTextoCentrado(batch, "BACK para volver", Main.ANCHO_MUNDO / 2f, 360f);

        fuente.getData().setScale(1.0f);
    }

    private void dibujarBotonToggle(SpriteBatch batch, Rectangle r, String texto, boolean activado) {
        // Color según estado
        if (activado) batch.setColor(0.20f, 0.85f, 0.35f, 0.85f);
        else batch.setColor(0.85f, 0.25f, 0.25f, 0.85f);

        batch.draw(recursos.pixelBlanco, r.x, r.y, r.width, r.height);

        // Líneas decorativas
        batch.setColor(0f, 0f, 0f, 0.25f);
        batch.draw(recursos.pixelBlanco, r.x, r.y, r.width, 6f);
        batch.draw(recursos.pixelBlanco, r.x, r.y + r.height - 6f, r.width, 6f);

        // Texto
        batch.setColor(1f, 1f, 1f, 1f);
        fuente.getData().setScale(2.6f);
        dibujarTextoCentrado(batch, texto, r.x + r.width / 2f, r.y + r.height / 2f + 22f);
        fuente.getData().setScale(1.0f);
    }

    private void dibujarTextoCentrado(SpriteBatch batch, String texto, float centroX, float y) {
        layout.setText(fuente, texto);
        float x = centroX - layout.width / 2f;
        fuente.draw(batch, layout, x, y);
    }

    @Override
    public void alRedimensionar(int ancho, int alto) {
        // El viewport se actualiza en Main
    }

    @Override
    public void alOcultar() {
        // Nada
    }

    @Override
    public void liberar() {
    fuente.dispose();
    }
}
