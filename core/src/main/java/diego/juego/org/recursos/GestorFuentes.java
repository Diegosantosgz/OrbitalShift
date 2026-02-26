package diego.juego.org.recursos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class GestorFuentes {

    private BitmapFont titulo;
    private BitmapFont tituloPeque;
    private BitmapFont boton;
    private BitmapFont normal;


    public void cargar() {
        titulo = generar("fuente/Orbitron-Black.ttf", 140);
        tituloPeque = generar("fuente/Orbitron-Black.ttf", 90); // Ajuste para paneles
        boton  = generar("fuente/Orbitron-SemiBold.ttf", 60);
        normal = generar("fuente/Orbitron-Regular.ttf", 36);
    }


    private BitmapFont generar(String path, int size) {

        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(Gdx.files.internal(path));

        FreeTypeFontGenerator.FreeTypeFontParameter param =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        param.size = size;
        param.kerning = true;
        param.minFilter = Texture.TextureFilter.Linear;
        param.magFilter = Texture.TextureFilter.Linear;

        BitmapFont font = generator.generateFont(param);
        generator.dispose();

        return font;
    }

    public BitmapFont getTitulo() { return titulo; }
    public BitmapFont getTituloPeque() { return tituloPeque; }
    public BitmapFont getBoton() { return boton; }
    public BitmapFont getNormal() { return normal; }

    public void liberar() {
        if (titulo != null) titulo.dispose();
        if (tituloPeque != null) tituloPeque.dispose();
        if (boton != null) boton.dispose();
        if (normal != null) normal.dispose();
    }
}
