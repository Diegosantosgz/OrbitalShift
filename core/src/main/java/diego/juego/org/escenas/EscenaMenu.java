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

/**
 * Escena del menú principal: muestra botones y permite navegar a otras escenas.
 */
public class EscenaMenu implements Escena {

    private final Recursos recursos;
    private final Viewport viewport;
    private final GestorEscenas gestorEscenas;

    // UI
    private Rectangle btnJugar, btnOpciones, btnCreditos, btnAyuda, btnRecords, btnSalir;




    // Texto
    private final BitmapFont fuente;
    private final GlyphLayout layout;

    // Parallax
    private float oMuy = 0f, oLej = 0f, oCer = 0f;
    private final float vMuy = 10f, vLej = 20f, vCer = 50f;

    // Tamaños UI
    private final float anchoBoton = 620f;
    private final float altoBoton = 120f;
    private final float separacion = 26f;

    public EscenaMenu(Recursos recursos, Viewport viewport, GestorEscenas gestorEscenas) {
        this.recursos = recursos;
        this.viewport = viewport;
        this.gestorEscenas = gestorEscenas;

        this.fuente = new BitmapFont();
        this.layout = new GlyphLayout();

        crearBotones();
    }

    private void crearBotones() {
        float x0 = (Main.ANCHO_MUNDO - anchoBoton) / 2f;
        float startY = 1080f;

        btnJugar    = new Rectangle(x0, startY,                       anchoBoton, altoBoton);
        btnOpciones = new Rectangle(x0, startY - (altoBoton+separacion) * 1f, anchoBoton, altoBoton);
        btnCreditos = new Rectangle(x0, startY - (altoBoton+separacion) * 2f, anchoBoton, altoBoton);
        btnAyuda    = new Rectangle(x0, startY - (altoBoton+separacion) * 3f, anchoBoton, altoBoton);
        btnRecords  = new Rectangle(x0, startY - (altoBoton+separacion) * 4f, anchoBoton, altoBoton);
        btnSalir    = new Rectangle(x0, startY - (altoBoton+separacion) * 5f, anchoBoton, altoBoton);
    }

    @Override
    public void alMostrar() {
        if (EstadoJuego.musicaActivada) {
            recursos.musicaFondo.play();
        } else {
            recursos.musicaFondo.pause();
        }
    }


    @Override
    public void actualizar(float delta) {
        actualizarParallax(delta);
        procesarEntrada();
    }

    private void actualizarParallax(float delta) {
        oMuy -= vMuy * delta;
        oLej -= vLej * delta;
        oCer -= vCer * delta;

        if (oMuy <= -Main.ALTO_MUNDO) oMuy += Main.ALTO_MUNDO;
        if (oLej <= -Main.ALTO_MUNDO) oLej += Main.ALTO_MUNDO;
        if (oCer <= -Main.ALTO_MUNDO) oCer += Main.ALTO_MUNDO;
    }

    private void procesarEntrada() {
        // BACK / ESC -> salir del juego desde el menú
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
            return;
        }

        if (!Gdx.input.justTouched()) return;

        Vector3 v = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(v);
        float x = v.x;
        float y = v.y;

        if (btnJugar.contains(x, y)) {
            // Ir a jugar
            gestorEscenas.cambiarA(new EscenaJuego(recursos, viewport, gestorEscenas));
            return;
        }

        if (btnOpciones.contains(x, y)) {
            gestorEscenas.cambiarA(new EscenaOpciones(recursos, viewport, gestorEscenas));
            return;
        }

        if (btnCreditos.contains(x, y)) {
            gestorEscenas.cambiarA(new EscenaCreditos(recursos, viewport, gestorEscenas));
            return;
        }

        if (btnAyuda.contains(x, y)) {
            gestorEscenas.cambiarA(new EscenaAyuda(recursos, viewport, gestorEscenas));
            return;
        }
        if (btnRecords.contains(x, y)) {
            gestorEscenas.cambiarA(new EscenaRecords(recursos, viewport, gestorEscenas));
            return;
        }


        if (btnSalir.contains(x, y)) {
            Gdx.app.exit();
        }
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        dibujarParallax(batch);

        // oscurecer un poco encima para que el texto se vea bien
        batch.setColor(0f, 0f, 0f, 0.55f);
        batch.draw(recursos.pixelBlanco, 0, 0, Main.ANCHO_MUNDO, Main.ALTO_MUNDO);
        batch.setColor(1f, 1f, 1f, 1f);

        // Título
        fuente.getData().setScale(7.0f);
        dibujarTextoCentrado(batch, recursos.textos.t("menu_title"), Main.ANCHO_MUNDO / 2f, 1520f);

        // Récord
        fuente.getData().setScale(2.2f);
        dibujarTextoCentrado(batch,
            recursos.textos.t("menu_record", EstadoJuego.getSiglas(0), EstadoJuego.getScore(0)),
            Main.ANCHO_MUNDO / 2f,
            1400f
        );

        // Botones
        fuente.getData().setScale(2.2f);
        dibujarBoton(batch, btnJugar,    recursos.textos.t("menu_play"));
        dibujarBoton(batch, btnOpciones, recursos.textos.t("menu_options"));
        dibujarBoton(batch, btnCreditos, recursos.textos.t("menu_credits"));
        dibujarBoton(batch, btnRecords,  recursos.textos.t("menu_records"));
        dibujarBoton(batch, btnAyuda,    recursos.textos.t("menu_help"));
        dibujarBoton(batch, btnSalir,    recursos.textos.t("menu_exit"));


        fuente.getData().setScale(1.0f);
    }

    private void dibujarParallax(SpriteBatch batch) {
        dibujarCapaParallax(batch, recursos.fondoMuyLejano, oMuy);
        dibujarCapaParallax(batch, recursos.fondoLejano, oLej);
        dibujarCapaParallax(batch, recursos.fondoCercano, oCer);
    }

    private void dibujarCapaParallax(SpriteBatch batch, com.badlogic.gdx.graphics.Texture tex, float offsetY) {
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(tex, 0, offsetY, Main.ANCHO_MUNDO, Main.ALTO_MUNDO);
        batch.draw(tex, 0, offsetY + Main.ALTO_MUNDO, Main.ANCHO_MUNDO, Main.ALTO_MUNDO);
    }

    private void dibujarBoton(SpriteBatch batch, Rectangle r, String texto) {
        // fondo botón
        batch.setColor(0.15f, 0.65f, 1f, 0.85f);
        batch.draw(recursos.pixelBlanco, r.x, r.y, r.width, r.height);

        // líneas decorativas
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

    @Override
    public void alRedimensionar(int ancho, int alto) {
        // El viewport lo actualiza Main; aquí no hace falta nada.
    }

    @Override
    public void alOcultar() {
        // Nada especial
    }

    @Override
    public void liberar() {
        // Ojo: no liberamos recursos compartidos aquí (eso lo hace Main -> Recursos.liberar()).
        // Liberamos la fuente propia de esta escena.
        fuente.dispose();
    }
}
