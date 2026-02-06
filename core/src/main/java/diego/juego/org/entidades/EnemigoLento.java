package diego.juego.org.entidades;

import com.badlogic.gdx.graphics.Texture;

import diego.juego.org.recursos.Recursos;

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

