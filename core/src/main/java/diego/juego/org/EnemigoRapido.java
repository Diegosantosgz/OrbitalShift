package diego.juego.org;

import com.badlogic.gdx.graphics.Texture;

public class EnemigoRapido extends Enemigo {

    public EnemigoRapido(float x, float y, float w, float h, float delayDisparo) {
        super(x, y, w, h, delayDisparo, 1);
        this.velocidadY = 320f;
    }

    @Override
    public Texture getTextura(Recursos recursos) {
        return recursos.enemigoRapido;
    }
}
