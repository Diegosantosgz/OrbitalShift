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
import diego.juego.org.estado.EstadoJuego;
import diego.juego.org.GestorEscenas;
import diego.juego.org.Main;
import diego.juego.org.recursos.Recursos;

public class EscenaOpciones implements Escena {

    private final Recursos recursos;
    private final Viewport viewport;
    private final GestorEscenas gestorEscenas;

    private final BitmapFont fuente;
    private final GlyphLayout layout;

    private final Rectangle btnToggleMultitouch;
    private final Rectangle btnToggleVibracion;
    private final Rectangle btnToggleMusica;
    private final Rectangle btnToggleSfx;

    // Idiomas
    private final Rectangle btnIdiomaES;
    private final Rectangle btnIdiomaEN;

    public EscenaOpciones(Recursos recursos, Viewport viewport, GestorEscenas gestorEscenas) {
        this.recursos = recursos;
        this.viewport = viewport;
        this.gestorEscenas = gestorEscenas;

        this.fuente = new BitmapFont();
        this.layout = new GlyphLayout();

        btnToggleMultitouch = new Rectangle((Main.ANCHO_MUNDO - 720f) / 2f, 900f, 720f, 120f);
        btnToggleVibracion  = new Rectangle((Main.ANCHO_MUNDO - 720f) / 2f, 720f, 720f, 120f);
        btnToggleMusica     = new Rectangle((Main.ANCHO_MUNDO - 720f) / 2f, 540f, 720f, 120f);
        btnToggleSfx        = new Rectangle((Main.ANCHO_MUNDO - 720f) / 2f, 360f, 720f, 120f);

        // Botones idioma (debajo de SFX, uno al lado de otro)
        float totalW = 720f;
        float gap = 30f;
        float eachW = (totalW - gap) / 2f;
        float x0 = (Main.ANCHO_MUNDO - totalW) / 2f;
        float yIdiomas = 180f;

        btnIdiomaES = new Rectangle(x0, yIdiomas, eachW, 120f);
        btnIdiomaEN = new Rectangle(x0 + eachW + gap, yIdiomas, eachW, 120f);
    }

    @Override public void alMostrar() { }

    @Override
    public void actualizar(float delta) {
        procesarEntrada();
    }

