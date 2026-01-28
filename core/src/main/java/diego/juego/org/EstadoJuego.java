package diego.juego.org;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.Locale;

public final class EstadoJuego {
    private EstadoJuego() {}

    public static boolean multitouchActivado = true;
    public static boolean vibracionActivada = true;
    public static boolean musicaActivada = true;
    public static boolean sfxActivados = true;
    public static int nivelActual = 1;
    public static int ultimoScoreRegistrado = -1;

    public static boolean scoreYaRegistrado(int score) {
        return ultimoScoreRegistrado == score;
    }

    public static void marcarScoreRegistrado(int score) {
        ultimoScoreRegistrado = score;
    }


    // ===== TOP 10 =====
    public static final int MAX_SCORES = 10;
    public static int[] topScores = new int[MAX_SCORES];
    public static String[] topNames = new String[MAX_SCORES];

    private static final String PREFS = "orbital_shift_scores";

    /** Llamar una vez al arrancar el juego (por ejemplo en Main.create()) */
    public static void cargarScores() {
        Preferences p = Gdx.app.getPreferences(PREFS);

        for (int i = 0; i < MAX_SCORES; i++) {
            topScores[i] = p.getInteger("score_" + i, 0);
            topNames[i] = p.getString("name_" + i, "---");
            if (topNames[i] == null || topNames[i].trim().isEmpty()) topNames[i] = "---";
        }
    }

    private static void guardarScores() {
        Preferences p = Gdx.app.getPreferences(PREFS);

        for (int i = 0; i < MAX_SCORES; i++) {
            p.putInteger("score_" + i, topScores[i]);
            p.putString("name_" + i, topNames[i]);
        }
        p.flush();
    }

    public static void resetTopScores() {
        for (int i = 0; i < MAX_SCORES; i++) {
            topScores[i] = 0;
            topNames[i] = "---";
        }
        guardarScores();
    }

    /** Devuelve true si el score entra en el Top10 (comparando contra el peor). */
    public static boolean entraEnTop10(int score) {
        return score > topScores[MAX_SCORES - 1];
    }

    /** Inserta score + iniciales en el top10, ordena y guarda. */
    public static void insertarScore(String iniciales, int score) {
        if (iniciales == null) iniciales = "---";
        iniciales = iniciales.trim().toUpperCase(Locale.ROOT);
        if (iniciales.length() > 3) iniciales = iniciales.substring(0, 3);
        while (iniciales.length() < 3) iniciales += "_"; // opcional: rellena

        // busca posición
        int pos = MAX_SCORES;
        for (int i = 0; i < MAX_SCORES; i++) {
            if (score > topScores[i]) { pos = i; break; }
        }
        if (pos >= MAX_SCORES) return;

        // desplaza hacia abajo
        for (int i = MAX_SCORES - 1; i > pos; i--) {
            topScores[i] = topScores[i - 1];
            topNames[i] = topNames[i - 1];
        }

        topScores[pos] = score;
        topNames[pos] = iniciales;

        guardarScores();
    }

    /** Convenience: récord (mejor score). */
    public static int getRecord() {
        return topScores[0];
    }
}
