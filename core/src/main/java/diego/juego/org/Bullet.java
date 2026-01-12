package diego.juego.org;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;


public class Bullet {

    public float x, y;
    private float speed = 900f;
    private Texture texture;
    public Rectangle bounds;

    public Bullet(float x, float y) {
        this.x = x;
        this.y = y;

        texture = new Texture("laser_verde.png");
        bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    public void update(float delta) {
        y += speed * delta;
        bounds.setPosition(x, y);
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public boolean isOffScreen(float worldHeight) {
        return y > worldHeight;
    }

    public void dispose() {
        texture.dispose();
    }
}
