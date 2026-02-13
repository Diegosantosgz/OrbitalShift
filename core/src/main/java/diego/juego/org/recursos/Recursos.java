package diego.juego.org.recursos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import diego.juego.org.estado.EstadoJuego;

/**
 * Carga y mantiene todos los recursos (texturas/sonidos) compartidos.
 */
public class Recursos {

    // Fondo parallax
    public Texture fondoMuyLejano;
    public Texture fondoLejano;
    public Texture fondoCercano;

    // Fondo parallax nivel 2

    public Texture fondoMuyLejano2;
    public Texture  fondoLejano2;
    public Texture  fondoCercano2;


    // Sprites
    public Texture naveJugador;
    public Texture enemigoNormal;
    public Texture enemigoRapido;
    public Texture enemigoLento;

    public Texture laserVerde;
    public Texture laserRojo;

    public Texture explosion;

    // Items
    public Texture itemEscudo;
    public Texture naveConEscudo;
    public Texture iconoVida;

    public Texture itemAntigravitacional;

    // Propulsion
    public Texture propulsionEstatica;
    public Texture propulsionRoja;

    // Gravedad
    public Texture fuerzaGravitacional;


    public Texture naveAntigravitacional;


    public Texture iconoPuntuacion;

    // UI
    public Texture touchpad;
    public Texture pixelBlanco;

    // Audio
    public Music musicaFondo;
    public Music musicaFondoN2;
    public Sound sfxDisparo;
    public Sound sfxExplosion;
    public Sound sfxGravedad;
    public Sound sfxEscudo;
    public Sound sfxRomperEscudo;
    public Sound sfxCuracion;
    public Sound sfxAntiGravedad;

    public Textos textos;




    public void cargar() {
        // Fondos
        fondoMuyLejano = new Texture("imagenes/fondos/fondo_muy_lejano.png");
        fondoLejano    = new Texture("imagenes/fondos/fondo_lejano_nuevo.png");
        fondoCercano   = new Texture("imagenes/fondos/fondo_cercano_nuevo.png");

        // Fondos nivel 2
        fondoMuyLejano2 = new Texture("imagenes/fondos_n2/fondo_muy_lejano_n2.png");
        fondoLejano2    = new Texture("imagenes/fondos_n2/fondo_lejano_n2.png");
        fondoCercano2   = new Texture("imagenes/fondos_n2/fondo_cercano_n2.png");


        // Nave
        naveJugador = new Texture("imagenes/nave/x-wingHDCenital.png");
        naveConEscudo = new Texture("imagenes/nave/nave_escudo.png");
        naveAntigravitacional = new Texture("imagenes/nave/nave_antigravitacional.png");

        // Enemigos
        enemigoNormal = new Texture("imagenes/enemigos/enemigo_normal.png");
        enemigoRapido = new Texture("imagenes/enemigos/enemigo_rapido.png");
        enemigoLento  = new Texture("imagenes/enemigos/enemigo_lento.png");



        // Disparos
        laserVerde = new Texture("imagenes/disparos/laser_verde.png");
        laserRojo  = new Texture("imagenes/disparos/laser_rojo.png");

        // Efectos
        explosion = new Texture("imagenes/efectos/explosion.png");
        fuerzaGravitacional = new Texture("imagenes/efectos/fuerza_gravitacional.png");




        // Propulsión
        propulsionEstatica = new Texture("imagenes/propulsion/propulsion_estatica.png");
        propulsionRoja     = new Texture("imagenes/propulsion/propulsion_larga.png");


        // Items
        itemEscudo = new Texture("imagenes/items/escudo.png");
        itemAntigravitacional = new Texture("imagenes/items/esfera_antigravitacional.png");


        // Interfaz (también item vida)
        touchpad = new Texture("imagenes/interfaz/touchpad.png");
        iconoVida = new Texture("imagenes/interfaz/vida.png");
        iconoPuntuacion = new Texture("imagenes/interfaz/estrella_record.png");



        // Música
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("audio/musica/musica_fondo.mp3"));
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(0.3f);
        musicaFondoN2 = Gdx.audio.newMusic(Gdx.files.internal("audio/musica/musica_fondo_n2.mp3"));
        musicaFondoN2.setLooping(true);
        musicaFondoN2.setVolume(0.3f);

