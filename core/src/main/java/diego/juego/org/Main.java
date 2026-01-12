package diego.juego.org;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter {

    private SpriteBatch batch;

    // Fondos
    private Texture fondoMuyLejano;
    private Texture fondoLejano;
    private Texture fondoCercano;

    // Nave
    private Texture xwing;

    // Música
    private Music musicaFondo;

    // Posición nave
    private float x, y;
    private float speed = 200f;
    private float scale = 0.10f;

    // Parallax offsets (vertical)
    private float offsetMuyLejano = 0;
    private float offsetLejano = 0;
    private float offsetCercano = 0;

    // Velocidades parallax
    private float velMuyLejano = 10f;
    private float velLejano = 20f;
    private float velCercano = 50f;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Cargar fondos
        fondoMuyLejano = new Texture("fondo_muy_lejano.png");
        fondoLejano = new Texture("fondo_lejano_nuevo.png");
        fondoCercano = new Texture("fondo_cercano_nuevo.png");

        // Cargar nave
        xwing = new Texture("x-wingHDCenital.png");

        // Centrar nave
        x = Gdx.graphics.getWidth() / 2f - (xwing.getWidth() * scale) / 2f;
        y = Gdx.graphics.getHeight() / 2f - (xwing.getHeight() * scale) / 2f;

        // ===== Música =====
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("musica_fondo.mp3"));
        musicaFondo.setLooping(true);   // repetir infinitamente
        musicaFondo.setVolume(0.5f);    // volumen 0 a 1
        musicaFondo.play();             // iniciar reproducción
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        float delta = Gdx.graphics.getDeltaTime();

        // ===== PARALLAX VERTICAL =====
        offsetMuyLejano -= velMuyLejano * delta;
        offsetLejano -= velLejano * delta;
        offsetCercano -= velCercano * delta;

        if (offsetMuyLejano <= -fondoMuyLejano.getHeight()) offsetMuyLejano = 0;
        if (offsetLejano <= -fondoLejano.getHeight()) offsetLejano = 0;
        if (offsetCercano <= -fondoCercano.getHeight()) offsetCercano = 0;

        // ===== CONTROLES =====
        if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Gyroscope)) {
            float gyroX = Gdx.input.getGyroscopeX();
            float gyroY = Gdx.input.getGyroscopeY();

            x -= gyroY * speed * delta;
            y += gyroX * speed * delta;
        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  x -= speed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) x += speed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.UP))    y += speed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  y -= speed * delta;
        }

        // Limitar dentro de pantalla
        x = Math.max(0, Math.min(x, Gdx.graphics.getWidth() - xwing.getWidth() * scale));
        y = Math.max(0, Math.min(y, Gdx.graphics.getHeight() - xwing.getHeight() * scale));

        // ===== DIBUJO =====
        batch.begin();

        // Fondo muy lejano
        batch.draw(fondoMuyLejano, 0, offsetMuyLejano);
        batch.draw(fondoMuyLejano, 0, offsetMuyLejano + fondoMuyLejano.getHeight());

        // Fondo lejano
        batch.draw(fondoLejano, 0, offsetLejano);
        batch.draw(fondoLejano, 0, offsetLejano + fondoLejano.getHeight());

        // Fondo cercano
        batch.draw(fondoCercano, 0, offsetCercano);
        batch.draw(fondoCercano, 0, offsetCercano + fondoCercano.getHeight());

        // Nave
        batch.draw(xwing, x, y, xwing.getWidth() * scale, xwing.getHeight() * scale);

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        fondoMuyLejano.dispose();
        fondoLejano.dispose();
        fondoCercano.dispose();
        xwing.dispose();
        musicaFondo.dispose();  // liberar recurso
    }
}
