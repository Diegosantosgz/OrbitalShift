package diego.juego.org;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import diego.juego.org.escenas.EscenaMenu;
import diego.juego.org.estado.EstadoJuego;
import diego.juego.org.plataforma.PlatformServices;
import diego.juego.org.recursos.Recursos;

/**
 * Clase principal del juego: solo inicializa motor, recursos y delega en escenas.
 */
public class Main extends ApplicationAdapter {

    public static final float ANCHO_MUNDO = 1080f;
    public static final float ALTO_MUNDO  = 1920f;

    // Servicios Android (vibración, etc.)
    public static PlatformServices services;

    private SpriteBatch batch;
    private OrthographicCamera camara;
    private Viewport viewport;

    private Recursos recursos;
    private GestorEscenas gestorEscenas;

    @Override
    public void create() {
        EstadoJuego.cargarScores();

        batch = new SpriteBatch();

        camara = new OrthographicCamera();
        viewport = new FitViewport(ANCHO_MUNDO, ALTO_MUNDO, camara);
        viewport.apply();
        camara.position.set(ANCHO_MUNDO / 2f, ALTO_MUNDO / 2f, 0);

        recursos = new Recursos();
        recursos.cargar();

        // Música por defecto ON (luego lo haces configurable en opciones)
        if (recursos.musicaFondo != null) recursos.musicaFondo.play();

        gestorEscenas = new GestorEscenas();
        gestorEscenas.cambiarA(new EscenaMenu(recursos, viewport, gestorEscenas));
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        float delta = Gdx.graphics.getDeltaTime();

        // Actualizar escena
        Escena escena = gestorEscenas.getEscenaActual();
        if (escena != null) escena.actualizar(delta);

        // Dibujar escena
        camara.update();
        batch.setProjectionMatrix(camara.combined);

        batch.begin();
        if (escena != null) escena.dibujar(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        Escena escena = gestorEscenas.getEscenaActual();
        if (escena != null) escena.alRedimensionar(width, height);
    }

    @Override
    public void pause() {
        if (recursos != null && recursos.musicaFondo != null && recursos.musicaFondo.isPlaying()) {
            recursos.musicaFondo.pause();
        }
    }

    @Override
    public void resume() {
        if (recursos != null && recursos.musicaFondo != null && !recursos.musicaFondo.isPlaying()) {
            recursos.musicaFondo.play();
        }
    }

    @Override
    public void dispose() {
        if (gestorEscenas != null) gestorEscenas.liberar();
        if (recursos != null) recursos.liberar();
        if (batch != null) batch.dispose();
    }
}
