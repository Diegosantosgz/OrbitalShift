package diego.juego.org;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Parallax vertical simple con 3 capas.
 */
public class Parallax {

    private float oMuy = 0f, oLej = 0f, oCer = 0f;
    private float vMuy = 10f, vLej = 20f, vCer = 50f;

    public void actualizar(float delta) {
        oMuy -= vMuy * delta;
        oLej -= vLej * delta;
        oCer -= vCer * delta;

        if (oMuy <= -Main.ALTO_MUNDO) oMuy += Main.ALTO_MUNDO;
        if (oLej <= -Main.ALTO_MUNDO) oLej += Main.ALTO_MUNDO;
        if (oCer <= -Main.ALTO_MUNDO) oCer += Main.ALTO_MUNDO;
    }

    public void dibujar(SpriteBatch batch, Texture muyLejano, Texture lejano, Texture cercano) {
        dibujarCapa(batch, muyLejano, oMuy);
        dibujarCapa(batch, lejano, oLej);
        dibujarCapa(batch, cercano, oCer);
    }

    private void dibujarCapa(SpriteBatch batch, Texture tex, float offsetY) {
        if (tex == null) return;
        batch.draw(tex, 0, offsetY, Main.ANCHO_MUNDO, Main.ALTO_MUNDO);
        batch.draw(tex, 0, offsetY + Main.ALTO_MUNDO, Main.ANCHO_MUNDO, Main.ALTO_MUNDO);
    }
}