    private void procesarEntrada() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gestorEscenas.cambiarA(new EscenaMenu(recursos, viewport, gestorEscenas));
            return;
        }

        if (!Gdx.input.justTouched()) return;

        Vector3 v = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(v);

        float x = v.x;
        float y = v.y;

        if (btnToggleMultitouch.contains(x, y)) {
            EstadoJuego.multitouchActivado = !EstadoJuego.multitouchActivado;
            return;
        }

        if (btnToggleVibracion.contains(x, y)) {
            EstadoJuego.vibracionActivada = !EstadoJuego.vibracionActivada;
            return;
        }

        if (btnToggleMusica.contains(x, y)) {
            EstadoJuego.musicaActivada = !EstadoJuego.musicaActivada;
            if (EstadoJuego.musicaActivada) recursos.musicaFondo.play();
            else recursos.musicaFondo.pause();
            return;
        }

        if (btnToggleSfx.contains(x, y)) {
            EstadoJuego.sfxActivados = !EstadoJuego.sfxActivados;
            return;
        }

        // Idioma
        if (btnIdiomaES.contains(x, y)) {
            EstadoJuego.idiomaActual = EstadoJuego.Idioma.ES;
            recursos.textos.recargar();
            return;
        }

        if (btnIdiomaEN.contains(x, y)) {
            EstadoJuego.idiomaActual = EstadoJuego.Idioma.EN;
            recursos.textos.recargar();
            return;
        }

        gestorEscenas.cambiarA(new EscenaMenu(recursos, viewport, gestorEscenas));
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        batch.setColor(0f, 0f, 0f, 0.65f);
        batch.draw(recursos.pixelBlanco, 0, 0, Main.ANCHO_MUNDO, Main.ALTO_MUNDO);
        batch.setColor(1f, 1f, 1f, 1f);

        String on  = recursos.textos.t("common_on");
        String off = recursos.textos.t("common_off");

        // Título
        fuente.getData().setScale(6.0f);
        dibujarTextoCentrado(batch, recursos.textos.t("opt_title"), Main.ANCHO_MUNDO / 2f, 1450f);

        // Subtítulo
        fuente.getData().setScale(2.4f);
        dibujarTextoCentrado(batch, recursos.textos.t("opt_multitouch_desc"), Main.ANCHO_MUNDO / 2f, 1200f);

        // Toggles
        fuente.getData().setScale(2.6f);
        dibujarBotonToggle(batch, btnToggleMultitouch,
            recursos.textos.t("opt_multitouch") + ": " + (EstadoJuego.multitouchActivado ? on : off),
            EstadoJuego.multitouchActivado
        );

        dibujarBotonToggle(batch, btnToggleVibracion,
            recursos.textos.t("opt_vibration") + ": " + (EstadoJuego.vibracionActivada ? on : off),
            EstadoJuego.vibracionActivada
        );

        dibujarBotonToggle(batch, btnToggleMusica,
            recursos.textos.t("opt_music") + ": " + (EstadoJuego.musicaActivada ? on : off),
            EstadoJuego.musicaActivada
        );

        dibujarBotonToggle(batch, btnToggleSfx,
            recursos.textos.t("opt_sfx") + ": " + (EstadoJuego.sfxActivados ? on : off),
            EstadoJuego.sfxActivados
        );

        // Idioma
        fuente.getData().setScale(2.0f);
        dibujarTextoCentrado(batch, recursos.textos.t("opt_language"), Main.ANCHO_MUNDO / 2f, 330f);

        dibujarBotonIdioma(batch, btnIdiomaES, "ES", EstadoJuego.idiomaActual == EstadoJuego.Idioma.ES);
        dibujarBotonIdioma(batch, btnIdiomaEN, "EN", EstadoJuego.idiomaActual == EstadoJuego.Idioma.EN);

        // Back
        fuente.getData().setScale(1.8f);
        dibujarTextoCentrado(batch, recursos.textos.t("opt_back"), Main.ANCHO_MUNDO / 2f, 140f);

        fuente.getData().setScale(1.0f);
    }

    private void dibujarBotonToggle(SpriteBatch batch, Rectangle r, String texto, boolean activado) {
        if (activado) batch.setColor(0.20f, 0.85f, 0.35f, 0.85f);
        else batch.setColor(0.85f, 0.25f, 0.25f, 0.85f);

        batch.draw(recursos.pixelBlanco, r.x, r.y, r.width, r.height);

        batch.setColor(0f, 0f, 0f, 0.25f);
        batch.draw(recursos.pixelBlanco, r.x, r.y, r.width, 6f);
        batch.draw(recursos.pixelBlanco, r.x, r.y + r.height - 6f, r.width, 6f);

        batch.setColor(1f, 1f, 1f, 1f);
        dibujarTextoCentrado(batch, texto, r.x + r.width / 2f, r.y + r.height / 2f + 22f);
    }

    private void dibujarTextoCentrado(SpriteBatch batch, String texto, float centroX, float y) {
        layout.setText(fuente, texto);
        float x = centroX - layout.width / 2f;
        fuente.draw(batch, layout, x, y);
    }

    private void dibujarBotonIdioma(SpriteBatch batch, Rectangle r, String texto, boolean seleccionado) {
        if (seleccionado) batch.setColor(0.15f, 0.65f, 1f, 0.90f);
        else batch.setColor(0.20f, 0.20f, 0.25f, 0.85f);

        batch.draw(recursos.pixelBlanco, r.x, r.y, r.width, r.height);

        batch.setColor(0f, 0f, 0f, 0.25f);
        batch.draw(recursos.pixelBlanco, r.x, r.y, r.width, 6f);
        batch.draw(recursos.pixelBlanco, r.x, r.y + r.height - 6f, r.width, 6f);

        batch.setColor(1f, 1f, 1f, 1f);
        dibujarTextoCentrado(batch, texto, r.x + r.width / 2f, r.y + r.height / 2f + 22f);
    }

    @Override public void alRedimensionar(int ancho, int alto) { }
    @Override public void alOcultar() { }

    @Override
    public void liberar() {
        fuente.dispose();
    }
}
