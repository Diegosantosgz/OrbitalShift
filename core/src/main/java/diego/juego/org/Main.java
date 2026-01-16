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
import com.badlogic.gdx.math.Vector3;


import java.util.Iterator;

public class Main extends ApplicationAdapter {



    // Menu
    private Rectangle btnPlay, btnOptions, btnCredits, btnHelp, btnQuit;

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

    private float touchMoveDeadZone = 22f;   // zona muerta en px (ajusta)
    private float touchMoveSpeed = 750f;     // velocidad del movimiento táctil (ajusta)


    private float oMuy = 0, oLej = 0, oCer = 0;
    private float vMuy = 10f, vLej = 20f, vCer = 50f;

    // ===== SCORE ANIM =====
    private float scorePopTimer = 0f;
    private static final float SCORE_POP_DURATION = 0.18f;
    private float scoreScaleBase = 3.2f; // tu escala normal

    // ===== TOUCHPAD DINÁMICO (solo si multitouchEnabled) =====
    private int movePointer = -1;           // qué dedo controla el pad
    private boolean touchpadActive = false;

    private float padBaseX = 0f, padBaseY = 0f;   // centro del pad
    private float padKnobX = 0f, padKnobY = 0f;   // centro del knob
    private float padRadius = 155f;               // radio base (ajusta)
    private float padKnobRadius = 60f;            // radio knob (ajusta)

    private float touchpadMaxSpeed = 650f;        // velocidad máx (ajusta)
    private float padDx = 0f, padDy = 0f;         // dirección normalizada [-1..1]



    private Music musicaFondo;

    private Sound sfxDisparo;

    private Sound sfxExplosion;

    private Sound sfxGravedad;

    private Sound sfxEscudo;

    private Sound sfxRomperEscudo;

    private Sound sfxCuracion;



    private float sfxVolume = 0.1f;

    private Texture laserVerde;



    // ===== TEXTURAS TOUCHPAD =====
    private Texture touchpadTex;      // touchpad.png (círculo)


    // ===== OPTIONS: MULTITOUCH =====
    private boolean multitouchEnabled = true;   // por defecto ON
    private Rectangle btnToggleMultitouch;


    // ===== ITEM ESCUDO =====
    private Texture escudoItemTex;
    private Texture naveEscudoTex;

    private float shieldScaleMultiplier = 1.75f; // Tamaño de la nave con escudo


    private boolean shieldActive = false;

    private float shieldSpawnTimer = 0f;
    private float shieldSpawnDelay = 14f; // cada cuantos segundos puede salir

    private Rectangle shieldBounds;
    private float shieldX, shieldY;
    private float shieldSpeed = 180f;
    private boolean shieldVisible = false;


    // ===== ITEM VIDA EXTRA =====
    private boolean hpItemVisible = false;
    private float hpItemSpawnTimer = 0f;
    private float hpItemSpawnDelay = 16f; // cada cuantos segundos puede salir (ajustable)

    private Rectangle hpItemBounds;
    private float hpItemX, hpItemY;
    private float hpItemSpeed = 170f; // velocidad bajada (ajustable)




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





    // ===== GRAVEDAD / AGUJERO NEGRO =====
    private Texture fuerzaGravTex;

    private float gravitySpawnTimer = 0f;
    private float gravitySpawnDelay = 9f;      // cada cuantos segundos intenta salir (ajustable)
    private float gravityDuration = 4.0f;      // cuanto dura en pantalla (ajustable)
    private float gravityTimer = 0f;
    private boolean gravityActive = false;

    private float gravityX = 0f;
    private float gravityY = 0f;

    private float gravityStrength = 300f;      // fuerza en "pixeles/segundo" (ajustable)
    private float gravitySideMargin = 60f;     // pegado al borde un poco
    private float gravitySpriteSize = 220f;    // tamaño del sprite (ajustable)

    // ===== GRAVEDAD: ROTACIÓN =====
    private float gravityRotation = 0f;          // grados
    private float gravityRotationSpeed = 50f;   // grados/seg (sube/baja esto)


    // ===== CALIBRACIÓN ACELERÓMETRO =====
    private boolean accelCalibrated = false;
    private float accelZeroX = 0f;
    private float accelZeroY = 0f;
    private float accelDeadZone = 0.08f; // zona muerta (ajustable)

    private Texture explosionTex;
    private Array<Explosion> explosions;

    private enum GameState { MENU, PLAYING, OPTIONS, CREDITS, HELP, GAME_OVER }
    private GameState state = GameState.MENU;

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

