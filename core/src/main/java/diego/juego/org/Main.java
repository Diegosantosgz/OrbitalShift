package diego.juego.org;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter {

    private SpriteBatch batch;

    private Texture fondoCerca, fondoLejano, fondoMuyLejano;
    private Texture xwing;
    private TextureRegion xwingRegion;

    private float x, y;
    private float speed = 200f;
    private float scale = 0.10f;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Fondos temporales de prueba
     //   fondoCerca = new Texture("fondo_cerca.png");
      //  fondoLejano = new Texture("fondo_lejano.png");
      //  fondoMuyLejano = new Texture("fondo_muy_lejano.png");

        // Nave
        xwing = new Texture("x-wingHDCenital.png");
        xwingRegion = new TextureRegion(xwing);

        // Posición inicial
        x = Gdx.graphics.getWidth() / 2f - (xwingRegion.getRegionWidth() * scale) / 2f;
        y = Gdx.graphics.getHeight() / 2f - (xwingRegion.getRegionHeight() * scale) / 2f;
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        float delta = Gdx.graphics.getDeltaTime();

        // Movimiento
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

        // Limitar nave
        x = Math.max(0, Math.min(x, Gdx.graphics.getWidth() - xwingRegion.getRegionWidth() * scale));
        y = Math.max(0, Math.min(y, Gdx.graphics.getHeight() - xwingRegion.getRegionHeight() * scale));

        // Dibujar fondos y nave
        batch.begin();
      //  batch.draw(fondoMuyLejano, -x * 0.05f, -y * 0.05f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      //  batch.draw(fondoLejano,    -x * 0.1f,  -y * 0.1f,  Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
      //  batch.draw(fondoCerca,     -x * 0.3f,  -y * 0.3f,  Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch.draw(
            xwingRegion,
            x, y,
            (xwingRegion.getRegionWidth() * scale) / 2f,
            (xwingRegion.getRegionHeight() * scale) / 2f,
            xwingRegion.getRegionWidth() * scale,
            xwingRegion.getRegionHeight() * scale,
            1f, 1f,
            0f
        );
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        xwing.dispose();
       // fondoCerca.dispose();
       // fondoLejano.dispose();
       // fondoMuyLejano.dispose();
    }
}
