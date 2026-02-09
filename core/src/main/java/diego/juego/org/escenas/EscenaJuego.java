package diego.juego.org.escenas;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Iterator;

import diego.juego.org.entidades.BalaEnemigo;
import diego.juego.org.entidades.BalaJugador;
import diego.juego.org.input.ControlTouchpad;
import diego.juego.org.entidades.Enemigo;
import diego.juego.org.entidades.EnemigoLento;
import diego.juego.org.entidades.EnemigoRapido;
import diego.juego.org.Escena;
import diego.juego.org.estado.EstadoJuego;
import diego.juego.org.entidades.Explosion;
import diego.juego.org.GestorEscenas;
import diego.juego.org.Main;
import diego.juego.org.Parallax;
import diego.juego.org.recursos.Recursos;

/**
 * Escena principal del juego (gameplay): parallax + lógica + render.
 */
public class EscenaJuego implements Escena {

    private final Recursos recursos;
    private final Viewport viewport;
    private final GestorEscenas gestorEscenas;

    private static final int PUNTOS_NIVEL_1 = 3000;
    private boolean victoriaLanzada = false;




    // ===== PROPULSIÓN =====
    private float empuje = 0f;          // 0 = parado, 1 = empuje máximo
    private float empujeSubida = 2.0f;  // tarda ~0.5s en llegar a 1
    private float empujeBajada = 4.0f;  // vuelve rápido a idle


    // Módulos
    private final Parallax parallax = new Parallax();
    private final ControlTouchpad controlPad = new ControlTouchpad(155f, 60f);

    // UI / TEXTO
    private final BitmapFont fuente;
    private final GlyphLayout layout;

    // JUGADOR
    private float x, y;
    private float velocidad = 500f;
    private float escala = 0.10f;

    private Rectangle limitesJugador;
    // ===== ANTIGRAVITACIONAL (ITEM) =====
    private boolean antiGravedadActiva = false;
    private boolean antiGravedadVisible = false;

    private float timerSpawnAntiGrav = 0f;
    private float delaySpawnAntiGrav = 18f;

    private float duracionAntiGrav = 15f;
    private float timerAntiGrav = 0f;

    private Rectangle limitesAntiGrav;
    private float antiGravX, antiGravY;
    private float velAntiGrav = 160f;

    private int vidaJugador = 3;
    private int puntuacion = 0;

    private float invulnTimer = 0f;
    private static final float INVULN_TIME = 0.45f;

    // DISPARO
    private final Array<BalaJugador> balasJugador = new Array<>();
    private float cooldownDisparo = 0.15f;
    private float timerDisparo = 0f;

    // ENEMIGOS
    private final Array<Enemigo> enemigos = new Array<>();
    private final Array<BalaEnemigo> balasEnemigas = new Array<>();

    private float timerSpawnEnemigos = 0f;
    private float delaySpawnEnemigos = 1.2f;
    private float timerDificultad = 0f;

    private float delaySpawnInicio = 3.2f;
    private float delaySpawnMin = 0.55f;
    private float tiempoRamp = 90f;

    // EXPLOSIONES
    private final Array<Explosion> explosiones = new Array<>();

    // SCORE POP
    private float timerScorePop = 0f;
    private static final float SCORE_POP_DURATION = 0.18f;
    private float escalaScoreBase = 3.2f;

    // GRAVEDAD / POZO
    private float timerSpawnGravedad = 0f;
    private float delaySpawnGravedad = 9f;
    private float duracionGravedad = 4.0f;
    private float timerGravedad = 0f;
    private boolean gravedadActiva = false;

    private float gravedadX = 0f, gravedadY = 0f;
    private float fuerzaGravedad = 300f;
    private float margenLateralGravedad = 60f;
    private float tamSpriteGravedad = 220f;

    // ===== HUD: barra antigravedad =====
    private final float barraAntiW = 520f;
    private final float barraAntiH = 18f;
    private final float barraAntiX = (Main.ANCHO_MUNDO - barraAntiW) / 2f;
    private final float barraAntiY = Main.ALTO_MUNDO - 1850f; // Barra duracion antigravedad debajo del marcador
    private final float barraPadding = 4f;


    private float rotGravedad = 0f;
    private float velRotGravedad = 50f;

    // CALIBRACIÓN ACELERÓMETRO
    private boolean accelCalibrado = false;
    private float accelZeroX = 0f;
    private float accelZeroY = 0f;
    private float accelDeadZone = 0.08f;

