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
 * Escena de Game Over.
 */
public class EscenaGameOver implements Escena {

    private final Recursos recursos;
    private final Viewport viewport;
    private final GestorEscenas gestorEscenas;
    private final int puntuacionFinal;

    private final BitmapFont fuente;
    private final GlyphLayout layout;

    private final Rectangle btnReintentar;
    private final Rectangle btnSalir;

    public EscenaGameOver(Recursos recursos, Viewport viewport, GestorEscenas gestorEscenas, int puntuacionFinal) {
        this.recursos = recursos;
        this.viewport = viewport;
        this.gestorEscenas = gestorEscenas;
        this.puntuacionFinal = puntuacionFinal;

        this.fuente = new BitmapFont();
        this.layout = new GlyphLayout();

        float bw = 520f, bh = 120f;
        btnReintentar = new Rectangle((Main.ANCHO_MUNDO - bw) / 2f, 700f, bw, bh);
        btnSalir      = new Rectangle((Main.ANCHO_MUNDO - bw) / 2f, 540f, bw, bh);
    }

    @Override
    public void alMostrar() {
        // Si ya lo pedimos para ESTE score, no lo vuelvas a pedir
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
            Gdx.app.exit();
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
            Gdx.app.exit();
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

        // === TEXTOS I18N ===
        String tTitle  = recursos.textos.t("gameover_title");
        String tScore  = recursos.textos.t("gameover_score", puntuacionFinal);
        String tRecord = recursos.textos.t("gameover_record", EstadoJuego.getRecord());
        String tRetry  = recursos.textos.t("gameover_retry");
        String tExit   = recursos.textos.t("gameover_exit");
        String tHint   = recursos.textos.t("gameover_hint");

        fuente.getData().setScale(7.0f);
        dibujarTextoCentrado(batch, tTitle, Main.ANCHO_MUNDO / 2f, panelY + panelH - 140f);

        fuente.getData().setScale(3.2f);
        dibujarTextoCentrado(batch, tScore,  Main.ANCHO_MUNDO / 2f, panelY + panelH - 250f);
        dibujarTextoCentrado(batch, tRecord, Main.ANCHO_MUNDO / 2f, panelY + panelH - 320f);

        dibujarBoton(batch, btnReintentar, tRetry, true);
        dibujarBoton(batch, btnSalir,      tExit,  false);

        fuente.getData().setScale(1.6f);
        batch.setColor(1f, 1f, 1f, 0.75f);
        dibujarTextoCentrado(batch, tHint, Main.ANCHO_MUNDO / 2f, panelY + 90f);

        batch.setColor(1f, 1f, 1f, 1f);
        fuente.getData().setScale(1.0f);
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
        fuente.getData().setScale(2.2f);
        dibujarTextoCentrado(batch, texto, r.x + r.width / 2f, r.y + r.height / 2f + 22f);
        fuente.getData().setScale(1.0f);
    }

    private void dibujarTextoCentrado(SpriteBatch batch, String texto, float centroX, float y) {
        layout.setText(fuente, texto);
        float x = centroX - layout.width / 2f;
        fuente.draw(batch, layout, x, y);
    }

    @Override public void alRedimensionar(int ancho, int alto) { }
    @Override public void alOcultar() { }
    @Override public void liberar() { fuente.dispose(); }
}
