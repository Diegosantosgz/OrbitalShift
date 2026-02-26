package diego.juego.org.escenas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

import diego.juego.org.Escena;
import diego.juego.org.GestorEscenas;
import diego.juego.org.Main;
import diego.juego.org.estado.EstadoJuego;
import diego.juego.org.recursos.Recursos;

public final class EscenaVictoriaFinal implements Escena {

    private final Recursos recursos;
    private final Viewport viewport;
    private final GestorEscenas gestorEscenas;
    private final int puntuacion;

    private final BitmapFont fuenteTitulo;
    private final BitmapFont fuenteNormal;
    private final BitmapFont fuenteBoton;
    private final GlyphLayout layout;

    private final Rectangle btnMenu = new Rectangle(0, 0, 620f, 140f);

    private boolean mostrandoRecord = false;
    private boolean recordGuardado = false;

    private final StringBuilder siglasInput = new StringBuilder();

    private boolean pidiendoSiglasMovil = false;

    private final Rectangle btnContinuarRecord = new Rectangle(0, 0, 760f, 150f);

    private final float recordPanelW = 980f;
    private final float recordPanelH = 760f;
    private float recordPanelX, recordPanelY;

    public EscenaVictoriaFinal(Recursos recursos, Viewport viewport, GestorEscenas gestorEscenas, int puntuacion) {
        this.recursos = recursos;
        this.viewport = viewport;
        this.gestorEscenas = gestorEscenas;
        this.puntuacion = puntuacion;

        this.fuenteTitulo = recursos.fuentes.getTitulo();
        this.fuenteNormal = recursos.fuentes.getNormal();
        this.fuenteBoton = recursos.fuentes.getBoton();
        this.layout = new GlyphLayout();

        btnMenu.set(
            (Main.ANCHO_MUNDO - btnMenu.width) / 2f,
            360f,
            btnMenu.width,
            btnMenu.height
        );

        recordPanelX = (Main.ANCHO_MUNDO - recordPanelW) / 2f;
        recordPanelY = 140f;

        btnContinuarRecord.set(
            (Main.ANCHO_MUNDO - btnContinuarRecord.width) / 2f,
            recordPanelY + 60f,
            btnContinuarRecord.width,
            btnContinuarRecord.height
        );
    }

    @Override
    public void alMostrar() {
        if (EstadoJuego.entraEnTop10(puntuacion)
            && !EstadoJuego.yaSePidieronSiglasPara(puntuacion)) {

            mostrandoRecord = true;
            recordGuardado = false;
            siglasInput.setLength(0);

            pedirSiglasMovil();
        }
    }

    private void pedirSiglasMovil() {
        if (pidiendoSiglasMovil) return;
        pidiendoSiglasMovil = true;

        Gdx.input.getTextInput(new TextInputListener() {
            @Override
            public void input(String text) {
                siglasInput.setLength(0);
                siglasInput.append(limpiar3(text));
                pidiendoSiglasMovil = false;
            }

            @Override
            public void canceled() {
                pidiendoSiglasMovil = false;
            }
        }, recursos.textos.t("record_title"), "", recursos.textos.t("record_hint"));
    }

    private String limpiar3(String s) {
        if (s == null) s = "";
        StringBuilder out = new StringBuilder(3);
        for (int i = 0; i < s.length() && out.length() < 3; i++) {
            char c = s.charAt(i);
            if (Character.isLetterOrDigit(c)) out.append(Character.toUpperCase(c));
        }
        while (out.length() < 3) out.append('-');
        return out.toString();
    }

    @Override
    public void actualizar(float delta) {

        if (mostrandoRecord) {

            leerInputSiglas();

            if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                return;
            }

            if (!Gdx.input.justTouched()) return;

            Vector3 v = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(v);

            if (btnContinuarRecord.contains(v.x, v.y)) {

                if (siglasInput.length() != 3) {
                    pedirSiglasMovil();
                    return;
                }

                if (!recordGuardado) {
                    EstadoJuego.insertarTopScore(siglasInput.toString(), puntuacion);
                    EstadoJuego.marcarSiglasPedidasPara(puntuacion);

                    recordGuardado = true;
                    mostrandoRecord = false;
                }
            }
            return;
        }

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