        btnToggleMultitouch = new Rectangle((WORLD_WIDTH - 720f) / 2f, 900f, 720f, 120f);


        fondoMuyLejano = new Texture("fondo_muy_lejano.png");
        fondoLejano = new Texture("fondo_lejano_nuevo.png");
        fondoCercano = new Texture("fondo_cercano_nuevo.png");

        scoreIcon = new Texture("estrella_record.png");

        fuerzaGravTex = new Texture("fuerza_gravitacional.png");

        escudoItemTex = new Texture("escudo.png");
        naveEscudoTex = new Texture("nave_escudo.png");
        sfxEscudo = Gdx.audio.newSound(Gdx.files.internal("sonido_recoger_escudo.ogg"));
        sfxRomperEscudo = Gdx.audio.newSound(
            Gdx.files.internal("sonido_romperse_escudo.ogg"));
        sfxCuracion = Gdx.audio.newSound(Gdx.files.internal("sonido_curacion.ogg"));

        touchpadTex = new Texture("touchpad.png");






        xwing = new Texture("x-wingHDCenital.png");
        x = WORLD_WIDTH / 2f - (xwing.getWidth() * scale) / 2f;
        y = 200f;

        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("musica_fondo.mp3"));
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(0.3f);
        musicaFondo.play();

        sfxDisparo = Gdx.audio.newSound(Gdx.files.internal("sonido_disparo.ogg"));
        sfxExplosion = Gdx.audio.newSound(Gdx.files.internal("explosion.ogg"));
        sfxGravedad = Gdx.audio.newSound(Gdx.files.internal("sonido_gravitacional.ogg"));




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

        float bw = 620f, bh = 120f;
        float startY = 1080f;     // ajusta si quieres más arriba/abajo
        float gap = 26f;

        float x0 = (WORLD_WIDTH - bw) / 2f;

        btnPlay    = new Rectangle(x0, startY,                bw, bh);
        btnOptions = new Rectangle(x0, startY - (bh+gap)*1f,  bw, bh);
        btnCredits = new Rectangle(x0, startY - (bh+gap)*2f,  bw, bh);
        btnHelp    = new Rectangle(x0, startY - (bh+gap)*3f,  bw, bh);
        btnQuit    = new Rectangle(x0, startY - (bh+gap)*4f,  bw, bh);



        float bwGO = 520f, bhGO = 120f;
        btnRetry = new Rectangle((WORLD_WIDTH - bwGO) / 2f, 700f, bwGO, bhGO);
        btnExit  = new Rectangle((WORLD_WIDTH - bwGO) / 2f, 540f, bwGO, bhGO);


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

        gravitySpawnTimer = 0f;
        gravityTimer = 0f;
        gravityActive = false;
        gravityX = 0f;
        gravityY = 0f;

        shieldActive = false;
        shieldVisible = false;
        shieldSpawnTimer = 0f;

        shieldX = 0f;
        shieldY = 0f;
        shieldBounds = null;
        accelCalibrated = false;

        hpItemVisible = false;
        hpItemSpawnTimer = 0f;
        hpItemBounds = null;
        hpItemX = 0f;
        hpItemY = 0f;


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
        } else if (state == GameState.GAME_OVER) {
            updateGameOver(delta);
        } else {
            updateMenuScreens(delta); // MENU / OPTIONS / CREDITS / HELP
        }



        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();


        drawParallaxLayer(fondoMuyLejano, oMuy);
        drawParallaxLayer(fondoLejano, oLej);
        drawParallaxLayer(fondoCercano, oCer);

        // Touchpad dinámico (solo en juego y solo si multitouch está ON)
        if (state == GameState.PLAYING && multitouchEnabled) {
            drawTouchpad();
        }


        // ===== MENÚ / PANTALLAS =====
        if (state == GameState.MENU) {
            drawMenu();
            batch.end();
            return;
        }
        if (state == GameState.OPTIONS) {
            drawOptionsScreen();
            batch.end();
            return;
        }
        if (state == GameState.CREDITS) {
            drawSimpleScreen("CRÉDITOS", "Hecho por Diego\nToca para volver");
            batch.end();
            return;
        }
        if (state == GameState.HELP) {
            drawSimpleScreen("AYUDA", "Inclina el móvil para moverte\nToca para disparar\nToca para volver");
            batch.end();
            return;
        }


        // ===== ITEM ESCUDO =====
        if (shieldVisible && shieldBounds != null) {
            batch.draw(
                escudoItemTex,
                shieldX,
                shieldY,
                shieldBounds.width,
                shieldBounds.height
            );
        }
        // ===== ITEM VIDA =====
        if (hpItemVisible && hpItemBounds != null) {
            batch.draw(
                vidaTex,
                hpItemX,
                hpItemY,
                hpItemBounds.width,
                hpItemBounds.height
            );
        }




        if (gravityActive && fuerzaGravTex != null) {
            batch.draw(
                fuerzaGravTex,
                gravityX, gravityY,                          // posición
                gravitySpriteSize / 2f, gravitySpriteSize / 2f, // origen (centro)
                gravitySpriteSize, gravitySpriteSize,        // tamaño
                1f, 1f,                                      // escala
                gravityRotation,                             // rotación (grados)
                0, 0,                                        // srcX, srcY
                fuerzaGravTex.getWidth(), fuerzaGravTex.getHeight(), // srcW, srcH
                false, false                                 // flip
            );
        }


        for (Enemy e : enemies) batch.draw(enemyTex, e.x, e.y, e.w, e.h);
        for (EnemyBullet b : enemyBullets) batch.draw(laserRojo, b.x, b.y, b.width, b.height);
        for (Bullet b : bullets) batch.draw(laserVerde, b.x, b.y, b.width, b.height);

        float baseW = xwing.getWidth() * scale;
        float baseH = xwing.getHeight() * scale;

        if (playerHp > 0) {
            if (shieldActive) {
                float w = baseW * shieldScaleMultiplier;
                float h = baseH * shieldScaleMultiplier;

                float drawX = x - (w - baseW) / 2f;
                float drawY = y - (h - baseH) / 2f;

                batch.draw(naveEscudoTex, drawX, drawY, w, h);
            } else {
                batch.draw(xwing, x, y, baseW, baseH);
            }
        }



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


        if (multitouchEnabled) {

            // 1) Si aún no tenemos dedo de movimiento, buscamos uno en la mitad izquierda
            if (!touchpadActive) {
                for (int p = 0; p < 5; p++) {
                    if (!Gdx.input.isTouched(p)) continue;

                    Vector3 vv = new Vector3(Gdx.input.getX(p), Gdx.input.getY(p), 0);
                    viewport.unproject(vv);

                    // mitad izquierda -> activar touchpad
                    if (vv.x < WORLD_WIDTH * 0.5f) {
                        movePointer = p;
                        touchpadActive = true;

                        padBaseX = vv.x;
                        padBaseY = vv.y;
                        padKnobX = padBaseX;
                        padKnobY = padBaseY;

                        padDx = 0f;
                        padDy = 0f;
                        break;
                    }
                }
            }

            // 2) Si el dedo de movimiento sigue tocando, actualizamos dirección
            if (touchpadActive) {
                if (movePointer >= 0 && Gdx.input.isTouched(movePointer)) {

                    Vector3 vv = new Vector3(Gdx.input.getX(movePointer), Gdx.input.getY(movePointer), 0);
                    viewport.unproject(vv);

                    float vx = vv.x - padBaseX;
                    float vy = vv.y - padBaseY;

                    // clamp dentro del radio
                    float len = (float)Math.sqrt(vx * vx + vy * vy);
                    if (len > padRadius) {
                        float s = padRadius / len;
                        vx *= s;
                        vy *= s;
                        len = padRadius;
                    }

                    padKnobX = padBaseX + vx;
                    padKnobY = padBaseY + vy;

                    // dirección normalizada [-1..1]
                    padDx = vx / padRadius;
                    padDy = vy / padRadius;

                } else {
                    // soltó el dedo -> desactivar pad
                    touchpadActive = false;
                    movePointer = -1;
                    padDx = 0f;
                    padDy = 0f;
                }
            }

            // 3) Mover nave con la dirección del pad
            dx = padDx;
            dy = padDy;

            x += dx * touchpadMaxSpeed * delta;
            y += dy * touchpadMaxSpeed * delta;

        } else {
            // ===== tu control normal por acelerómetro (igual que lo tienes) =====
            if (!accelCalibrated && Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
                accelZeroX = Gdx.input.getAccelerometerX();
                accelZeroY = Gdx.input.getAccelerometerY();
                accelCalibrated = true;
            }

            if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
                dx = -(Gdx.input.getAccelerometerX() - accelZeroX);
                dy = -(Gdx.input.getAccelerometerY() - accelZeroY);

                if (Math.abs(dx) < accelDeadZone) dx = 0f;
                if (Math.abs(dy) < accelDeadZone) dy = 0f;

            } else {
                if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  dx = -1f;
                if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) dx =  1f;
                if (Gdx.input.isKeyPressed(Input.Keys.UP))    dy =  1f;
                if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  dy = -1f;
            }

            x += dx * speed * delta;
            y += dy * speed * delta;
        }



        // ===== GRAVEDAD: arrastre lateral =====
        if (gravityActive && playerHp > 0) {

            float shipW = xwing.getWidth() * scale;

            float holeCenterX = gravityX + gravitySpriteSize / 2f;
            float shipCenterX = x + shipW / 2f;

            float dir = Math.signum(holeCenterX - shipCenterX);

            float dist = Math.abs(holeCenterX - shipCenterX);
            float normalized = MathUtils.clamp(1f - (dist / 700f), 0.2f, 1f);

            x += dir * gravityStrength * normalized * delta;
        }


        float shipW = xwing.getWidth() * scale;
        float shipH = xwing.getHeight() * scale;

        x = Math.max(0, Math.min(x, WORLD_WIDTH - shipW));
        y = Math.max(0, Math.min(y, WORLD_HEIGHT - shipH));
        playerBounds.set(x, y, shipW, shipH);

        if (invulnTimer > 0f) invulnTimer -= delta;

        shootTimer += delta;
        boolean shootPressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);

        if (multitouchEnabled) {
            for (int p = 0; p < 5; p++) {
                if (p == movePointer) continue;       // <- importante
                if (!Gdx.input.isTouched(p)) continue;

                Vector3 vv = new Vector3(Gdx.input.getX(p), Gdx.input.getY(p), 0);
                viewport.unproject(vv);

                if (vv.x >= WORLD_WIDTH * 0.5f) {
                    shootPressed = true;
                    break;
                }
            }
        } else {
            shootPressed = shootPressed || Gdx.input.isTouched();
        }



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

        // ===== GRAVEDAD: spawn ocasional =====
        gravitySpawnTimer += delta;

        if (!gravityActive && gravitySpawnTimer >= gravitySpawnDelay && playerHp > 0) {
            gravitySpawnTimer = 0f;

            if (MathUtils.random() < 0.6f) {
                spawnGravityWell();
            }
        }

        if (gravityActive) {
            gravityTimer -= delta;
            gravityRotation += gravityRotationSpeed * delta;

            gravityRotation %= 360f;

            if (gravityTimer <= 0f) {
                gravityActive = false;
            }
        }

        // ===== ESCUDO: spawn =====
        shieldSpawnTimer += delta;

        if (!shieldVisible && shieldSpawnTimer >= shieldSpawnDelay && !shieldActive) {
            shieldSpawnTimer = 0f;

            if (MathUtils.random() < 0.6f) { // probabilidad
                spawnShieldItem();
            }
        }





        if (shieldVisible) {
            shieldY -= shieldSpeed * delta;
            shieldBounds.setPosition(shieldX, shieldY);

            if (shieldY + shieldBounds.height < 0) {
                shieldVisible = false;
            }
        }

        if (shieldVisible && shieldBounds.overlaps(playerBounds)) {
            shieldVisible = false;
            shieldActive = true;

            if (sfxEscudo != null) {
                sfxEscudo.play(0.6f);
            }
        }

        // ===== VIDA: spawn =====
        hpItemSpawnTimer += delta;

