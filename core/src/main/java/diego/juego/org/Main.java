package diego.juego.org;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Iterator;

public class Main extends ApplicationAdapter {

    private SpriteBatch batch;

    private OrthographicCamera camera;
    private Viewport viewport;

    private static final float WORLD_WIDTH = 1080f;
    private static final float WORLD_HEIGHT = 1920f;

    private Texture fondoMuyLejano, fondoLejano, fondoCercano;

    private Texture xwing;
    private float x, y;
    private float speed = 500f;
    private float scale = 0.10f;

    private float oMuy = 0, oLej = 0, oCer = 0;
    private float vMuy = 10f, vLej = 20f, vCer = 50f;

    private Music musicaFondo;

    // 🔊 SFX disparo
    private Sound sfxDisparo;
    private float sfxVolume = 0.7f; // 0..1

    // ====== Láser verde jugador ======
    private Texture laserVerde;
    private Array<Bullet> bullets;
    private float shootCooldown = 0.15f;
    private float shootTimer = 0f;

    // ====== Enemigos + láser rojo ======
    private Texture enemyTex;
    private Texture laserRojo;
    private Array<Enemy> enemies;
    private Array<EnemyBullet> enemyBullets;

    private float enemySpawnTimer = 0f;
    private float enemySpawnDelay = 1.2f; // cada cuánto aparece un enemigo (segundos)

    // ====== Vida jugador + colisión ======
    private Rectangle playerBounds;
    private int playerHp = 3;
    private float invulnTimer = 0f;
    private static final float INVULN_TIME = 0.45f;

    // ====== Bala verde (jugador) ======
    private static class Bullet {
        float x, y;
        float width, height;
        float speed = 1500f;
        Rectangle bounds;

        Bullet(float x, float y, float w, float h) {
            this.x = x;
            this.y = y;
            this.width = w;
            this.height = h;
            this.bounds = new Rectangle(x, y, w, h);
        }

        void update(float delta) {
            y += speed * delta;
            bounds.setPosition(x, y);
        }
    }

    // ====== Enemigo ======
    private static class Enemy {
        float x, y;
        float w, h;
        float speed = 180f;
        Rectangle bounds;

        float shootTimer = 0f;
        float shootDelay;

        Enemy(float x, float y, float w, float h, float shootDelay) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.shootDelay = shootDelay;
            this.bounds = new Rectangle(x, y, w, h);
        }

        void update(float delta) {
            y -= speed * delta;
            shootTimer += delta;
            bounds.setPosition(x, y);
        }

        boolean canShoot() {
            return shootTimer >= shootDelay;
        }

        void resetShoot() {
            shootTimer = 0f;
        }

