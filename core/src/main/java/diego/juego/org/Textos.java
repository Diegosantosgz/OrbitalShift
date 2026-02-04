package diego.juego.org;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;

public class Textos {

    private I18NBundle bundle;

    public Textos() {
        recargar();
    }

    public void recargar() {
        Locale locale = (EstadoJuego.idiomaActual == EstadoJuego.Idioma.EN)
            ? Locale.ENGLISH
            : new Locale("es");

        try {
            FileHandle base = Gdx.files.internal("idiomas/strings");
            bundle = I18NBundle.createBundle(base, locale);
        } catch (Throwable t) {
            bundle = null;
            Gdx.app.error("Textos", "No se pudo cargar idiomas/strings*.properties", t);
        }
    }

    public String t(String key, Object... args) {
        if (bundle == null) return key;
        try {
            return bundle.format(key, args);
        } catch (Throwable t) {
            return key;
        }
    }
}
