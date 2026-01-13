package diego.juego.org;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.Timer;


import java.util.Iterator;

public class Main extends ApplicationAdapter {


    public static PlatformServices services;

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

    // SFX disparo
    private Sound sfxDisparo;
    private float sfxVolume = 0.7f;

    // Láser verde jugador
    private Texture laserVerde;
    private Array<Bullet> bullets;
    private float shootCooldown = 0.15f;
    private float shootTimer = 0f;

    // Enemigos + láser rojo
    private Texture enemyTex;
    private Texture laserRojo;
    private Array<Enemy> enemies;
    private Array<EnemyBullet> enemyBullets;
    private float enemySpawnTimer = 0f;
    private float enemySpawnDelay = 1.2f;

    // Vida jugador + colisión
    private Rectangle playerBounds;
    private int playerHp = 3;
    private float invulnTimer = 0f;
    private static final float INVULN_TIME = 0.45f;

    // Explosiones
    private Texture explosionTex;              // assets/explosion.png
    private Array<Explosion> explosions;

    // ===== GAME OVER UI =====
    private enum GameState { PLAYING, GAME_OVER }
    private GameState state = GameState.PLAYING;

    private Texture whitePixel;     // textura 1x1 para panel/botones
    private BitmapFont font;
    private GlyphLayout layout;

    private Rectangle btnRetry;
    private Rectangle btnExit;

    // Un pelín de delay para que se vea la explosión final antes de salir el panel
    private float gameOverDelayTimer = 0f;
    private static final float GAME_OVER_DELAY = 0.35f;

    // ===== CLASES =====
    private static class Explosion {
        float x, y;
        float size;
        float time = 0f;
        float duration = 0.55f;

        Explosion(float x, float y, float size) {
            this.x = x;
            this.y = y;
            this.size = size;
        }

        void update(float delta) { time += delta; }
        boolean finished() { return time >= duration; }

        float alpha() {
            float t = time / duration;
            return 1f - t;
        }

        float scalePop() {
            float t = time / duration;
            return 0.8f + (t * 0.7f);
        }
    }

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

