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
 * Escena de Game Over.
 */
public class EscenaGameOver implements Escena {

    private final Recursos recursos;
    private final Viewport viewport;
    private final GestorEscenas gestorEscenas;
    private final int puntuacionFinal;

    private final BitmapFont fuenteTitulo;
    private final BitmapFont fuenteNormal;
    private final BitmapFont fuenteBoton;
    private final GlyphLayout layout;

    private final Rectangle btnReintentar;
    private final Rectangle btnSalir;

    public EscenaGameOver(Recursos recursos, Viewport viewport, GestorEscenas gestorEscenas, int puntuacionFinal) {
        this.recursos = recursos;
        this.viewport = viewport;
        this.gestorEscenas = gestorEscenas;
        this.puntuacionFinal = puntuacionFinal;

        this.fuenteTitulo = recursos.fuentes.getTituloPeque();
        this.fuenteNormal = recursos.fuentes.getNormal();
        this.fuenteBoton = recursos.fuentes.getBoton();
        this.layout = new GlyphLayout();

        float bw = 520f, bh = 120f;
        btnReintentar = new Rectangle((Main.ANCHO_MUNDO - bw) / 2f, 700f, bw, bh);
        btnSalir      = new Rectangle((Main.ANCHO_MUNDO - bw) / 2f, 540f, bw, bh);
    }
    private void dibujarTextoCentradoConAncho(SpriteBatch batch, BitmapFont font, String texto, float centroX, float y, float maxAncho) {
        layout.setText(font, texto);
        float escala = 1f;

        if (layout.width > maxAncho) {
            escala = maxAncho / layout.width;
        }

        font.getData().setScale(escala);
        layout.setText(font, texto);

        float x = centroX - layout.width / 2f;
        font.draw(batch, layout, x, y);

        font.getData().setScale(1f);
    }

    @Override
    public void alMostrar() {
        if (EstadoJuego.yaSePidieronSiglasPara(puntuacionFinal)) return;

        if (EstadoJuego.entraEnTop10(puntuacionFinal)) {
            EstadoJuego.marcarSiglasPedidasPara(puntuacionFinal);

            gestorEscenas.cambiarA(new EscenaNuevoRecord(
                recursos, viewport, gestorEscenas, puntuacionFinal,
                new Runnable() {
                    @Override public void run() {
                        gestorEscenas.cambiarA(new EscenaGameOver(
                            recursos, viewport, gestorEscenas, puntuacionFinal
                        ));
                    }
                }
            ));
        }
    }

    @Override
    public void actualizar(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            EstadoJuego.resetFlagsGameOver();
            EstadoJuego.nivelActual = 1;
            gestorEscenas.cambiarA(new EscenaMenu(recursos, viewport, gestorEscenas));
            return;
        }

        if (!Gdx.input.justTouched()) return;

        Vector3 v = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(v);

        if (btnReintentar.contains(v.x, v.y)) {
            EstadoJuego.resetFlagsGameOver();
            EstadoJuego.nivelActual = 1;
            gestorEscenas.cambiarA(new EscenaJuego(recursos, viewport, gestorEscenas));
            return;
        }

        if (btnSalir.contains(v.x, v.y)) {
            EstadoJuego.resetFlagsGameOver();
            EstadoJuego.nivelActual = 1;
            gestorEscenas.cambiarA(new EscenaMenu(recursos, viewport, gestorEscenas));
        }
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        batch.setColor(0f, 0f, 0f, 0.65f);
        batch.draw(recursos.pixelBlanco, 0, 0, Main.ANCHO_MUNDO, Main.ALTO_MUNDO);
        batch.setColor(1f, 1f, 1f, 1f);

        float panelW = 820f;
        float panelH = 720f;
        float panelX = (Main.ANCHO_MUNDO - panelW) / 2f;
        float panelY = (Main.ALTO_MUNDO - panelH) / 2f;

