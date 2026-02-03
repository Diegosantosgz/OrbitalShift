package diego.juego.org;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

/**
 * Carga y mantiene todos los recursos (texturas/sonidos) compartidos.
 */
public class Recursos {

    // Fondo parallax
    public Texture fondoMuyLejano;
    public Texture fondoLejano;
    public Texture fondoCercano;

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
    public Sound sfxDisparo;
    public Sound sfxExplosion;
    public Sound sfxGravedad;
    public Sound sfxEscudo;
    public Sound sfxRomperEscudo;
    public Sound sfxCuracion;
    public Sound sfxAntiGravedad;


    public void cargar() {
        // Texturas
        fondoMuyLejano = new Texture("fondo_muy_lejano.png");
        fondoLejano    = new Texture("fondo_lejano_nuevo.png");
        fondoCercano   = new Texture("fondo_cercano_nuevo.png");

        naveJugador = new Texture("x-wingHDCenital.png");
        propulsionEstatica = new Texture("propulsion_estatica.png");
        propulsionRoja = new Texture("propulsion_larga.png");

        enemigoNormal = new Texture("enemigo_normal.png");
        enemigoRapido = new Texture("enemigo_rapido.png");
        enemigoLento  = new Texture("enemigo_lento.png");



        laserVerde = new Texture("laser_verde.png");
        laserRojo  = new Texture("laser_rojo.png");

        explosion = new Texture("explosion.png");

        iconoVida = new Texture("vida.png");
        itemEscudo = new Texture("escudo.png");
        naveConEscudo = new Texture("nave_escudo.png");

        fuerzaGravitacional = new Texture("fuerza_gravitacional.png");
        itemAntigravitacional = new Texture("esfera_antigravitacional.png");
        naveAntigravitacional = new Texture("nave_antigravitacional.png");




        iconoPuntuacion = new Texture("estrella_record.png");

        touchpad = new Texture("touchpad.png");

        pixelBlanco = crearPixelBlanco();

        // Música
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("musica_fondo.mp3"));
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(0.3f);

        // SFX
        sfxDisparo = Gdx.audio.newSound(Gdx.files.internal("sonido_disparo.ogg"));
        sfxExplosion = Gdx.audio.newSound(Gdx.files.internal("explosion.ogg"));
        sfxGravedad = Gdx.audio.newSound(Gdx.files.internal("sonido_gravitacional.ogg"));
        sfxEscudo = Gdx.audio.newSound(Gdx.files.internal("sonido_recoger_escudo.ogg"));
        sfxRomperEscudo = Gdx.audio.newSound(Gdx.files.internal("sonido_romperse_escudo.ogg"));
        sfxCuracion = Gdx.audio.newSound(Gdx.files.internal("sonido_curacion.ogg"));
        sfxAntiGravedad = Gdx.audio.newSound(Gdx.files.internal("sonido_esfera_antigravitacional.ogg"));


    }

    private Texture crearPixelBlanco() {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(1, 1, 1, 1);
        pm.fill();
        Texture t = new Texture(pm);
        pm.dispose();
        return t;
    }

    public void liberar() {
        // Texturas
        liberarTex(fondoMuyLejano);
        liberarTex(fondoLejano);
        liberarTex(fondoCercano);

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

        liberarSfx(sfxDisparo);
        liberarSfx(sfxExplosion);
        liberarSfx(sfxGravedad);
        liberarTex(itemAntigravitacional);
        liberarSfx(sfxEscudo);
        liberarSfx(sfxRomperEscudo);
        liberarSfx(sfxCuracion);
        liberarSfx(sfxAntiGravedad);

    }

    private void liberarTex(Texture t) {
        if (t != null) t.dispose();
    }

    private void liberarSfx(Sound s) {
        if (s != null) s.dispose();
    }
}
