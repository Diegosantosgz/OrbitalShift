package diego.juego.org;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

public class EscenaRecords implements Escena {

    private final Recursos recursos;
    private final Viewport viewport;
    private final GestorEscenas gestor;

    private final BitmapFont fuente = new BitmapFont();
    private final GlyphLayout layout = new GlyphLayout();

    private final Rectangle btnVolver;
    private final Rectangle btnReset; // opcional

    public EscenaRecords(Recursos recursos, Viewport viewport, GestorEscenas gestor) {
        this.recursos = recursos;
        this.viewport = viewport;
        this.gestor = gestor;

        float bw = 520f, bh = 110f;
        btnVolver = new Rectangle((Main.ANCHO_MUNDO - bw) / 2f, 220f, bw, bh);
        btnReset  = new Rectangle((Main.ANCHO_MUNDO - bw) / 2f, 360f, bw, bh);
    }

    @Override public void alMostrar() {}

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
        // fondo oscurecido
        batch.setColor(0f, 0f, 0f, 0.70f);
        batch.draw(recursos.pixelBlanco, 0, 0, Main.ANCHO_MUNDO, Main.ALTO_MUNDO);
        batch.setColor(1f, 1f, 1f, 1f);

        // título
        fuente.getData().setScale(6.0f);
        dibujarCentrado(batch, "RECORDS", 1500f);

        // lista top 10
        fuente.getData().setScale(3.0f);
        float y = 1300f;
        float step = 92f;

        for (int i = 0; i < 10; i++) {
            int score = EstadoJuego.topScores[i];
            String linea = (i + 1) + ".  " + score;
            dibujarCentrado(batch, linea, y);
            y -= step;
        }

        // botones
        dibujarBoton(batch, btnReset, "RESETEAR", false);
        dibujarBoton(batch, btnVolver, "VOLVER", true);

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
        dibujarCentradoEnRect(batch, texto, r);
        fuente.getData().setScale(1.0f);
    }

    private void dibujarCentrado(SpriteBatch batch, String txt, float y) {
        layout.setText(fuente, txt);
        fuente.draw(batch, layout, (Main.ANCHO_MUNDO - layout.width) / 2f, y);
    }

    private void dibujarCentradoEnRect(SpriteBatch batch, String txt, Rectangle r) {
        layout.setText(fuente, txt);
        float x = r.x + (r.width - layout.width) / 2f;
        float y = r.y + (r.height / 2f) + (layout.height / 2f);
        fuente.draw(batch, layout, x, y);
    }

    @Override public void alRedimensionar(int a, int b) {}
    @Override public void alOcultar() {}
    @Override public void liberar() { fuente.dispose(); }
}