        boolean offScreen() {
            return y + h < 0;
        }
    }

    // ====== Bala roja (enemigo) ======
    private static class EnemyBullet {
        float x, y;
        float width, height;
        float speed = 900f; // hacia abajo
        Rectangle bounds;

        EnemyBullet(float x, float y, float w, float h) {
            this.x = x;
            this.y = y;
            this.width = w;
            this.height = h;
            this.bounds = new Rectangle(x, y, w, h);
        }

        void update(float delta) {
            y -= speed * delta;
            bounds.setPosition(x, y);
        }

        boolean offScreen() {
            return y + height < 0;
        }
    }

    @Override
    public void create() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);

        fondoMuyLejano = new Texture("fondo_muy_lejano.png");
        fondoLejano = new Texture("fondo_lejano_nuevo.png");
        fondoCercano = new Texture("fondo_cercano_nuevo.png");

        xwing = new Texture("x-wingHDCenital.png");
        x = WORLD_WIDTH / 2f - (xwing.getWidth() * scale) / 2f;
        y = 200f;

        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("musica_fondo.mp3"));
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(0.5f);
        musicaFondo.play();

        // 🔊 Cargar SFX disparo
        sfxDisparo = Gdx.audio.newSound(Gdx.files.internal("sonido_disparo.ogg"));

        // Láser verde jugador
        laserVerde = new Texture("laser_verde.png");
        bullets = new Array<>();

        // Enemigo + láser rojo (ASEGÚRATE de tener estos dos archivos en assets/)
        enemyTex = new Texture("enemigo_normal.png");
        laserRojo = new Texture("laser_rojo.png");
        enemies = new Array<>();
        enemyBullets = new Array<>();

        float shipW = xwing.getWidth() * scale;
        float shipH = xwing.getHeight() * scale;
        playerBounds = new Rectangle(x, y, shipW, shipH);
    }

    private void spawnEnemy() {
        float eW = enemyTex.getWidth() * scale;
        float eH = enemyTex.getHeight() * scale;

        float spawnX = MathUtils.random(0f, WORLD_WIDTH - eW);
        float spawnY = WORLD_HEIGHT + eH;

        float shootDelay = MathUtils.random(0.9f, 1.7f); // disparo aleatorio
        enemies.add(new Enemy(spawnX, spawnY, eW, eH, shootDelay));
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        float delta = Gdx.graphics.getDeltaTime();

        // ===== Parallax =====
        oMuy -= vMuy * delta;
        oLej -= vLej * delta;
        oCer -= vCer * delta;

        if (oMuy <= -WORLD_HEIGHT) oMuy += WORLD_HEIGHT;
        if (oLej <= -WORLD_HEIGHT) oLej += WORLD_HEIGHT;
        if (oCer <= -WORLD_HEIGHT) oCer += WORLD_HEIGHT;

        // ===== Movimiento jugador =====
        float dx = 0f, dy = 0f;
        if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
            dx = -Gdx.input.getAccelerometerX();
            dy = -Gdx.input.getAccelerometerY();
        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  dx = -1f;
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) dx =  1f;
            if (Gdx.input.isKeyPressed(Input.Keys.UP))    dy =  1f;
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  dy = -1f;
        }

        x += dx * speed * delta;
        y += dy * speed * delta;

        float shipW = xwing.getWidth() * scale;
        float shipH = xwing.getHeight() * scale;

        x = Math.max(0, Math.min(x, WORLD_WIDTH - shipW));
        y = Math.max(0, Math.min(y, WORLD_HEIGHT - shipH));

        playerBounds.set(x, y, shipW, shipH);

        // ===== Invulnerabilidad =====
        if (invulnTimer > 0f) invulnTimer -= delta;

        // ===== Disparo verde =====
        shootTimer += delta;
        boolean shootPressed = Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isTouched();

        if (shootPressed && shootTimer >= shootCooldown && playerHp > 0) {
            shootTimer = 0f;

            float bulletW = laserVerde.getWidth() * scale;
            float bulletH = laserVerde.getHeight() * scale;

            float leftWingX = x + (shipW * 0.15f) - (bulletW / 2f);
            float rightWingX = x + (shipW * 0.85f) - (bulletW / 2f);
            float gunY = y + (shipH * 0.7f);

            bullets.add(new Bullet(leftWingX, gunY, bulletW, bulletH));
            bullets.add(new Bullet(rightWingX, gunY, bulletW, bulletH));

            sfxDisparo.play(sfxVolume);
        }

        // Update balas verdes
        for (Iterator<Bullet> it = bullets.iterator(); it.hasNext(); ) {
            Bullet b = it.next();
            b.update(delta);
            if (b.y > WORLD_HEIGHT) it.remove();
        }

        // ===== Spawn enemigos =====
        enemySpawnTimer += delta;
        if (enemySpawnTimer >= enemySpawnDelay && playerHp > 0) {
            spawnEnemy();
            enemySpawnTimer = 0f;
        }

        // Update enemigos + disparo rojo
        for (Iterator<Enemy> it = enemies.iterator(); it.hasNext(); ) {
            Enemy e = it.next();
            e.update(delta);

            if (e.canShoot()) {
                e.resetShoot();

                float bW = laserRojo.getWidth() * scale;
                float bH = laserRojo.getHeight() * scale;

                float bx = e.x + (e.w / 2f) - (bW / 2f);
                float by = e.y - (bH * 0.2f);

                enemyBullets.add(new EnemyBullet(bx, by, bW, bH));
            }

            if (e.offScreen()) it.remove();
        }

        // Update balas rojas
        for (Iterator<EnemyBullet> it = enemyBullets.iterator(); it.hasNext(); ) {
            EnemyBullet b = it.next();
            b.update(delta);
            if (b.offScreen()) it.remove();
        }

        // ===== Colisión verde vs enemigo (mueres enemigo) =====
        for (Iterator<Bullet> bit = bullets.iterator(); bit.hasNext(); ) {
            Bullet b = bit.next();
            boolean hit = false;

            for (Iterator<Enemy> eit = enemies.iterator(); eit.hasNext(); ) {
                Enemy e = eit.next();
                if (b.bounds.overlaps(e.bounds)) {
                    eit.remove(); // enemigo muerto
                    hit = true;
                    break;
                }
            }
            if (hit) bit.remove(); // consume bala
        }

        // ===== Colisión rojo vs jugador =====
        if (playerHp > 0 && invulnTimer <= 0f) {
            for (Iterator<EnemyBullet> it = enemyBullets.iterator(); it.hasNext(); ) {
                EnemyBullet b = it.next();
                if (b.bounds.overlaps(playerBounds)) {
                    it.remove();
                    playerHp--;
                    invulnTimer = INVULN_TIME;
                    break;
                }
            }
        }

        // ===== Render =====
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        drawParallaxLayer(fondoMuyLejano, oMuy);
        drawParallaxLayer(fondoLejano, oLej);
        drawParallaxLayer(fondoCercano, oCer);

        // Enemigos
        for (Enemy e : enemies) {
            batch.draw(enemyTex, e.x, e.y, e.w, e.h);
        }

        // Balas rojas
        for (EnemyBullet b : enemyBullets) {
            batch.draw(laserRojo, b.x, b.y, b.width, b.height);
        }

        // Balas verdes
        for (Bullet b : bullets) {
            batch.draw(laserVerde, b.x, b.y, b.width, b.height);
        }

        // Jugador
        if (playerHp > 0) {
            batch.draw(xwing, x, y, shipW, shipH);
        }

        batch.end();
    }

    private void drawParallaxLayer(Texture tex, float offsetY) {
        batch.draw(tex, 0, offsetY, WORLD_WIDTH, WORLD_HEIGHT);
        batch.draw(tex, 0, offsetY + WORLD_HEIGHT, WORLD_WIDTH, WORLD_HEIGHT);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
        if (musicaFondo != null && musicaFondo.isPlaying()) musicaFondo.pause();
    }

    @Override
    public void resume() {
        if (musicaFondo != null && !musicaFondo.isPlaying()) musicaFondo.play();
    }

    @Override
    public void dispose() {
        batch.dispose();
        fondoMuyLejano.dispose();
        fondoLejano.dispose();
        fondoCercano.dispose();
        xwing.dispose();
        laserVerde.dispose();
        enemyTex.dispose();
        laserRojo.dispose();

        if (sfxDisparo != null) sfxDisparo.dispose();
        if (musicaFondo != null) musicaFondo.dispose();
    }
}