    // ESCUDO (ITEM)
    private boolean escudoActivo = false;
    private boolean escudoVisible = false;
    private float timerSpawnEscudo = 0f;
    private float delaySpawnEscudo = 14f;

    private Rectangle limitesEscudo;
    private float escudoX, escudoY;
    private float velEscudo = 180f;

    // Escalas para el tamaño de imagen
    private float multiplicadorEscudo = 1.75f;
    private float multiplicadorAntiGrav = 2.3f; //


    // VIDA (ITEM)
    private boolean vidaItemVisible = false;
    private float timerSpawnVida = 0f;
    private float delaySpawnVida = 16f;

    private Rectangle limitesVidaItem;
    private float vidaItemX, vidaItemY;
    private float velVidaItem = 170f;

    // HUD VIDAS
    private float tamVida = 70f;
    private float paddingVida = 14f;
    private float margenIzqVida = 24f;
    private float margenArribaVida = 24f;

    // SONIDOS
    private float volumenSFX = 0.1f;

    public EscenaJuego(Recursos recursos, Viewport viewport, GestorEscenas gestorEscenas) {
        this.recursos = recursos;
        this.viewport = viewport;
        this.gestorEscenas = gestorEscenas;

        this.fuente = new BitmapFont();
        this.layout = new GlyphLayout();

        inicializar();
        reiniciarPartida();


    }

    private void inicializar() {
        float shipW = recursos.naveJugador.getWidth() * escala;
        float shipH = recursos.naveJugador.getHeight() * escala;
        limitesJugador = new Rectangle(0, 0, shipW, shipH);
    }

    private void reiniciarPartida() {
        vidaJugador = 3;
        timerScorePop = 0f;
        invulnTimer = 0f;

        // Nivel 2 empieza con base de 3000 puntos
        puntuacion = (EstadoJuego.nivelActual >= 2) ? PUNTOS_NIVEL_1 : 0;



        float shipW = recursos.naveJugador.getWidth() * escala;
        float shipH = recursos.naveJugador.getHeight() * escala;

        x = Main.ANCHO_MUNDO / 2f - shipW / 2f;
        y = 200f;
        limitesJugador.set(x, y, shipW, shipH);

        balasJugador.clear();
        enemigos.clear();
        balasEnemigas.clear();
        explosiones.clear();

        timerSpawnEnemigos = 0f;
        timerDisparo = 0f;

        delaySpawnEnemigos = delaySpawnInicio;
        timerDificultad = 0f;

        // gravedad
        timerSpawnGravedad = 0f;
        timerGravedad = 0f;
        gravedadActiva = false;
        gravedadX = 0f;
        gravedadY = 0f;
        rotGravedad = 0f;


        antiGravedadActiva = false;
        antiGravedadVisible = false;
        timerSpawnAntiGrav = 0f;
        timerAntiGrav = 0f;
        limitesAntiGrav = null;


        // escudo
        escudoActivo = false;
        escudoVisible = false;
        timerSpawnEscudo = 0f;
        limitesEscudo = null;

        // vida item
        vidaItemVisible = false;
        timerSpawnVida = 0f;
        limitesVidaItem = null;

        accelCalibrado = false;

        // touchpad
        controlPad.reset();
    }

    @Override
    public void alMostrar() {
        if (EstadoJuego.musicaActivada) {
            if (!recursos.musicaFondo.isPlaying()) {
                recursos.musicaFondo.play();
            }
        } else {
            recursos.musicaFondo.pause();
        }
    }


    @Override
    public void actualizar(float delta) {
        parallax.actualizar(delta);

        // BACK/ESC -> volver al menú
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gestorEscenas.cambiarA(new EscenaMenu(recursos, viewport, gestorEscenas));
            return;
        }

        actualizarJuego(delta);
    }

    private void actualizarJuego(float delta) {
        if (!victoriaLanzada && EstadoJuego.nivelActual == 1 && puntuacion >= PUNTOS_NIVEL_1) {
            victoriaLanzada = true;
            gestorEscenas.cambiarA(
                new EscenaVictoria(recursos, viewport, gestorEscenas, puntuacion)
            );
            return;
        }

        if (vidaJugador <= 0) return;

        float dx = 0f, dy = 0f;

        // ===== CONTROL =====
        if (EstadoJuego.multitouchActivado) {
            controlPad.actualizar(viewport);

            dx = controlPad.getDx();
            dy = controlPad.getDy();

            x += dx * 650f * delta;
            y += dy * 650f * delta;

        } else {
            // acelerómetro / teclado
            if (!accelCalibrado && Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
                accelZeroX = Gdx.input.getAccelerometerX();
                accelZeroY = Gdx.input.getAccelerometerY();
                accelCalibrado = true;
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

            x += dx * velocidad * delta;
            y += dy * velocidad * delta;
        }
        // ===== CONTROL TECLADO (Desktop / pruebas) =====
// Esto permite mover SIEMPRE con flechas (y WASD) en el lwjgl3Launcher.
        float kx = 0f, ky = 0f;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))  kx -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) kx += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W))    ky += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))  ky -= 1f;

