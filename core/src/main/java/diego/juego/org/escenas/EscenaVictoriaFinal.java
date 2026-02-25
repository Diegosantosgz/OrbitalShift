package diego.juego.org.escenas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
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

    private final BitmapFont fuente = new BitmapFont();
    private final GlyphLayout layout = new GlyphLayout();

    private final Rectangle btnMenu = new Rectangle(0, 0, 620f, 140f);

    // ===== RECORD (panel debajo) =====
    private boolean mostrandoRecord = false;
    private boolean recordGuardado = false;

    // SIGLAS (se dibujan aquí)
    private final StringBuilder siglasInput = new StringBuilder();

    // Para móvil (evita abrir el teclado mil veces)
    private boolean pidiendoSiglasMovil = false;

    private final Rectangle btnContinuarRecord = new Rectangle(0, 0, 760f, 150f);

    // Panel record (solo estética / layout)
    private final float recordPanelW = 980f;
    private final float recordPanelH = 760f;
    private float recordPanelX, recordPanelY;

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

        // Panel record centrado horizontal y colocado por debajo de los textos de victoria
        recordPanelX = (Main.ANCHO_MUNDO - recordPanelW) / 2f;
        recordPanelY = 220f;

        btnContinuarRecord.set(
            (Main.ANCHO_MUNDO - btnContinuarRecord.width) / 2f,
            recordPanelY + 40f,
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

            // IMPORTANTE: en móvil hay que usar getTextInput
            pedirSiglasMovil();
        }
    }

    private void pedirSiglasMovil() {
        if (pidiendoSiglasMovil) return;
        pidiendoSiglasMovil = true;

        // Abre el teclado del sistema (Android/iOS) y devuelve el texto aquí:
        Gdx.input.getTextInput(new TextInputListener() {
            @Override
            public void input(String text) {
                siglasInput.setLength(0);
                siglasInput.append(limpiar3(text));
                pidiendoSiglasMovil = false;
            }

            @Override
            public void canceled() {
                // Si cancela, dejamos vacío (o puedes poner "---" si prefieres)
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

        // ===== Si estamos pidiendo record, bloqueamos botón menú =====
        if (mostrandoRecord) {

            // Desktop / teclado físico: sigue funcionando como antes
            leerInputSiglas();

            // Back/Escape: no salir mientras pide siglas
            if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                return;
            }

            if (!Gdx.input.justTouched()) return;

            Vector3 v = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(v);

            // Si pulsa continuar sin 3 letras, en móvil reabrimos teclado
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

        // ===== Lo que ya tenías =====
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
        // Si ya tenemos 3 (por móvil), no sigas metiendo
        if (siglasInput.length() >= 3) {
            // permite backspace igualmente
            if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
                siglasInput.deleteCharAt(siglasInput.length() - 1);
            }
            return;
        }

        // Letras A-Z
        for (int key = Input.Keys.A; key <= Input.Keys.Z; key++) {
            if (Gdx.input.isKeyJustPressed(key)) {
                meterChar((char) ('A' + (key - Input.Keys.A)));
            }
        }

        // Números 0-9
        for (int key = Input.Keys.NUM_0; key <= Input.Keys.NUM_9; key++) {
            if (Gdx.input.isKeyJustPressed(key)) {
                meterChar((char) ('0' + (key - Input.Keys.NUM_0)));
            }
        }

        // Borrar
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

        // ===== Fondo =====
        batch.setColor(0f, 0f, 0f, 0.85f);
        batch.draw(recursos.pixelBlanco, 0, 0, Main.ANCHO_MUNDO, Main.ALTO_MUNDO);
        batch.setColor(1f, 1f, 1f, 1f);

        // ===== Tu victoria final ARRIBA (igual que antes) =====
        fuente.getData().setScale(6.0f);
        dibujarTextoCentrado(batch, recursos.textos.t("victory_title"), Main.ANCHO_MUNDO / 2f, Main.ALTO_MUNDO - 260f);

        fuente.getData().setScale(2.8f);
        dibujarTextoCentrado(batch, recursos.textos.t("victory_final_msg"), Main.ANCHO_MUNDO / 2f, Main.ALTO_MUNDO - 430f);

        fuente.getData().setScale(3.2f);
        dibujarTextoCentrado(batch, recursos.textos.t("score") + ": " + puntuacion, Main.ANCHO_MUNDO / 2f, Main.ALTO_MUNDO - 560f);

        fuente.getData().setScale(1.0f);

        // Botón volver al menú (si hay record, lo dibujamos apagado)
        if (mostrandoRecord) {
            batch.setColor(0.15f, 0.65f, 1f, 0.25f);
            batch.draw(recursos.pixelBlanco, btnMenu.x, btnMenu.y, btnMenu.width, btnMenu.height);
            batch.setColor(1f, 1f, 1f, 0.35f);
            fuente.getData().setScale(2.6f);
            dibujarTextoCentrado(batch, recursos.textos.t("victory_back_menu"), Main.ANCHO_MUNDO / 2f, btnMenu.y + 95f);
            fuente.getData().setScale(1.0f);
            batch.setColor(1f, 1f, 1f, 1f);
        } else {
            dibujarBoton(batch, btnMenu, recursos.textos.t("victory_back_menu"));
        }

        // ===== Panel record debajo =====
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

        // Texto record
        fuente.getData().setScale(5.6f);
        dibujarTextoCentrado(batch, recursos.textos.t("record_title"), cx, recordPanelY + recordPanelH - 120f);

        fuente.getData().setScale(2.6f);
        dibujarTextoCentrado(batch,
            recursos.textos.t("record_points", puntuacion),
            cx,
            recordPanelY + recordPanelH - 230f
        );

        fuente.getData().setScale(2.9f);
        dibujarTextoCentrado(batch, recursos.textos.t("record_initials_label"), cx, recordPanelY + recordPanelH - 360f);

        String shown = siglasInput.toString();
        while (shown.length() < 3) shown += "_";

        // >>> SUBIDO UN PELÍN (antes -430f)
        fuente.getData().setScale(4.6f);
        dibujarTextoCentrado(batch, shown, cx, recordPanelY + recordPanelH - 410f);

        fuente.getData().setScale(1.8f);
        dibujarTextoCentrado(batch, recursos.textos.t("record_hint"), cx, recordPanelY + 240f);

        // Botón continuar
        boolean enabled = siglasInput.length() == 3;

        if (enabled) batch.setColor(0.15f, 0.65f, 1f, 0.90f);
        else batch.setColor(0.15f, 0.65f, 1f, 0.35f);

        batch.draw(recursos.pixelBlanco,
            btnContinuarRecord.x, btnContinuarRecord.y,
            btnContinuarRecord.width, btnContinuarRecord.height);

        batch.setColor(1f, 1f, 1f, 1f);

        fuente.getData().setScale(2.8f);
        dibujarTextoCentrado(batch, recursos.textos.t("record_continue"), cx, btnContinuarRecord.y + 95f);

        fuente.getData().setScale(1.0f);
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

    @Override
    public void liberar() {
        fuente.dispose();
    }
}
