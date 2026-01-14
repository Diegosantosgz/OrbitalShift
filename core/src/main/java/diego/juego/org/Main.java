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

    private Sound sfxDisparo;

    private Sound sfxExplosion;

    private float sfxVolume = 0.1f;

    private Texture laserVerde;
    private Array<Bullet> bullets;
    private float shootCooldown = 0.15f;
    private float shootTimer = 0f;

    private Texture enemyTex;
    private Texture laserRojo;
    private Texture scoreIcon;

    private Array<Enemy> enemies;
    private Array<EnemyBullet> enemyBullets;
    private float enemySpawnTimer = 0f;
    private float enemySpawnDelay = 1.2f;
    private float difficultyTimer = 0f;

    // delay dinámico (empieza fácil)
    private float enemySpawnDelayStart = 3.2f;   // al inicio (menos enemigos)
    private float enemySpawnDelayMin   = 0.55f;  // límite (más difícil)
    private float difficultyRampTime   = 90f;    // segundos para llegar al mínimo


    private Rectangle playerBounds;
    private int playerHp = 3;
    private int score = 0;
    private float invulnTimer = 0f;
    private static final float INVULN_TIME = 0.45f;

    private Texture vidaTex;
    private float vidaSize = 70f;
    private float vidaPadding = 14f;
    private float vidaMarginLeft = 24f;
    private float vidaMarginTop = 24f;


    private Texture explosionTex;
    private Array<Explosion> explosions;

    private enum GameState { PLAYING, GAME_OVER }
    private GameState state = GameState.PLAYING;

    private Texture whitePixel;
    private BitmapFont font;
    private GlyphLayout layout;

    private Rectangle btnRetry;
    private Rectangle btnExit;

    private float gameOverDelayTimer = 0f;
    private static final float GAME_OVER_DELAY = 0.35f;

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

        scoreIcon = new Texture("estrella_record.png");


        xwing = new Texture("x-wingHDCenital.png");
        x = WORLD_WIDTH / 2f - (xwing.getWidth() * scale) / 2f;
        y = 200f;

        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("musica_fondo.mp3"));
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(0.3f);
        musicaFondo.play();

        sfxDisparo = Gdx.audio.newSound(Gdx.files.internal("sonido_disparo.ogg"));
        sfxExplosion = Gdx.audio.newSound(Gdx.files.internal("explosion.ogg"));


        laserVerde = new Texture("laser_verde.png");
        bullets = new Array<>();

        enemyTex = new Texture("enemigo_normal.png");
        laserRojo = new Texture("laser_rojo.png");
        enemies = new Array<>();
        enemyBullets = new Array<>();

        explosionTex = new Texture("explosion.png");
        explosions = new Array<>();
        vidaTex = new Texture("vida.png");

        float shipW = xwing.getWidth() * scale;
        float shipH = xwing.getHeight() * scale;
        playerBounds = new Rectangle(x, y, shipW, shipH);

        whitePixel = createWhitePixel();
        font = new BitmapFont();
        layout = new GlyphLayout();

        float bw = 520f, bh = 120f;
        btnRetry = new Rectangle((WORLD_WIDTH - bw) / 2f, 700f, bw, bh);
        btnExit  = new Rectangle((WORLD_WIDTH - bw) / 2f, 540f, bw, bh);

        enemySpawnDelay = enemySpawnDelayStart;
        difficultyTimer = 0f;
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
                services.vibrate(450);
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
        state = GameState.PLAYING;
        gameOverDelayTimer = 0f;

        playerHp = 3;
        invulnTimer = 0f;

        float shipW = xwing.getWidth() * scale;
        float shipH = xwing.getHeight() * scale;
        x = WORLD_WIDTH / 2f - shipW / 2f;
        y = 200f;
        playerBounds.set(x, y, shipW, shipH);

        bullets.clear();
        enemies.clear();
        enemyBullets.clear();
        explosions.clear();

        enemySpawnTimer = 0f;
        shootTimer = 0f;

        enemySpawnDelay = enemySpawnDelayStart;
        difficultyTimer = 0f;

        score = 0;

    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        float delta = Gdx.graphics.getDeltaTime();

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

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        drawParallaxLayer(fondoMuyLejano, oMuy);
        drawParallaxLayer(fondoLejano, oLej);
        drawParallaxLayer(fondoCercano, oCer);

        for (Enemy e : enemies) batch.draw(enemyTex, e.x, e.y, e.w, e.h);
        for (EnemyBullet b : enemyBullets) batch.draw(laserRojo, b.x, b.y, b.width, b.height);
        for (Bullet b : bullets) batch.draw(laserVerde, b.x, b.y, b.width, b.height);

        float shipW = xwing.getWidth() * scale;
        float shipH = xwing.getHeight() * scale;
        if (playerHp > 0) batch.draw(xwing, x, y, shipW, shipH);

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

        drawLivesUI();
        drawScoreUI();


        if (state == GameState.GAME_OVER && gameOverDelayTimer >= GAME_OVER_DELAY) {
            drawGameOverUI();
        }

        batch.end();
    }

    private void updatePlaying(float delta) {
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

        if (invulnTimer > 0f) invulnTimer -= delta;

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

        for (Iterator<Bullet> it = bullets.iterator(); it.hasNext(); ) {
            Bullet b = it.next();
            b.update(delta);
            if (b.y > WORLD_HEIGHT) it.remove();
        }

        difficultyTimer += delta;

// 0 -> 1 según pasan los segundos
        float t = MathUtils.clamp(difficultyTimer / difficultyRampTime, 0f, 1f);

// delay va bajando poco a poco
        enemySpawnDelay = MathUtils.lerp(enemySpawnDelayStart, enemySpawnDelayMin, t);


        enemySpawnTimer += delta;
        if (enemySpawnTimer >= enemySpawnDelay && playerHp > 0) {
            spawnEnemy();
            enemySpawnTimer = 0f;
        }

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

        for (Iterator<EnemyBullet> it = enemyBullets.iterator(); it.hasNext(); ) {
            EnemyBullet b = it.next();
            b.update(delta);
            if (b.offScreen()) it.remove();
        }

        for (Iterator<Bullet> bit = bullets.iterator(); bit.hasNext(); ) {
            Bullet b = bit.next();
            boolean hit = false;

            for (Iterator<Enemy> eit = enemies.iterator(); eit.hasNext(); ) {
                Enemy e = eit.next();
                if (b.bounds.overlaps(e.bounds)) {
                    addExplosionCentered(e.x + e.w / 2f, e.y + e.h / 2f, e.w * 1.25f);
                    if (sfxExplosion != null) {
                        float vol = 0.75f + MathUtils.random(0.15f);
                        float pitch = 0.90f + MathUtils.random(0.20f);
                        sfxExplosion.play(vol, pitch, 0f);
                    }

                    score += 100;


                    eit.remove();

                    hit = true;
                    break;
                }
            }
            if (hit) bit.remove();
        }

        if (playerHp > 0 && invulnTimer <= 0f) {
            for (Iterator<EnemyBullet> it = enemyBullets.iterator(); it.hasNext(); ) {
                EnemyBullet b = it.next();
                if (b.bounds.overlaps(playerBounds)) {
                    it.remove();
                    playerHp--;
                    invulnTimer = INVULN_TIME;

                    vibrateHit();

                    addExplosionCentered(x + shipW / 2f, y + shipH / 2f, shipW * 1.1f);
                    if (sfxExplosion != null) {
                        sfxExplosion.play(0.3f, 1.1f, 0f);
                    }


                    if (playerHp <= 0) {
                        playerHp = 0;

                        vibrateDeathPattern();

                        addExplosionCentered(x + shipW / 2f, y + shipH / 2f, shipW * 1.9f);
                        if (sfxExplosion != null) {
                            sfxExplosion.play(1.0f, 0.85f, 0f);
                        }

                        state = GameState.GAME_OVER;
                        gameOverDelayTimer = 0f;

                    }
                    break;
                }
            }
        }

        for (Iterator<Explosion> it = explosions.iterator(); it.hasNext(); ) {
            Explosion ex = it.next();
            ex.update(delta);
            if (ex.finished()) it.remove();
        }
    }
    private void drawScoreUI() {
        font.getData().setScale(3.2f);

        String text = String.valueOf(score);
        layout.setText(font, text);

        float padding = 20f;
        float iconSize = 64f;

        float totalWidth = iconSize + padding + layout.width;

        float x = WORLD_WIDTH - totalWidth - 30f;
        float y = WORLD_HEIGHT - 40f;

        // dibuja la estrella
        batch.draw(scoreIcon, x, y - iconSize + 10f, iconSize, iconSize);

        // dibuja el texto a la derecha de la estrella
        font.draw(batch, layout, x + iconSize + padding, y);

        font.getData().setScale(1.0f);
    }



    private void drawLivesUI() {
        if (vidaTex == null) return;

        int maxLives = 3;
        for (int i = 0; i < maxLives; i++) {
            float drawX = vidaMarginLeft + i * (vidaSize + vidaPadding);
            float drawY = WORLD_HEIGHT - vidaMarginTop - vidaSize;

            if (i < playerHp) {
                batch.setColor(1f, 1f, 1f, 1f);
            } else {
                batch.setColor(1f, 1f, 1f, 0.25f);
            }

            batch.draw(vidaTex, drawX, drawY, vidaSize, vidaSize);
        }

        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void updateGameOver(float delta) {
        gameOverDelayTimer += delta;

        for (Iterator<Explosion> it = explosions.iterator(); it.hasNext(); ) {
            Explosion ex = it.next();
            ex.update(delta);
            if (ex.finished()) it.remove();
        }

        if (gameOverDelayTimer < GAME_OVER_DELAY) return;

        if (Gdx.input.justTouched()) {
            float sx = Gdx.input.getX();
            float sy = Gdx.input.getY();

            float worldX = sx * (WORLD_WIDTH / Gdx.graphics.getWidth());
            float worldY = WORLD_HEIGHT - (sy * (WORLD_HEIGHT / Gdx.graphics.getHeight()));

            if (btnRetry.contains(worldX, worldY)) {
                restartGame();
            } else if (btnExit.contains(worldX, worldY)) {
                Gdx.app.exit();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    private void drawGameOverUI() {
        batch.setColor(0f, 0f, 0f, 0.65f);
        batch.draw(whitePixel, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        float panelW = 820f;
        float panelH = 720f;
        float panelX = (WORLD_WIDTH - panelW) / 2f;
        float panelY = (WORLD_HEIGHT - panelH) / 2f;

        batch.setColor(0.08f, 0.08f, 0.10f, 0.90f);
        batch.draw(whitePixel, panelX, panelY, panelW, panelH);

        batch.setColor(1f, 0.2f, 0.2f, 0.90f);
        batch.draw(whitePixel, panelX, panelY + panelH - 10f, panelW, 10f);

        batch.setColor(1f, 1f, 1f, 1f);
        font.getData().setScale(7.0f);
        drawCenteredText("GAME OVER", WORLD_WIDTH / 2f, panelY + panelH - 140f);

        font.getData().setScale(4.5f);
        drawCenteredText("Tu nave ha sido destruida", WORLD_WIDTH / 2f, panelY + panelH - 235f);

        drawButton(btnRetry, "REINTENTAR", true);
        drawButton(btnExit, "SALIR", false);

        font.getData().setScale(2f);
        batch.setColor(1f, 1f, 1f, 0.75f);
        drawCenteredText("Pulsa un botón o ESC para salir", WORLD_WIDTH / 2f, panelY + 95f);

        batch.setColor(1f, 1f, 1f, 1f);
        font.getData().setScale(1.0f);
    }

    private void drawButton(Rectangle r, String text, boolean primary) {
        if (primary) batch.setColor(0.15f, 0.65f, 1f, 0.85f);
        else batch.setColor(0.85f, 0.25f, 0.25f, 0.85f);
        batch.draw(whitePixel, r.x, r.y, r.width, r.height);

        batch.setColor(0f, 0f, 0f, 0.25f);
        batch.draw(whitePixel, r.x, r.y, r.width, 6f);
        batch.draw(whitePixel, r.x, r.y + r.height - 6f, r.width, 6f);
        batch.draw(whitePixel, r.x, r.y, 6f, r.height);
        batch.draw(whitePixel, r.x + r.width - 6f, r.y, 6f, r.height);

        batch.setColor(1f, 1f, 1f, 1f);
        font.getData().setScale(2.2f);
        drawCenteredText(text, r.x + r.width / 2f, r.y + r.height / 2f + 22f);

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
        if (vidaTex != null) vidaTex.dispose();
        if (sfxExplosion != null) sfxExplosion.dispose();
        if (scoreIcon != null) scoreIcon.dispose();


    }
}