// Si hay teclado pulsado, prioriza teclado (anula el dx/dy del touchpad/acelerómetro)
        if (kx != 0f || ky != 0f) {
            float len = (float)Math.sqrt(kx*kx + ky*ky);
            if (len != 0f) { kx /= len; ky /= len; } // normaliza para diagonales
            dx = kx;
            dy = ky;

            x += dx * velocidad * delta;
            y += dy * velocidad * delta;
        }
        // Solo empuja cuando vas hacia ARRIBA (dy > 0)
        boolean empujandoArriba = dy > 0.05f; // ajusta el 0.05 si quieres más/menos sensibilidad

        if (empujandoArriba) {
            empuje = Math.min(1f, empuje + empujeSubida * delta);
        } else {
            empuje = Math.max(0f, empuje - empujeBajada * delta);
        }



        // ===== gravedad: arrastre lateral =====
        if (gravedadActiva && !antiGravedadActiva){
            float shipW = recursos.naveJugador.getWidth() * escala;
            float centroPozoX = gravedadX + tamSpriteGravedad / 2f;
            float centroNaveX = x + shipW / 2f;

            float dir = Math.signum(centroPozoX - centroNaveX);
            float dist = Math.abs(centroPozoX - centroNaveX);
            float normalizado = MathUtils.clamp(1f - (dist / 700f), 0.2f, 1f);

            x += dir * fuerzaGravedad * normalizado * delta;
        }

        // ===== límites mundo =====
        float shipW = recursos.naveJugador.getWidth() * escala;
        float shipH = recursos.naveJugador.getHeight() * escala;

        x = Math.max(0, Math.min(x, Main.ANCHO_MUNDO - shipW));
        y = Math.max(0, Math.min(y, Main.ALTO_MUNDO - shipH));
        limitesJugador.set(x, y, shipW, shipH);

        if (invulnTimer > 0f) invulnTimer -= delta;

        // ===== disparo =====
        timerDisparo += delta;
        boolean disparoPulsado = Gdx.input.isKeyPressed(Input.Keys.SPACE);

        if (EstadoJuego.multitouchActivado) {
            // dedo movimiento no dispara, los otros disparan en mitad derecha
            for (int p = 0; p < 5; p++) {
                if (p == controlPad.getDedoMovimiento()) continue;
                if (!Gdx.input.isTouched(p)) continue;

                Vector3 v = new Vector3(Gdx.input.getX(p), Gdx.input.getY(p), 0);
                viewport.unproject(v);

                if (v.x >= Main.ANCHO_MUNDO * 0.5f) {
                    disparoPulsado = true;
                    break;
                }
            }
        } else {
            disparoPulsado = disparoPulsado || Gdx.input.isTouched();
        }

        if (disparoPulsado && timerDisparo >= cooldownDisparo) {
            timerDisparo = 0f;

            float bW = recursos.laserVerde.getWidth() * escala;
            float bH = recursos.laserVerde.getHeight() * escala;

            float leftX  = x + (shipW * 0.15f) - (bW / 2f);
            float rightX = x + (shipW * 0.85f) - (bW / 2f);
            float gunY = y + (shipH * 0.7f);

            balasJugador.add(new BalaJugador(leftX, gunY, bW, bH));
            balasJugador.add(new BalaJugador(rightX, gunY, bW, bH));

            reproducir(recursos.sfxDisparo, volumenSFX);
        }

        for (Iterator<BalaJugador> it = balasJugador.iterator(); it.hasNext(); ) {
            BalaJugador b = it.next();
            b.actualizar(delta);
            if (b.y > Main.ALTO_MUNDO) it.remove();
        }

        // ===== dificultad (spawn delay dinámico) =====
        timerDificultad += delta;
        float t = MathUtils.clamp(timerDificultad / tiempoRamp, 0f, 1f);
        delaySpawnEnemigos = MathUtils.lerp(delaySpawnInicio, delaySpawnMin, t);

        // ===== gravedad spawn =====
        timerSpawnGravedad += delta;
        if (!gravedadActiva && timerSpawnGravedad >= delaySpawnGravedad) {
            timerSpawnGravedad = 0f;
            if (MathUtils.random() < 0.6f) spawnPozoGravitatorio();
        }

        if (gravedadActiva) {
            timerGravedad -= delta;
            rotGravedad = (rotGravedad + velRotGravedad * delta) % 360f;
            if (timerGravedad <= 0f) gravedadActiva = false;
        }

        // ===== escudo spawn + caída =====
        timerSpawnEscudo += delta;
        if (!escudoVisible && timerSpawnEscudo >= delaySpawnEscudo && !escudoActivo) {
            timerSpawnEscudo = 0f;
            if (MathUtils.random() < 0.6f) spawnItemEscudo();
        }

        if (escudoVisible && limitesEscudo != null) {
            escudoY -= velEscudo * delta;
            limitesEscudo.setPosition(escudoX, escudoY);
            if (escudoY + limitesEscudo.height < 0) escudoVisible = false;
        }

        if (escudoVisible && limitesEscudo != null && limitesEscudo.overlaps(limitesJugador)) {
            escudoVisible = false;
            escudoActivo = true;
            reproducir(recursos.sfxEscudo, 0.6f);
        }

        // ===== vida item spawn + caída =====
        timerSpawnVida += delta;
        if (!vidaItemVisible && timerSpawnVida >= delaySpawnVida && vidaJugador < 3) {
            timerSpawnVida = 0f;
            if (MathUtils.random() < 0.55f) spawnItemVida();
        }

        if (vidaItemVisible && limitesVidaItem != null) {
            vidaItemY -= velVidaItem * delta;
            limitesVidaItem.setPosition(vidaItemX, vidaItemY);
            if (vidaItemY + limitesVidaItem.height < 0) vidaItemVisible = false;
        }

        if (vidaItemVisible && limitesVidaItem != null && limitesVidaItem.overlaps(limitesJugador)) {
            vidaItemVisible = false;
            vidaJugador = Math.min(3, vidaJugador + 1);
            reproducir(recursos.sfxCuracion, 0.7f);
        }

        // ===== antigravedad spawn =====
        timerSpawnAntiGrav += delta;
        if (!antiGravedadVisible && !antiGravedadActiva && timerSpawnAntiGrav >= delaySpawnAntiGrav) {
            timerSpawnAntiGrav = 0f;
            if (MathUtils.random() < 0.5f) {
                spawnItemAntiGravedad();
            }
        }
        // antigravedad caida
        if (antiGravedadVisible && limitesAntiGrav != null) {
            antiGravY -= velAntiGrav * delta;
            limitesAntiGrav.setPosition(antiGravX, antiGravY);

            if (antiGravY + limitesAntiGrav.height < 0) {
                antiGravedadVisible = false;
            }
        }
        // antigravedad caída
        if (antiGravedadVisible && limitesAntiGrav != null &&
            limitesAntiGrav.overlaps(limitesJugador)) {

            antiGravedadVisible = false;
            antiGravedadActiva = true;
            timerAntiGrav = duracionAntiGrav;



            timerSpawnGravedad = 0f; // <- para que no salga una gravedad inmediata al terminar


            reproducir(recursos.sfxAntiGravedad, 0.8f);
        }

        if (antiGravedadActiva) {
            timerAntiGrav -= delta;
            if (timerAntiGrav <= 0f) {
                antiGravedadActiva = false;
            }
        }





        if (timerScorePop > 0f) timerScorePop -= delta;

        // ===== spawn enemigos =====
        timerSpawnEnemigos += delta;
        if (timerSpawnEnemigos >= delaySpawnEnemigos) {
            spawnEnemigo();
            timerSpawnEnemigos = 0f;
        }

        // ===== actualizar enemigos + disparo enemigo =====
        for (Iterator<Enemigo> itE = enemigos.iterator(); itE.hasNext(); ) {
            Enemigo e = itE.next();
            e.actualizar(delta);

            if (e.puedeDisparar()) {
                e.resetDisparo();

                float bW = recursos.laserRojo.getWidth() * escala;
                float bH = recursos.laserRojo.getHeight() * escala;

                float bx = e.x + (e.w / 2f) - (bW / 2f);
                float by = e.y - (bH * 0.2f);

                balasEnemigas.add(new BalaEnemigo(bx, by, bW, bH));
            }

            if (e.fueraPantalla()) itE.remove();
        }

        for (Iterator<BalaEnemigo> itB = balasEnemigas.iterator(); itB.hasNext(); ) {
            BalaEnemigo b = itB.next();
            b.actualizar(delta);
            if (b.fueraPantalla()) itB.remove();
        }

        // ===== colisiones bala jugador -> enemigo =====
        for (Iterator<BalaJugador> itBJ = balasJugador.iterator(); itBJ.hasNext(); ) {
            BalaJugador b = itBJ.next();
            boolean impacto = false;

            for (Iterator<Enemigo> itE = enemigos.iterator(); itE.hasNext(); ) {
                Enemigo e = itE.next();

                if (b.limites.overlaps(e.limites)) {
                    impacto = true;

                    e.recibirImpacto(); // cada bala quita 1 vida

                    if (e.estaMuerto()) {
                        agregarExplosionCentrada(e.x + e.w / 2f, e.y + e.h / 2f, e.w * 1.25f);
                        reproducirExplosionAleatoria();

                        puntuacion += 100;
                        activarScorePop();

                        itE.remove(); // solo si está muerto
                    }

                    break; // esta bala solo puede afectar a un enemigo
                }
            }

            if (impacto) itBJ.remove(); // la bala desaparece siempre que impacta
        }

        // ===== colisión bala enemigo -> jugador =====
        if (invulnTimer <= 0f) {
            for (Iterator<BalaEnemigo> it = balasEnemigas.iterator(); it.hasNext(); ) {
                BalaEnemigo b = it.next();

                if (b.limites.overlaps(limitesJugador)) {
                    it.remove();

                    if (escudoActivo) {
                        escudoActivo = false;
                        invulnTimer = 0.15f;
                        reproducir(recursos.sfxRomperEscudo, 0.9f);
                    } else {
                        vidaJugador--;
                        invulnTimer = INVULN_TIME;

                        vibrarGolpe();

                        agregarExplosionCentrada(x + shipW / 2f, y + shipH / 2f, shipW * 1.1f);
                        reproducir(recursos.sfxExplosion, 0.3f);

                        if (vidaJugador <= 0) {
                            vidaJugador = 0;

                            vibrarMuerte();
                            agregarExplosionCentrada(x + shipW / 2f, y + shipH / 2f, shipW * 1.9f);
                            reproducir(recursos.sfxExplosion, 1.0f);

                            gestorEscenas.cambiarA(new EscenaGameOver(recursos, viewport, gestorEscenas, puntuacion));
                        }
                    }

                    break;
                }
            }
        }

        // ===== actualizar explosiones =====
        for (Iterator<Explosion> it = explosiones.iterator(); it.hasNext(); ) {
            Explosion ex = it.next();
            ex.actualizar(delta);
            if (ex.terminada()) it.remove();
        }
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        if (EstadoJuego.nivelActual == 2) {
            parallax.dibujar(batch,
                recursos.fondoMuyLejano2,
                recursos.fondoLejano2,
                recursos.fondoCercano2
            );
        } else {
            parallax.dibujar(batch,
                recursos.fondoMuyLejano,
                recursos.fondoLejano,
                recursos.fondoCercano
            );
        }


        // item escudo
        if (escudoVisible && limitesEscudo != null) {
            batch.draw(recursos.itemEscudo, escudoX, escudoY, limitesEscudo.width, limitesEscudo.height);
        }

        // item vida
        if (vidaItemVisible && limitesVidaItem != null) {
            batch.draw(recursos.iconoVida, vidaItemX, vidaItemY, limitesVidaItem.width, limitesVidaItem.height);
        }
        // item antigravitacional
        if (antiGravedadVisible && limitesAntiGrav != null) {
            batch.draw(
                recursos.itemAntigravitacional,
                antiGravX,
                antiGravY,
                limitesAntiGrav.width,
                limitesAntiGrav.height
            );
        }


        // gravedad (rotando)
        if (gravedadActiva && recursos.fuerzaGravitacional != null) {
            batch.draw(
                recursos.fuerzaGravitacional,
                gravedadX, gravedadY,
                tamSpriteGravedad / 2f, tamSpriteGravedad / 2f,
                tamSpriteGravedad, tamSpriteGravedad,
                1f, 1f,
                rotGravedad,
                0, 0,
                recursos.fuerzaGravitacional.getWidth(), recursos.fuerzaGravitacional.getHeight(),
                false, false
            );
        }

        // enemigos + balas
        for (Enemigo e : enemigos) batch.draw(e.getTextura(recursos), e.x, e.y, e.w, e.h);
        for (BalaEnemigo b : balasEnemigas) batch.draw(recursos.laserRojo, b.x, b.y, b.w, b.h);
        for (BalaJugador b : balasJugador) batch.draw(recursos.laserVerde, b.x, b.y, b.w, b.h);


        // jugador
        float baseW = recursos.naveJugador.getWidth() * escala;
        float baseH = recursos.naveJugador.getHeight() * escala;

        if (vidaJugador > 0) {

            // 1) Escudo tiene prioridad visual
            if (escudoActivo && recursos.naveConEscudo != null) {

                float w = baseW * multiplicadorEscudo;
                float h = baseH * multiplicadorEscudo;

                float drawX = x - (w - baseW) / 2f;
                float drawY = y - (h - baseH) / 2f;

                dibujarPropulsionDoble(batch, drawX, drawY, w, h, baseW, baseH);
                batch.draw(recursos.naveConEscudo, drawX, drawY, w, h);


                // 2) Antigravedad
            } else if (antiGravedadActiva && recursos.naveAntigravitacional != null) {

                float w = baseW * multiplicadorAntiGrav;
                float h = baseH * multiplicadorAntiGrav;

                float drawX = x - (w - baseW) / 2f;
                float drawY = y - (h - baseH) / 2f;

                dibujarPropulsionDoble(batch, drawX, drawY, w, h, baseW, baseH);
                batch.draw(recursos.naveAntigravitacional, drawX, drawY, w, h);


                // 3) Normal
            } else {
                dibujarPropulsionDoble(batch, x, y, baseW, baseH, baseW, baseH);
                batch.draw(recursos.naveJugador, x, y, baseW, baseH);

            }
        }

        batch.setColor(1f, 1f, 1f, 1f);

        for (Explosion ex : explosiones) {
            float a = ex.alpha();
            float s = ex.escalaPop();

            batch.setColor(1f, 1f, 1f, a);

            float drawW = ex.size * s;
            float drawH = ex.size * s;

            float cx = ex.x + ex.size / 2f;
            float cy = ex.y + ex.size / 2f;

            float drawX = cx - drawW / 2f;
            float drawY = cy - drawH / 2f;

            batch.draw(recursos.explosion, drawX, drawY, drawW, drawH);
        }

        batch.setColor(1f, 1f, 1f, 1f);

        // HUD
        dibujarVidas(batch);
        dibujarPuntuacion(batch);
        dibujarBarraAntiGravedad(batch);








        // touchpad
        if (EstadoJuego.multitouchActivado) dibujarTouchpad(batch);
    }

    private void dibujarTouchpad(SpriteBatch batch) {
        if (!controlPad.estaActivo()) return;
        if (recursos.touchpad == null) return;

        batch.setColor(1f, 1f, 1f, 0.48f);
        batch.draw(
            recursos.touchpad,
            controlPad.getBaseX() - controlPad.getRadioPad(),
            controlPad.getBaseY() - controlPad.getRadioPad(),
            controlPad.getRadioPad() * 2f,
            controlPad.getRadioPad() * 2f
        );

        batch.setColor(1f, 1f, 1f, 0.75f);
        batch.draw(
            recursos.touchpad,
            controlPad.getKnobX() - controlPad.getRadioKnob(),
            controlPad.getKnobY() - controlPad.getRadioKnob(),
            controlPad.getRadioKnob() * 2f,
            controlPad.getRadioKnob() * 2f
        );

        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void dibujarVidas(SpriteBatch batch) {
        int max = 3;
        for (int i = 0; i < max; i++) {
            float drawX = margenIzqVida + i * (tamVida + paddingVida);
            float drawY = Main.ALTO_MUNDO - margenArribaVida - tamVida;

            if (i < vidaJugador) batch.setColor(1f, 1f, 1f, 1f);
            else batch.setColor(1f, 1f, 1f, 0.25f);

            batch.draw(recursos.iconoVida, drawX, drawY, tamVida, tamVida);
        }
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void dibujarPuntuacion(SpriteBatch batch) {
        float extra = 0f;
        if (timerScorePop > 0f) {
            float t = timerScorePop / SCORE_POP_DURATION;
            extra = (float) Math.sin(t * Math.PI) * 0.9f;
        }
        fuente.getData().setScale(escalaScoreBase + extra);

        String text = String.valueOf(puntuacion);
        layout.setText(fuente, text);

        float padding = 20f;
        float iconSize = 64f;

        float totalW = iconSize + padding + layout.width;

        float x = Main.ANCHO_MUNDO - totalW - 30f;
        float y = Main.ALTO_MUNDO - 40f;

        batch.draw(recursos.iconoPuntuacion, x, y - iconSize + 10f, iconSize, iconSize);
        fuente.draw(batch, layout, x + iconSize + padding, y);

        fuente.getData().setScale(1.0f);
    }
    private void dibujarBarraAntiGravedad(SpriteBatch batch) {
        if (!antiGravedadActiva) return;
        if (duracionAntiGrav <= 0f) return;

        float progreso = MathUtils.clamp(timerAntiGrav / duracionAntiGrav, 0f, 1f);

        // Fondo (oscuro)
        batch.setColor(0f, 0f, 0f, 0.55f);
        batch.draw(recursos.pixelBlanco, barraAntiX, barraAntiY, barraAntiW, barraAntiH);

        // Marco
        batch.setColor(1f, 1f, 1f, 0.85f);
        batch.draw(recursos.pixelBlanco, barraAntiX, barraAntiY, barraAntiW, 2f);
        batch.draw(recursos.pixelBlanco, barraAntiX, barraAntiY + barraAntiH - 2f, barraAntiW, 2f);
        batch.draw(recursos.pixelBlanco, barraAntiX, barraAntiY, 2f, barraAntiH);
        batch.draw(recursos.pixelBlanco, barraAntiX + barraAntiW - 2f, barraAntiY, 2f, barraAntiH);

        // Relleno (morado)
        float innerX = barraAntiX + barraPadding;
        float innerY = barraAntiY + barraPadding;
        float innerW = (barraAntiW - barraPadding * 2f) * progreso;
        float innerH = barraAntiH - barraPadding * 2f;

        batch.setColor(0.70f, 0.25f, 1f, 0.90f);
        batch.draw(recursos.pixelBlanco, innerX, innerY, innerW, innerH);

        // Parpadeo cuando queda poco
        if (timerAntiGrav <= 1.2f) {
            float blink = (float) (0.35f + 0.35f * Math.sin(timerAntiGrav * 18f));
            batch.setColor(1f, 1f, 1f, blink);
            batch.draw(recursos.pixelBlanco, barraAntiX, barraAntiY, barraAntiW, barraAntiH);
        }

        batch.setColor(1f, 1f, 1f, 1f);
    }
    private void dibujarPropulsionDoble(
        SpriteBatch batch,
        float naveX, float naveY,      // posición real donde se dibuja la nave
        float naveW, float naveH,      // tamaño real donde se dibuja la nave (para colocar)
        float baseW, float baseH       // tamaño base (para que la llama NO crezca con escudo/anti)
    ) {
        Texture tex = (empuje <= 0.02f) ? recursos.propulsionEstatica : recursos.propulsionRoja;
        if (tex == null) return;

        // === TAMAÑO (SIEMPRE BASE) ===
        float w = baseW * 1.5f;
        float hBase = baseH * 4.0f;

        float h = hBase;
        if (empuje > 0.02f) {
            float escalaY = 1.0f + empuje * 2.0f;
            h = hBase * escalaY;
        }

        // === POSICIÓN (USANDO NAVE REAL) ===
        float drawX = naveX + (naveW - w) / 2f;
        float drawY = naveY - h * 0.50f; // ajusta si hace falta

        batch.draw(tex, drawX, drawY, w, h);
    }




    // ====================== SPAWNS / UTIL ======================

    private void spawnEnemigo() {
        if (EstadoJuego.nivelActual == 1) {
            // Nivel 1: SOLO enemigo normal
            spawnEnemigoBasico();
            return;
        }

        if (EstadoJuego.nivelActual == 2) {
            // Nivel 2: mezcla (ajusta porcentajes a tu gusto)
            float r = MathUtils.random();
            if (r < 0.60f) {
                spawnEnemigoBasico();   // 60% normal
            } else if (r < 0.85f) {
                spawnEnemigoRapido();   // 25% rápido
            } else {
                spawnEnemigoLento();    // 15% lento
            }
            return;
        }

        // Si en el futuro hay más niveles
        spawnEnemigoBasico();
    }

    private void spawnEnemigoBasico() {
        float eW = recursos.enemigoNormal.getWidth() * escala;
        float eH = recursos.enemigoNormal.getHeight() * escala;

        float spawnX = MathUtils.random(0f, Main.ANCHO_MUNDO - eW);
        float spawnY = Main.ALTO_MUNDO + eH;

        float delayDisparo = MathUtils.random(0.9f, 1.7f);
        enemigos.add(new Enemigo(spawnX, spawnY, eW, eH, delayDisparo));
    }
    private void spawnEnemigoRapido() {
        float eW = recursos.enemigoRapido.getWidth() * escala;
        float eH = recursos.enemigoRapido.getHeight() * escala;

        float spawnX = MathUtils.random(0f, Main.ANCHO_MUNDO - eW);
        float spawnY = Main.ALTO_MUNDO + eH;

        float delayDisparo = MathUtils.random(1.2f, 2.0f);
        enemigos.add(new EnemigoRapido(spawnX, spawnY, eW, eH, delayDisparo));
    }
    private void spawnEnemigoLento() {
        float eW = recursos.enemigoLento.getWidth() * escala;
        float eH = recursos.enemigoLento.getHeight() * escala;

        float spawnX = MathUtils.random(0f, Main.ANCHO_MUNDO - eW);
        float spawnY = Main.ALTO_MUNDO + eH;

        float delayDisparo = MathUtils.random(0.7f, 1.3f);

        Enemigo e = new EnemigoLento(spawnX, spawnY, eW, eH, delayDisparo);
        enemigos.add(e);

        Gdx.app.log("SPAWN", e.getClass().getName() + " vida=" + e.getVida());
    }




    private void spawnEnemigoNuevo1() {
        // Por ahora, para que compile: spawnea un básico (luego lo cambias por el nuevo enemigo real)
        spawnEnemigoBasico();
    }


    private void spawnPozoGravitatorio() {
        gravedadActiva = true;
        timerGravedad = duracionGravedad;
        rotGravedad = 0f;

        boolean izquierda = MathUtils.randomBoolean();
        gravedadX = izquierda ? margenLateralGravedad : (Main.ANCHO_MUNDO - margenLateralGravedad - tamSpriteGravedad);
        gravedadY = MathUtils.random(700f, Main.ALTO_MUNDO - 450f);

        reproducir(recursos.sfxGravedad, 2.0f);
    }

    private void spawnItemEscudo() {
        escudoVisible = true;

        float size = 90f;
        escudoX = MathUtils.random(40f, Main.ANCHO_MUNDO - size - 40f);
        escudoY = Main.ALTO_MUNDO + size;

        limitesEscudo = new Rectangle(escudoX, escudoY, size, size);
    }

    private void spawnItemVida() {
        vidaItemVisible = true;

        float size = 90f;
        vidaItemX = MathUtils.random(40f, Main.ANCHO_MUNDO - size - 40f);
        vidaItemY = Main.ALTO_MUNDO + size;

        limitesVidaItem = new Rectangle(vidaItemX, vidaItemY, size, size);
    }
    private void spawnItemAntiGravedad() {
        antiGravedadVisible = true;

        float size = 90f;
        antiGravX = MathUtils.random(40f, Main.ANCHO_MUNDO - size - 40f);
        antiGravY = Main.ALTO_MUNDO + size;

        limitesAntiGrav = new Rectangle(antiGravX, antiGravY, size, size);
    }


    private void activarScorePop() {
        timerScorePop = SCORE_POP_DURATION;
    }

    private void agregarExplosionCentrada(float cx, float cy, float size) {
        float exX = cx - size / 2f;
        float exY = cy - size / 2f;
        explosiones.add(new Explosion(exX, exY, size));
    }

    private void reproducirExplosionAleatoria() {
        if (!EstadoJuego.sfxActivados) return;
        if (recursos.sfxExplosion == null) return;

        float vol = 0.75f + MathUtils.random(0.15f);
        float pitch = 0.90f + MathUtils.random(0.20f);
        recursos.sfxExplosion.play(vol, pitch, 0f);
    }

    private void reproducir(Sound s, float vol) {
        if (!EstadoJuego.sfxActivados) return;
        if (s != null) s.play(vol);
    }


    private void vibrarGolpe() {
        if (!EstadoJuego.vibracionActivada) return;

        try {
            if (Main.services != null) Main.services.vibrar(120);
        } catch (Throwable ignored) {}
    }


    private void vibrarMuerte() {
        if (!EstadoJuego.vibracionActivada) return;

        try {
            if (Main.services != null) {
                Main.services.vibrar(450);
                Main.services.vibrarPatron(new long[]{0, 80, 60, 160, 60, 240});
            }
        } catch (Throwable ignored) {}
    }


    @Override public void alRedimensionar(int ancho, int alto) {

    }
    @Override public void alOcultar() {

    }

    @Override
    public void liberar() {
        fuente.dispose();

    }
}
