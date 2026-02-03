package diego.juego.org;

import com.badlogic.gdx.graphics.Texture;

public class EnemigoLento extends Enemigo {

    public EnemigoLento(float x, float y, float w, float h, float delayDisparo) {
        super(x, y, w, h, delayDisparo, 3);
        this.velocidadY = 110f;
    }

    @Override
    public Texture getTextura(Recursos recursos) {
        return recursos.enemigoLento;
    }
}