        batch.setColor(0.08f, 0.08f, 0.10f, 0.90f);
        batch.draw(recursos.pixelBlanco, panelX, panelY, panelW, panelH);

        batch.setColor(1f, 0.2f, 0.2f, 0.90f);
        batch.draw(recursos.pixelBlanco, panelX, panelY + panelH - 10f, panelW, 10f);

        batch.setColor(1f, 1f, 1f, 1f);

        String tTitle  = recursos.textos.t("gameover_title");
        String tScore  = recursos.textos.t("gameover_score", puntuacionFinal);
        String tRecord = recursos.textos.t("gameover_record", EstadoJuego.getRecord());
        String tRetry  = recursos.textos.t("gameover_retry");
        String tExit   = recursos.textos.t("gameover_exit");
        String tHint   = recursos.textos.t("gameover_hint");

        dibujarTextoCentradoConAncho(
            batch,
            fuenteTitulo,
            tTitle,
            Main.ANCHO_MUNDO / 2f,
            panelY + panelH - 140f,
            panelW * 0.90f
        );

        dibujarTextoCentrado(batch, fuenteNormal, tScore,  Main.ANCHO_MUNDO / 2f, panelY + panelH - 250f);
        dibujarTextoCentrado(batch, fuenteNormal, tRecord, Main.ANCHO_MUNDO / 2f, panelY + panelH - 320f);

        dibujarBoton(batch, btnReintentar, tRetry, true);
        dibujarBoton(batch, btnSalir,      tExit,  false);

        batch.setColor(1f, 1f, 1f, 0.75f);
        dibujarTextoCentrado(batch, fuenteNormal, tHint, Main.ANCHO_MUNDO / 2f, panelY + 90f);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void dibujarBoton(SpriteBatch batch, Rectangle r, String texto, boolean primario) {
        if (primario) batch.setColor(0.15f, 0.65f, 1f, 0.85f);
        else batch.setColor(0.85f, 0.25f, 0.25f, 0.85f);

        batch.draw(recursos.pixelBlanco, r.x, r.y, r.width, r.height);

        batch.setColor(0f, 0f, 0f, 0.25f);
        batch.draw(recursos.pixelBlanco, r.x, r.y, r.width, 6f);
        batch.draw(recursos.pixelBlanco, r.x, r.y + r.height - 6f, r.width, 6f);
        batch.draw(recursos.pixelBlanco, r.x, r.y, 6f, r.height);
        batch.draw(recursos.pixelBlanco, r.x + r.width - 6f, r.y, 6f, r.height);

        batch.setColor(1f, 1f, 1f, 1f);

        // Texto centrado y autoajustado al ancho del botón
        dibujarTextoCentradoAjustado(
            batch,
            fuenteBoton,
            texto,
            r.x + r.width / 2f,
            r.y + r.height / 2f + 18f,
            r.width * 0.88f
        );
    }
    private void dibujarTextoCentradoAjustado(
        SpriteBatch batch,
        BitmapFont font,
        String texto,
        float centroX,
        float y,
        float maxAncho
    ) {
        layout.setText(font, texto);

        float escala = 1f;
        if (layout.width > maxAncho) {
            escala = maxAncho / layout.width;
        }

        font.getData().setScale(escala);
        layout.setText(font, texto);

        float x = centroX - layout.width / 2f;
        font.draw(batch, layout, x, y);

        font.getData().setScale(1f);
    }

    private void dibujarTextoCentrado(SpriteBatch batch, BitmapFont font, String texto, float centroX, float y) {
        layout.setText(font, texto);
        float x = centroX - layout.width / 2f;
        font.draw(batch, layout, x, y);
    }

    @Override public void alRedimensionar(int ancho, int alto) { viewport.update(ancho, alto, true); }
    @Override public void alOcultar() { }

    @Override
    public void liberar() {
        // No liberar fuentes aquí (son compartidas y las libera Recursos.liberar()).
    }
}
