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

public class EscenaRecords implements Escena {

    private final Recursos recursos;
    private final Viewport viewport;
    private final GestorEscenas gestor;

    private final BitmapFont fuenteTitulo;
    private final BitmapFont fuenteLista;
    private final BitmapFont fuenteBoton;
    private final GlyphLayout layout;

    private final Rectangle btnVolver;
    private final Rectangle btnReset;

    public EscenaRecords(Recursos recursos, Viewport viewport, GestorEscenas gestor) {
        this.recursos = recursos;
        this.viewport = viewport;
        this.gestor = gestor;

        this.fuenteTitulo = recursos.fuentes.getTitulo();
        this.fuenteLista = recursos.fuentes.getNormal();
        this.fuenteBoton = recursos.fuentes.getBoton();
        this.layout = new GlyphLayout();

        float bw = 520f, bh = 110f;
        btnVolver = new Rectangle((Main.ANCHO_MUNDO - bw) / 2f, 90f, bw, bh);
        btnReset  = new Rectangle((Main.ANCHO_MUNDO - bw) / 2f, 230f, bw, bh);
    }

    @Override public void alMostrar() { }

    @Override
    public void actualizar(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gestor.cambiarA(new EscenaMenu(recursos, viewport, gestor));
            return;
        }

        if (!Gdx.input.justTouched()) return;

        Vector3 v = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(v);

        if (btnVolver.contains(v.x, v.y)) {
            gestor.cambiarA(new EscenaMenu(recursos, viewport, gestor));
            return;
        }

        if (btnReset.contains(v.x, v.y)) {
            EstadoJuego.resetTop();
            return;
        }
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        batch.setColor(0f, 0f, 0f, 0.70f);
        batch.draw(recursos.pixelBlanco, 0, 0, Main.ANCHO_MUNDO, Main.ALTO_MUNDO);
        batch.setColor(1f, 1f, 1f, 1f);

        dibujarCentrado(batch, fuenteTitulo, recursos.textos.t("records_title"), 1500f);

        float y = 1300f;
        float step = 92f;

        for (int i = 0; i < 10; i++) {
            String linea = (i + 1) + ".  " + EstadoJuego.getSiglas(i) + "   " + EstadoJuego.getScore(i);
            dibujarCentrado(batch, fuenteLista, linea, y);
            y -= step;
        }

        dibujarBoton(batch, btnReset,  recursos.textos.t("records_reset"), false);
        dibujarBoton(batch, btnVolver, recursos.textos.t("records_back"), true);
    }

    private void dibujarBoton(SpriteBatch batch, Rectangle r, String texto, boolean primario) {
        if (primario) batch.setColor(0.15f, 0.65f, 1f, 0.85f);
        else batch.setColor(0.85f, 0.25f, 0.25f, 0.85f);

        batch.draw(recursos.pixelBlanco, r.x, r.y, r.width, r.height);

        batch.setColor(0f, 0f, 0f, 0.25f);
        batch.draw(recursos.pixelBlanco, r.x, r.y, r.width, 6f);
        batch.draw(recursos.pixelBlanco, r.x, r.y + r.height - 6f, r.width, 6f);

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

    private void dibujarCentrado(SpriteBatch batch, BitmapFont font, String txt, float y) {
        layout.setText(font, txt);
        float x = (Main.ANCHO_MUNDO - layout.width) / 2f;
        font.draw(batch, layout, x, y);
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

    @Override public void alRedimensionar(int ancho, int alto) { viewport.update(ancho, alto, true); }
    @Override public void alOcultar() {}

    @Override
    public void liberar() {
        // No liberar fuentes aquí (son compartidas y las libera Recursos.liberar()).
    }
}