    private void leerInputSiglas() {
        if (siglasInput.length() >= 3) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
                siglasInput.deleteCharAt(siglasInput.length() - 1);
            }
            return;
        }

        for (int key = Input.Keys.A; key <= Input.Keys.Z; key++) {
            if (Gdx.input.isKeyJustPressed(key)) {
                meterChar((char) ('A' + (key - Input.Keys.A)));
            }
        }

        for (int key = Input.Keys.NUM_0; key <= Input.Keys.NUM_9; key++) {
            if (Gdx.input.isKeyJustPressed(key)) {
                meterChar((char) ('0' + (key - Input.Keys.NUM_0)));
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            if (siglasInput.length() > 0) siglasInput.deleteCharAt(siglasInput.length() - 1);
        }
    }

    private void meterChar(char c) {
        if (siglasInput.length() >= 3) return;
        if (Character.isLetterOrDigit(c)) siglasInput.append(Character.toUpperCase(c));
    }

    @Override
    public void dibujar(SpriteBatch batch) {

        batch.setColor(0f, 0f, 0f, 0.85f);
        batch.draw(recursos.pixelBlanco, 0, 0, Main.ANCHO_MUNDO, Main.ALTO_MUNDO);
        batch.setColor(1f, 1f, 1f, 1f);

        float cx = Main.ANCHO_MUNDO / 2f;

        dibujarTextoCentrado(batch, fuenteTitulo, recursos.textos.t("victory_title"), cx, Main.ALTO_MUNDO - 260f);

        dibujarTextoWrapCentrado(
            batch,
            fuenteNormal,
            recursos.textos.t("victory_final_msg"),
            cx,
            Main.ALTO_MUNDO - 430f,
            Main.ANCHO_MUNDO * 0.90f
        );

        dibujarTextoCentrado(batch, fuenteNormal, recursos.textos.t("score") + ": " + puntuacion, cx, Main.ALTO_MUNDO - 560f);

        if (mostrandoRecord) {
            batch.setColor(0.15f, 0.65f, 1f, 0.25f);
            batch.draw(recursos.pixelBlanco, btnMenu.x, btnMenu.y, btnMenu.width, btnMenu.height);

            batch.setColor(1f, 1f, 1f, 0.35f);
            dibujarTextoCentradoAjustado(batch, fuenteBoton, recursos.textos.t("victory_back_menu"), cx, btnMenu.y + 95f, btnMenu.width * 0.88f);
            batch.setColor(1f, 1f, 1f, 1f);
        } else {
            dibujarBoton(batch, btnMenu, recursos.textos.t("victory_back_menu"));
        }

        if (mostrandoRecord) {
            dibujarPanelRecord(batch);
        }
    }

    private void dibujarPanelRecord(SpriteBatch batch) {
        float cx = Main.ANCHO_MUNDO / 2f;

        // Fondo panel
        batch.setColor(0f, 0f, 0f, 0.70f);
        batch.draw(recursos.pixelBlanco, recordPanelX, recordPanelY, recordPanelW, recordPanelH);
        batch.setColor(1f, 1f, 1f, 1f);

        // Título del panel (usa tituloPeque si lo tienes)
        dibujarTextoCentrado(batch, fuenteBoton, recursos.textos.t("record_title"), cx, recordPanelY + recordPanelH - 110f);

        // Puntos
        dibujarTextoCentrado(batch, fuenteNormal, recursos.textos.t("record_points", puntuacion), cx, recordPanelY + recordPanelH - 210f);

        // Label siglas
        dibujarTextoCentrado(batch, fuenteNormal, recursos.textos.t("record_initials_label"), cx, recordPanelY + recordPanelH - 320f);

        // Siglas (AAA / ___)
        String shown = siglasInput.toString();
        while (shown.length() < 3) shown += "_";
        dibujarTextoCentrado(batch, fuenteBoton, shown, cx, recordPanelY + recordPanelH - 380f);

        // Hint (wrap para que no pise el botón)
        dibujarTextoWrapCentrado(
            batch,
            fuenteNormal,
            recursos.textos.t("record_hint"),
            cx,
            recordPanelY + 260f,
            recordPanelW * 0.90f
        );

        // Botón continuar
        boolean enabled = siglasInput.length() == 3;

        if (enabled) batch.setColor(0.15f, 0.65f, 1f, 0.90f);
        else batch.setColor(0.15f, 0.65f, 1f, 0.35f);

        batch.draw(recursos.pixelBlanco,
            btnContinuarRecord.x, btnContinuarRecord.y,
            btnContinuarRecord.width, btnContinuarRecord.height);

        batch.setColor(1f, 1f, 1f, 1f);

        dibujarTextoCentradoAjustado(
            batch,
            fuenteBoton,
            recursos.textos.t("record_continue"),
            cx,
            btnContinuarRecord.y + btnContinuarRecord.height / 2f + 18f,
            btnContinuarRecord.width * 0.88f
        );
    }

    private void dibujarTextoCentrado(SpriteBatch batch, BitmapFont font, String texto, float centroX, float y) {
        layout.setText(font, texto);
        float x = centroX - layout.width / 2f;
        font.draw(batch, layout, x, y);
    }

    private void dibujarBoton(SpriteBatch batch, Rectangle r, String texto) {
        batch.setColor(0.15f, 0.65f, 1f, 0.85f);
        batch.draw(recursos.pixelBlanco, r.x, r.y, r.width, r.height);
        batch.setColor(1f, 1f, 1f, 1f);

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

    private void dibujarTextoWrapCentrado(
        SpriteBatch batch,
        BitmapFont font,
        String texto,
        float centroX,
        float yTop,
        float maxAncho
    ) {
        layout.setText(font, texto, Color.WHITE, maxAncho, Align.center, true);

        float x = centroX - maxAncho / 2f;
        font.draw(batch, layout, x, yTop);
    }

    @Override public void alRedimensionar(int ancho, int alto) { viewport.update(ancho, alto, true); }
    @Override public void alOcultar() {}

    @Override
    public void liberar() {
        // No liberar fuentes aquí (son compartidas).
    }
}