        // SFX
        sfxDisparo = Gdx.audio.newSound(Gdx.files.internal("audio/efectos_sonido/sonido_disparo.ogg"));
        sfxExplosion = Gdx.audio.newSound(Gdx.files.internal("audio/efectos_sonido/explosion.ogg"));
        sfxGravedad = Gdx.audio.newSound(Gdx.files.internal("audio/efectos_sonido/sonido_gravitacional.ogg"));
        sfxEscudo = Gdx.audio.newSound(Gdx.files.internal("audio/efectos_sonido/sonido_recoger_escudo.ogg"));
        sfxRomperEscudo = Gdx.audio.newSound(Gdx.files.internal("audio/efectos_sonido/sonido_romperse_escudo.ogg"));
        sfxCuracion = Gdx.audio.newSound(Gdx.files.internal("audio/efectos_sonido/sonido_curacion.ogg"));
        sfxAntiGravedad = Gdx.audio.newSound(Gdx.files.internal("audio/efectos_sonido/sonido_esfera_antigravitacional.ogg"));



        pixelBlanco = crearPixelBlanco();
        textos = new Textos();
    }

    private Texture crearPixelBlanco() {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(1, 1, 1, 1);
        pm.fill();
        Texture t = new Texture(pm);
        pm.dispose();
        return t;
    }
    public void reproducirMusicaParaNivel(int nivel) {
        // Si la música está desactivada: parar todo
        if (!EstadoJuego.musicaActivada) {
            if (musicaFondo != null) musicaFondo.pause();
            if (musicaFondoN2 != null) musicaFondoN2.pause();
            return;
        }

        // Parar ambas antes (evita solapes)
        if (musicaFondo != null) musicaFondo.stop();
        if (musicaFondoN2 != null) musicaFondoN2.stop();

        // Reproducir la correcta
        if (nivel >= 2) {
            if (musicaFondoN2 != null) musicaFondoN2.play();
        } else {
            if (musicaFondo != null) musicaFondo.play();
        }
    }



    public void liberar() {
        // Texturas
        liberarTex(fondoMuyLejano);
        liberarTex(fondoLejano);
        liberarTex(fondoCercano);

        liberarTex(fondoMuyLejano2);
        liberarTex(fondoLejano2);
        liberarTex(fondoCercano2);


        liberarTex(naveJugador);
        liberarTex(enemigoNormal);
        liberarTex(enemigoRapido);
        liberarTex(enemigoLento);




        liberarTex(laserVerde);
        liberarTex(laserRojo);

        liberarTex(explosion);

        liberarTex(iconoVida);
        liberarTex(itemEscudo);
        liberarTex(naveConEscudo);

        liberarTex(fuerzaGravitacional);
        liberarTex(iconoPuntuacion);

        liberarTex(touchpad);
        liberarTex(pixelBlanco);
        liberarTex(naveAntigravitacional);
        liberarTex(propulsionEstatica);
        liberarTex(propulsionRoja);



        // Audio
        if (musicaFondo != null) musicaFondo.dispose();
        if (musicaFondoN2 != null) musicaFondoN2.dispose();


        liberarSfx(sfxDisparo);
        liberarSfx(sfxExplosion);
        liberarSfx(sfxGravedad);
        liberarTex(itemAntigravitacional);
        liberarSfx(sfxEscudo);
        liberarSfx(sfxRomperEscudo);
        liberarSfx(sfxCuracion);
        liberarSfx(sfxAntiGravedad);

        textos = null;


    }

    private void liberarTex(Texture t) {
        if (t != null) t.dispose();
    }

    private void liberarSfx(Sound s) {
        if (s != null) s.dispose();
    }
}