// Solo spawnea si NO estás ya a tope (3)
        if (!hpItemVisible && hpItemSpawnTimer >= hpItemSpawnDelay && playerHp < 3) {
            hpItemSpawnTimer = 0f;

            if (MathUtils.random() < 0.55f) { // probabilidad (ajustable)
                spawnHpItem();
            }
        }
        if (hpItemVisible && hpItemBounds != null) {
            hpItemY -= hpItemSpeed * delta;
            hpItemBounds.setPosition(hpItemX, hpItemY);

            if (hpItemY + hpItemBounds.height < 0) {
                hpItemVisible = false;
            }
        }
        if (hpItemVisible && hpItemBounds.overlaps(playerBounds)) {
            hpItemVisible = false;

            playerHp = Math.min(3, playerHp + 1);

            if (sfxCuracion != null) {
                sfxCuracion.play(0.7f);
            }


            // (opcional) mini feedback: vibración suave
            // vibrateSafe(60);

            // (opcional) sonido: si luego le pones uno, aquí iría
        }



        if (scorePopTimer > 0f) scorePopTimer -= delta;


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
                    triggerScorePop();


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

                    if (shieldActive) {
                        shieldActive = false;
                        invulnTimer = 0.15f;

                        if (sfxRomperEscudo != null) {
                            sfxRomperEscudo.play(0.9f);
                        }

                    } else {
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
                    }

                    break; // ✅ importante: sales tras 1 impacto
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
        float extra = 0f;
        if (scorePopTimer > 0f) {
            float t = scorePopTimer / SCORE_POP_DURATION;
            extra = (float)Math.sin(t * Math.PI) * 0.9f;
        }
        font.getData().setScale(scoreScaleBase + extra);

        String text = String.valueOf(score);
        layout.setText(font, text);

        float padding = 20f;
        float iconSize = 64f;

        float totalWidth = iconSize + padding + layout.width;

        float x = WORLD_WIDTH - totalWidth - 30f;
        float y = WORLD_HEIGHT - 40f;

        batch.draw(scoreIcon, x, y - iconSize + 10f, iconSize, iconSize);
        font.draw(batch, layout, x + iconSize + padding, y);

        font.getData().setScale(1.0f);
    }
    private void spawnShieldItem() {
        shieldVisible = true;

        float size = 90f;
        shieldX = MathUtils.random(40f, WORLD_WIDTH - size - 40f);
        shieldY = WORLD_HEIGHT + size;

        shieldBounds = new Rectangle(shieldX, shieldY, size, size);
    }
    private void spawnHpItem() {
        hpItemVisible = true;

        float size = 90f;
        hpItemX = MathUtils.random(40f, WORLD_WIDTH - size - 40f);
        hpItemY = WORLD_HEIGHT + size;

        hpItemBounds = new Rectangle(hpItemX, hpItemY, size, size);
    }

    private void triggerScorePop() {
        scorePopTimer = SCORE_POP_DURATION;
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
    private void updateMenuScreens(float delta) {

        // BACK / ESC siempre
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (state == GameState.MENU) {
                Gdx.app.exit();
            } else {
                state = GameState.MENU;
            }
            return;
        }

        if (!Gdx.input.justTouched()) return;

        // Coordenadas correctas con viewport (FitViewport)
        Vector3 v = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(v);
        float worldX = v.x;
        float worldY = v.y;

        if (state == GameState.MENU) {
            if (btnPlay.contains(worldX, worldY)) {
                restartGame();
                state = GameState.PLAYING;
                return;
            }
            if (btnOptions.contains(worldX, worldY)) { state = GameState.OPTIONS; return; }
            if (btnCredits.contains(worldX, worldY)) { state = GameState.CREDITS; return; }
            if (btnHelp.contains(worldX, worldY))    { state = GameState.HELP; return; }
            if (btnQuit.contains(worldX, worldY))    { Gdx.app.exit(); return; }
            return;
        }

        if (state == GameState.OPTIONS) {
            // si toca el botón -> toggle y se queda
            if (btnToggleMultitouch.contains(worldX, worldY)) {
                multitouchEnabled = !multitouchEnabled;

                // reset del touchpad para que no se quede pillado
                touchpadActive = false;
                movePointer = -1;
                padDx = 0f;
                padDy = 0f;

                return;
            }

            // si toca fuera -> volver al menú
            state = GameState.MENU;
            return;
        }

        // CREDITS / HELP: tocar vuelve al menú
        state = GameState.MENU;
    }




    private void drawMenu() {
        // Fondo oscurecido encima del parallax
        batch.setColor(0f, 0f, 0f, 0.55f);
        batch.draw(whitePixel, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        batch.setColor(1f, 1f, 1f, 1f);

        font.getData().setScale(7.0f);
        drawCenteredText("ORBITAL SHIFT", WORLD_WIDTH / 2f, 1520f);

        font.getData().setScale(2.2f);
        drawMenuButton(btnPlay, "JUGAR");
        drawMenuButton(btnOptions, "OPCIONES");
        drawMenuButton(btnCredits, "CRÉDITOS");
        drawMenuButton(btnHelp, "AYUDA");
        drawMenuButton(btnQuit, "SALIR");

        font.getData().setScale(1.0f);
    }

    private void drawMenuButton(Rectangle r, String text) {
        batch.setColor(0.15f, 0.65f, 1f, 0.85f);
        batch.draw(whitePixel, r.x, r.y, r.width, r.height);

        batch.setColor(0f, 0f, 0f, 0.25f);
        batch.draw(whitePixel, r.x, r.y, r.width, 6f);
        batch.draw(whitePixel, r.x, r.y + r.height - 6f, r.width, 6f);

        batch.setColor(1f, 1f, 1f, 1f);
        drawCenteredText(text, r.x + r.width / 2f, r.y + r.height / 2f + 22f);
    }
    private void drawTouchpad() {
        if (!touchpadActive) return;
        if (touchpadTex == null) return;

        // Base (círculo grande)
        batch.setColor(1f, 1f, 1f, 0.48f);
        batch.draw(
            touchpadTex,
            padBaseX - padRadius, padBaseY - padRadius,
            padRadius * 2f, padRadius * 2f
        );

        // Knob (círculo pequeño) -> usamos el mismo sprite
        batch.setColor(1f, 1f, 1f, 0.75f);
        batch.draw(
            touchpadTex,
            padKnobX - padKnobRadius, padKnobY - padKnobRadius,
            padKnobRadius * 2f, padKnobRadius * 2f
        );

        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void drawOptionsScreen() {
        batch.setColor(0f, 0f, 0f, 0.65f);
        batch.draw(whitePixel, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        batch.setColor(1f, 1f, 1f, 1f);

        font.getData().setScale(6.0f);
        drawCenteredText("OPCIONES", WORLD_WIDTH / 2f, 1450f);

        font.getData().setScale(2.4f);
        drawCenteredText("Multitouch (mover + disparar)", WORLD_WIDTH / 2f, 1200f);

        drawToggleButton(
            btnToggleMultitouch,
            "MULTITOUCH: " + (multitouchEnabled ? "ON" : "OFF"),
            multitouchEnabled
        );

        font.getData().setScale(1.8f);
        drawCenteredText("Toca el botón para cambiar", WORLD_WIDTH / 2f, 650f);
        drawCenteredText("BACK para volver", WORLD_WIDTH / 2f, 360f);

        font.getData().setScale(1.0f);
    }

    private void drawToggleButton(Rectangle r, String text, boolean on) {
        if (on) batch.setColor(0.20f, 0.85f, 0.35f, 0.85f);
        else    batch.setColor(0.85f, 0.25f, 0.25f, 0.85f);

        batch.draw(whitePixel, r.x, r.y, r.width, r.height);

        batch.setColor(0f, 0f, 0f, 0.25f);
        batch.draw(whitePixel, r.x, r.y, r.width, 6f);
        batch.draw(whitePixel, r.x, r.y + r.height - 6f, r.width, 6f);

        batch.setColor(1f, 1f, 1f, 1f);
        font.getData().setScale(2.6f);
        drawCenteredText(text, r.x + r.width / 2f, r.y + r.height / 2f + 22f);
        font.getData().setScale(1.0f);
    }


    private void drawSimpleScreen(String title, String body) {
        batch.setColor(0f, 0f, 0f, 0.65f);
        batch.draw(whitePixel, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        batch.setColor(1f, 1f, 1f, 1f);

        font.getData().setScale(6.0f);
        drawCenteredText(title, WORLD_WIDTH / 2f, 1450f);

        font.getData().setScale(2.4f);
        drawCenteredText(body, WORLD_WIDTH / 2f, 1150f);

        font.getData().setScale(1.8f);
        drawCenteredText("Toca para volver", WORLD_WIDTH / 2f, 300f);

        font.getData().setScale(1.0f);
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
    private void spawnGravityWell() {
        gravityActive = true;
        gravityTimer = gravityDuration;
        gravityRotation = 0f;

        boolean left = MathUtils.randomBoolean();
        gravityX = left ? gravitySideMargin
            : (WORLD_WIDTH - gravitySideMargin - gravitySpriteSize);

        gravityY = MathUtils.random(700f, WORLD_HEIGHT - 450f);

        // 🔊 SONIDO GRAVITACIONAL
        if (sfxGravedad != null) {
            sfxGravedad.play(2.0f); // volumen del sonido gravitacional (ajustable)
        }
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
        if (fuerzaGravTex != null) fuerzaGravTex.dispose();
        if (sfxGravedad != null) sfxGravedad.dispose();
        if (escudoItemTex != null) escudoItemTex.dispose();
        if (naveEscudoTex != null) naveEscudoTex.dispose();
        if (sfxEscudo != null) sfxEscudo.dispose();
        if (sfxRomperEscudo != null) sfxRomperEscudo.dispose();
        if (sfxCuracion != null) sfxCuracion.dispose();
        if (touchpadTex != null) touchpadTex.dispose();

    }
}
