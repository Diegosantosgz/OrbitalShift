package diego.juego.org.escenas;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Input.TextInputListener;
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

public class EscenaNuevoRecord implements Escena {

    private final Recursos recursos;
    private final Viewport viewport;
    private final GestorEscenas gestor;
    private final int score;
    private final Runnable onDone;

    private final BitmapFont fuente = new BitmapFont();
    private final GlyphLayout layout = new GlyphLayout();

    private final Rectangle btnContinuar;

    private final StringBuilder iniciales = new StringBuilder(3);

    private boolean activo = false;

    // ANDROID CONTROL
    private boolean esAndroid = false;
    private boolean esperandoDialogo = false;
    private boolean dialogoMostrado = false;

    private final InputAdapter input = new InputAdapter() {
        @Override
        public boolean keyDown(int keycode) {
            if (!activo) return false;

            if (keycode == Input.Keys.BACKSPACE) {
                borrarLetra();
                return true;
            }
            if (keycode == Input.Keys.ENTER || keycode == Input.Keys.NUMPAD_ENTER) {
                confirmar();
                return true;
            }
            if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
                return true;
            }
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            if (!activo) return false;

            if (Character.isLetterOrDigit(character)) {
                if (iniciales.length() < 3) {
                    iniciales.append(Character.toUpperCase(character));
                }
                return true;
            }
            return false;
        }
    };

    public EscenaNuevoRecord(Recursos recursos, Viewport viewport, GestorEscenas gestor, int score, Runnable onDone) {
        this.recursos = recursos;
        this.viewport = viewport;
        this.gestor = gestor;
        this.score = score;
        this.onDone = onDone;

        float bw = 560f, bh = 120f;
        btnContinuar = new Rectangle((Main.ANCHO_MUNDO - bw) / 2f, 450f, bw, bh);
    }

    @Override
    public void alMostrar() {
        activo = true;

        esAndroid = (Gdx.app.getType() == Application.ApplicationType.Android);

        InputMultiplexer mux = new InputMultiplexer();
        mux.addProcessor(input);
        Gdx.input.setInputProcessor(mux);

        if (esAndroid) {
            if (!dialogoMostrado) {
                dialogoMostrado = true;
                esperandoDialogo = true;
                pedirSiglasAndroid();
            }
        } else {
            Gdx.input.setOnscreenKeyboardVisible(true);
        }
    }

    private void pedirSiglasAndroid() {
        Gdx.input.getTextInput(new TextInputListener() {
                                   @Override
                                   public void input(String text) {
                                       iniciales.setLength(0);

                                       for (int i = 0; i < text.length() && iniciales.length() < 3; i++) {
                                           char c = text.charAt(i);
                                           if (Character.isLetterOrDigit(c)) {
                                               iniciales.append(Character.toUpperCase(c));
                                           }
                                       }

                                       while (iniciales.length() < 3) iniciales.append('-');

                                       esperandoDialogo = false;
                                   }

                                   @Override
                                   public void canceled() {
                                       iniciales.setLength(0);
                                       iniciales.append("---");
                                       esperandoDialogo = false;
                                   }
                               },
            recursos.textos.t("newrecord_dialog_title"),
            recursos.textos.t("newrecord_dialog_hint"),
            recursos.textos.t("newrecord_dialog_example")
        );
    }

    private void borrarLetra() {
        int n = iniciales.length();
        if (n > 0) iniciales.deleteCharAt(n - 1);
    }

    private boolean confirmado = false;

    private void confirmar() {
        if (!activo || confirmado) return;
        confirmado = true;

        while (iniciales.length() < 3) iniciales.append('-');
        EstadoJuego.insertarTopScore(iniciales.toString(), score);

        activo = false;
        Gdx.input.setOnscreenKeyboardVisible(false);
        Gdx.input.setInputProcessor(null);

        if (onDone != null) onDone.run();
    }

    @Override
    public void actualizar(float delta) {
        if (!activo) return;
        if (esAndroid && esperandoDialogo) return;

        if (!Gdx.input.justTouched()) return;

        Vector3 v = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(v);

        if (btnContinuar.contains(v.x, v.y)) {
            confirmar();
        }
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        batch.setColor(0f, 0f, 0f, 0.75f);
        batch.draw(recursos.pixelBlanco, 0, 0, Main.ANCHO_MUNDO, Main.ALTO_MUNDO);
        batch.setColor(1f, 1f, 1f, 1f);

        fuente.getData().setScale(5.5f);
        dibujarCentrado(batch, recursos.textos.t("newrecord_title"), 1500f);

        fuente.getData().setScale(3.0f);
        dibujarCentrado(batch, recursos.textos.t("newrecord_points", score), 1320f);

        fuente.getData().setScale(3.4f);
        dibujarCentrado(batch, recursos.textos.t("newrecord_initials", iniciales.toString()), 1100f);

        fuente.getData().setScale(2.0f);
        dibujarCentrado(batch, recursos.textos.t("newrecord_hint"), 920f);

        dibujarBoton(batch, btnContinuar, recursos.textos.t("newrecord_continue"), true);

        fuente.getData().setScale(1.0f);
    }

    private void dibujarBoton(SpriteBatch batch, Rectangle r, String texto, boolean primario) {
        if (primario) batch.setColor(0.15f, 0.65f, 1f, 0.85f);
        else batch.setColor(0.85f, 0.25f, 0.25f, 0.85f);

        batch.draw(recursos.pixelBlanco, r.x, r.y, r.width, r.height);

        batch.setColor(0f, 0f, 0f, 0.25f);
        batch.draw(recursos.pixelBlanco, r.x, r.y, r.width, 6f);
        batch.draw(recursos.pixelBlanco, r.x, r.y + r.height - 6f, r.width, 6f);

        batch.setColor(1f, 1f, 1f, 1f);
        fuente.getData().setScale(2.4f);

        layout.setText(fuente, texto);
        float x = r.x + (r.width - layout.width) / 2f;
        float y = r.y + (r.height / 2f) + (layout.height / 2f);
        fuente.draw(batch, layout, x, y);

        fuente.getData().setScale(1.0f);
    }

    private void dibujarCentrado(SpriteBatch batch, String txt, float y) {
        layout.setText(fuente, txt);
        fuente.draw(batch, layout, (Main.ANCHO_MUNDO - layout.width) / 2f, y);
    }

    @Override public void alRedimensionar(int a, int b) {}

    @Override
    public void alOcultar() {
        activo = false;
        Gdx.input.setOnscreenKeyboardVisible(false);
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void liberar() {
        fuente.dispose();
    }
}
