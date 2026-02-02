package diego.juego.org;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public final class EstadoJuego {
    private EstadoJuego() {}

    public static boolean multitouchActivado = true;
    public static boolean vibracionActivada = true;
    public static boolean musicaActivada = true;
    public static boolean sfxActivados = true;
    public static boolean pidioSiglasEnEsteGameOver = false;


    public static int nivelActual = 1;

    // ===== TOP 10 ARCADE =====
    public static final int MAX_RECORDS = 10;

    private static final int[] scores = new int[MAX_RECORDS];
    private static final String[] siglas = new String[MAX_RECORDS];

    private static final String PREFS = "orbitalshift_scores";


    // NUEVO: para no pedir siglas 20 veces con el mismo score
    private static int scoreSiglasYaPedidas = -1;

    public static boolean yaSePidieronSiglasPara(int score) {
        return scoreSiglasYaPedidas == score;
    }

    public static void marcarSiglasPedidasPara(int score) {
        scoreSiglasYaPedidas = score;
    }


    static {
        // defaults
        for (int i = 0; i < MAX_RECORDS; i++) {
            scores[i] = 0;
            siglas[i] = "---";
        }
    }

    // ===== Persistencia =====
    public static void cargarScores() {
        Preferences p = Gdx.app.getPreferences(PREFS);
        for (int i = 0; i < MAX_RECORDS; i++) {
            scores[i] = p.getInteger("score_" + i, 0);
            siglas[i] = p.getString("siglas_" + i, "---");
            if (siglas[i] == null || siglas[i].length() != 3) siglas[i] = "---";
        }
    }
    public static void resetFlagsGameOver() {
        pidioSiglasEnEsteGameOver = false;
        scoreSiglasYaPedidas = -1;
    }


    public static void guardarScores() {
        Preferences p = Gdx.app.getPreferences(PREFS);
        for (int i = 0; i < MAX_RECORDS; i++) {
            p.putInteger("score_" + i, scores[i]);
            p.putString("siglas_" + i, siglas[i]);
        }
        p.flush();
    }

    // ===== Lógica top 10 =====
    public static boolean entraEnTop10(int score) {
        return score > scores[MAX_RECORDS - 1];
    }

    public static void insertarTopScore(String s, int score) {
        if (!entraEnTop10(score)) return;

        String cleaned = limpiarSiglas(s);

        int pos = MAX_RECORDS - 1;
        while (pos > 0 && score > scores[pos - 1]) pos--;

        for (int i = MAX_RECORDS - 1; i > pos; i--) {
            scores[i] = scores[i - 1];
            siglas[i] = siglas[i - 1];
        }

        scores[pos] = score;
        siglas[pos] = cleaned;

        guardarScores();
    }

    private static String limpiarSiglas(String s) {
        if (s == null) s = "";
        StringBuilder out = new StringBuilder(3);
        for (int i = 0; i < s.length() && out.length() < 3; i++) {
            char c = s.charAt(i);
            if (Character.isLetterOrDigit(c)) out.append(Character.toUpperCase(c));
        }
        while (out.length() < 3) out.append('-');
        return out.toString();
    }

    // ===== Reset =====
    public static void resetTop() {
        for (int i = 0; i < MAX_RECORDS; i++) {
            scores[i] = 0;
            siglas[i] = "---";
        }
        guardarScores();
    }

    // Alias para no romper escenas viejas
    public static void resetTopScores() { resetTop(); }

    // ===== Getters =====
    public static int getScore(int i) { return scores[i]; }
    public static String getSiglas(int i) { return siglas[i]; }

    public static int getRecord() { return scores[0]; }
}