        boolean canShoot() { return shootTimer >= shootDelay; }
        void resetShoot() { shootTimer = 0f; }
        boolean offScreen() { return y + h < 0; }
    }

    private static class EnemyBullet {
        float x, y;
        float width, height;
        float speed = 900f;
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

        boolean offScreen() { return y + height < 0; }
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

        sfxDisparo = Gdx.audio.newSound(Gdx.files.internal("sonido_disparo.ogg"));

        laserVerde = new Texture("laser_verde.png");
        bullets = new Array<>();

        enemyTex = new Texture("enemigo_normal.png");
        laserRojo = new Texture("laser_rojo.png");
        enemies = new Array<>();
        enemyBullets = new Array<>();

        explosionTex = new Texture("explosion.png");
        explosions = new Array<>();

        float shipW = xwing.getWidth() * scale;
        float shipH = xwing.getHeight() * scale;
        playerBounds = new Rectangle(x, y, shipW, shipH);

        // ===== UI GAME OVER =====
        whitePixel = createWhitePixel();
        font = new BitmapFont(); // luego podrás cambiar por font propio
        layout = new GlyphLayout();

        float bw = 520f, bh = 120f;
        btnRetry = new Rectangle((WORLD_WIDTH - bw) / 2f, 700f, bw, bh);
        btnExit  = new Rectangle((WORLD_WIDTH - bw) / 2f, 540f, bw, bh);
    }

    private Texture createWhitePixel() {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(1, 1, 1, 1);
        pm.fill();
        Texture t = new Texture(pm);
        pm.dispose();
        return t;
    }

    private void spawnEnemy() {
        float eW = enemyTex.getWidth() * scale;
        float eH = enemyTex.getHeight() * scale;

        float spawnX = MathUtils.random(0f, WORLD_WIDTH - eW);
        float spawnY = WORLD_HEIGHT + eH;

        float shootDelay = MathUtils.random(0.9f, 1.7f);
        enemies.add(new Enemy(spawnX, spawnY, eW, eH, shootDelay));
    }
    private void vibrateSafe(int ms) {
        try {
            if (services != null) services.vibrate(ms);
        } catch (Throwable ignored) {}
    }

    private void vibrateHit() {
        vibrateSafe(120);
    }

    private void vibrateDeathPattern() {
        try {
            if (services != null) {
                // golpe fuerte al morir
                services.vibrate(450);
                // patrón extra (queda guapo)
                services.vibratePattern(new long[]{0, 80, 60, 160, 60, 240});
            }
        } catch (Throwable ignored) {}
    }



    private void addExplosionCentered(float centerX, float centerY, float size) {
        float exX = centerX - size / 2f;
        float exY = centerY - size / 2f;
        explosions.add(new Explosion(exX, exY, size));
    }

    private void restartGame() {
        // reset estado
        state = GameState.PLAYING;
        gameOverDelayTimer = 0f;

        // reset jugador
        playerHp = 3;
        invulnTimer = 0f;

        float shipW = xwing.getWidth() * scale;
        float shipH = xwing.getHeight() * scale;
        x = WORLD_WIDTH / 2f - shipW / 2f;
        y = 200f;
        playerBounds.set(x, y, shipW, shipH);

        // limpiar arrays
        bullets.clear();
        enemies.clear();
        enemyBullets.clear();
        explosions.clear();

        // reset timers
        enemySpawnTimer = 0f;
        shootTimer = 0f;
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        float delta = Gdx.graphics.getDeltaTime();

        // ===== Parallax (siempre, incluso en game over) =====
        oMuy -= vMuy * delta;
        oLej -= vLej * delta;
        oCer -= vCer * delta;

        if (oMuy <= -WORLD_HEIGHT) oMuy += WORLD_HEIGHT;
        if (oLej <= -WORLD_HEIGHT) oLej += WORLD_HEIGHT;
        if (oCer <= -WORLD_HEIGHT) oCer += WORLD_HEIGHT;

        if (state == GameState.PLAYING) {
            updatePlaying(delta);
        } else {
            updateGameOver(delta);
        }

        // ===== Render =====
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        drawParallaxLayer(fondoMuyLejano, oMuy);
        drawParallaxLayer(fondoLejano, oLej);
        drawParallaxLayer(fondoCercano, oCer);

        // Enemigos
        for (Enemy e : enemies) batch.draw(enemyTex, e.x, e.y, e.w, e.h);

        // Balas rojas
        for (EnemyBullet b : enemyBullets) batch.draw(laserRojo, b.x, b.y, b.width, b.height);

        // Balas verdes
        for (Bullet b : bullets) batch.draw(laserVerde, b.x, b.y, b.width, b.height);

        // Jugador (si está vivo)
        float shipW = xwing.getWidth() * scale;
        float shipH = xwing.getHeight() * scale;
        if (playerHp > 0) batch.draw(xwing, x, y, shipW, shipH);

        // Explosiones
        for (Explosion ex : explosions) {
            float a = ex.alpha();
            float s = ex.scalePop();

            batch.setColor(1f, 1f, 1f, a);

            float drawW = ex.size * s;
            float drawH = ex.size * s;

            float cx = ex.x + ex.size / 2f;
            float cy = ex.y + ex.size / 2f;
            float drawX = cx - drawW / 2f;
            float drawY = cy - drawH / 2f;

            batch.draw(explosionTex, drawX, drawY, drawW, drawH);
            batch.setColor(1f, 1f, 1f, 1f);
        }

        // UI Game Over (por encima)
        if (state == GameState.GAME_OVER && gameOverDelayTimer >= GAME_OVER_DELAY) {
            drawGameOverUI();
        }

        batch.end();
    }

    private void updatePlaying(float delta) {
        // Movimiento jugador
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

        // Invulnerabilidad
        if (invulnTimer > 0f) invulnTimer -= delta;

        // Disparo verde
        shootTimer += delta;
        boolean shootPressed = Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isTouched();
        if (shootPressed && shootTimer >= shootCooldown && playerHp > 0) {
            shootTimer = 0f;

            float bulletW = laserVerde.getWidth() * scale;
            float bulletH = laserVerde.getHeight() * scale;

            float leftWingX  = x + (shipW * 0.15f) - (bulletW / 2f);
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

        // Spawn enemigos
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

        // Colisión verde vs enemigo
        for (Iterator<Bullet> bit = bullets.iterator(); bit.hasNext(); ) {
            Bullet b = bit.next();
            boolean hit = false;

            for (Iterator<Enemy> eit = enemies.iterator(); eit.hasNext(); ) {
                Enemy e = eit.next();
                if (b.bounds.overlaps(e.bounds)) {
                    addExplosionCentered(e.x + e.w / 2f, e.y + e.h / 2f, e.w * 1.25f);
                    eit.remove();
                    hit = true;
                    break;
                }
            }
            if (hit) bit.remove();
        }

        // Colisión rojo vs jugador
        if (playerHp > 0 && invulnTimer <= 0f) {
            for (Iterator<EnemyBullet> it = enemyBullets.iterator(); it.hasNext(); ) {
                EnemyBullet b = it.next();
                if (b.bounds.overlaps(playerBounds)) {
                    it.remove();
                    playerHp--;
                    invulnTimer = INVULN_TIME;

                    // 📳 Vibración al impacto
                    vibrateHit();

                    // Explosión por impacto
                    addExplosionCentered(x + shipW / 2f, y + shipH / 2f, shipW * 1.1f);

                    if (playerHp <= 0) {
                        playerHp = 0; // seguridad

                        // 📳 Vibración fuerte al morir
                        vibrateDeathPattern();

                        // Explosión final grande
                        addExplosionCentered(x + shipW / 2f, y + shipH / 2f, shipW * 1.9f);
                        state = GameState.GAME_OVER;
                        gameOverDelayTimer = 0f;
                    }
                    break;
                }
            }
        }



        // Update explosiones
        for (Iterator<Explosion> it = explosions.iterator(); it.hasNext(); ) {
            Explosion ex = it.next();
            ex.update(delta);
            if (ex.finished()) it.remove();
        }
    }

    private void updateGameOver(float delta) {
        // deja el fondo y explosiones corriendo
        gameOverDelayTimer += delta;

        for (Iterator<Explosion> it = explosions.iterator(); it.hasNext(); ) {
            Explosion ex = it.next();
            ex.update(delta);
            if (ex.finished()) it.remove();
        }

        // Si aún no mostramos la UI, no aceptamos clicks
        if (gameOverDelayTimer < GAME_OVER_DELAY) return;

        // Touch / click
        if (Gdx.input.justTouched()) {
            float sx = Gdx.input.getX();
            float sy = Gdx.input.getY();

            // Convertir coordenadas pantalla -> mundo
            float worldX = sx * (WORLD_WIDTH / Gdx.graphics.getWidth());
            float worldY = WORLD_HEIGHT - (sy * (WORLD_HEIGHT / Gdx.graphics.getHeight()));

            if (btnRetry.contains(worldX, worldY)) {
                restartGame();
            } else if (btnExit.contains(worldX, worldY)) {
                Gdx.app.exit();
            }
        }

        // Tecla ESC para salir (desktop)
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    private void drawGameOverUI() {
        // Fondo oscuro
        batch.setColor(0f, 0f, 0f, 0.65f);
        batch.draw(whitePixel, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        // Panel central
        float panelW = 820f;
        float panelH = 720f;
        float panelX = (WORLD_WIDTH - panelW) / 2f;
        float panelY = (WORLD_HEIGHT - panelH) / 2f;

        batch.setColor(0.08f, 0.08f, 0.10f, 0.90f);
        batch.draw(whitePixel, panelX, panelY, panelW, panelH);

        // Línea arriba
        batch.setColor(1f, 0.2f, 0.2f, 0.90f);
        batch.draw(whitePixel, panelX, panelY + panelH - 10f, panelW, 10f);

        // Título (MUCHO más grande)
        batch.setColor(1f, 1f, 1f, 1f);
        font.getData().setScale(7.0f);
        drawCenteredText("GAME OVER", WORLD_WIDTH / 2f, panelY + panelH - 140f);

        // Subtítulo (más grande)
        font.getData().setScale(4.5f);
        drawCenteredText("Tu nave ha sido destruida", WORLD_WIDTH / 2f, panelY + panelH - 235f);

        // Botones
        drawButton(btnRetry, "REINTENTAR", true);
        drawButton(btnExit, "SALIR", false);

        // Hint (más grande)
        font.getData().setScale(2f);
        batch.setColor(1f, 1f, 1f, 0.75f);
        drawCenteredText("Pulsa un botón o ESC para salir", WORLD_WIDTH / 2f, panelY + 95f);

        // Reset
        batch.setColor(1f, 1f, 1f, 1f);
        font.getData().setScale(1.0f);
    }


    private void drawButton(Rectangle r, String text, boolean primary) {
        // Fondo botón
        if (primary) batch.setColor(0.15f, 0.65f, 1f, 0.85f);
        else batch.setColor(0.85f, 0.25f, 0.25f, 0.85f);
        batch.draw(whitePixel, r.x, r.y, r.width, r.height);

        // Borde
        batch.setColor(0f, 0f, 0f, 0.25f);
        batch.draw(whitePixel, r.x, r.y, r.width, 6f);
        batch.draw(whitePixel, r.x, r.y + r.height - 6f, r.width, 6f);
        batch.draw(whitePixel, r.x, r.y, 6f, r.height);
        batch.draw(whitePixel, r.x + r.width - 6f, r.y, 6f, r.height);

        // Texto (MUCHO más grande)
        batch.setColor(1f, 1f, 1f, 1f);
        font.getData().setScale(2.2f);
        drawCenteredText(text, r.x + r.width / 2f, r.y + r.height / 2f + 22f);

        // Reset
        font.getData().setScale(1.0f);
    }


    private void drawCenteredText(String text, float centerX, float y) {
        layout.setText(font, text);
        float x = centerX - layout.width / 2f;
        font.draw(batch, layout, x, y);
    }

    private void drawParallaxLayer(Texture tex, float offsetY) {
        batch.setColor(1f, 1f, 1f, 1f);
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
        if (batch != null) batch.dispose();

        if (fondoMuyLejano != null) fondoMuyLejano.dispose();
        if (fondoLejano != null) fondoLejano.dispose();
        if (fondoCercano != null) fondoCercano.dispose();

        if (xwing != null) xwing.dispose();
        if (laserVerde != null) laserVerde.dispose();
        if (enemyTex != null) enemyTex.dispose();
        if (laserRojo != null) laserRojo.dispose();
        if (explosionTex != null) explosionTex.dispose();

        if (whitePixel != null) whitePixel.dispose();
        if (font != null) font.dispose();

        if (sfxDisparo != null) sfxDisparo.dispose();
        if (musicaFondo != null) musicaFondo.dispose();
    }
}
