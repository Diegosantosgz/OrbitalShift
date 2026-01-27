package diego.juego.org;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public final class EstadoJuego {
    private EstadoJuego() {}

    public static boolean multitouchActivado = true;
    public static boolean vibracionActivada = true;
    public static boolean musicaActivada = true;
    public static boolean sfxActivados = true;

    public static int nivelActual = 1;

    // ===== TOP 10 ARCADE (score + 3 iniciales) =====
    private static final String PREFS_NAME = "orbital_shift_prefs";
    private static final String KEY_SCORE_PREFIX = "top_score_"; // 0..9
    private static final String KEY_NAME_PREFIX  = "top_name_";  // 0..9
    private static final int TOP_N = 10;

    private static Preferences prefs;

    public static int[] topScores = new int[TOP_N];
    public static String[] topNames = new String[TOP_N]; // "AAA"

    public static void cargar() {
        if (prefs == null) prefs = Gdx.app.getPreferences(PREFS_NAME);

        for (int i = 0; i < TOP_N; i++) {
            topScores[i] = prefs.getInteger(KEY_SCORE_PREFIX + i, 0);
            topNames[i]  = prefs.getString(KEY_NAME_PREFIX + i, "---");
            if (topNames[i] == null || topNames[i].trim().isEmpty()) topNames[i] = "---";
        }
    }

    private static void guardar() {
        if (prefs == null) prefs = Gdx.app.getPreferences(PREFS_NAME);

        for (int i = 0; i < TOP_N; i++) {
            prefs.putInteger(KEY_SCORE_PREFIX + i, topScores[i]);
            prefs.putString(KEY_NAME_PREFIX + i, topNames[i] == null ? "---" : topNames[i]);
        }
        prefs.flush();
    }

    /** ¿Esta puntuación entra al Top 10? */
    public static boolean entraEnTop10(int score) {
        if (score <= 0) return false;
        return score > topScores[TOP_N - 1];
    }

    /** Inserta score + iniciales (normaliza a 3 letras) y guarda. */
    public static void insertarEnTop10(int score, String iniciales) {
        if (score <= 0) return;

        String name = normalizarIniciales(iniciales);

        // si no entra, fuera
        if (!entraEnTop10(score)) return;

        for (int i = 0; i < TOP_N; i++) {
            if (score > topScores[i]) {
                // desplazar hacia abajo
                for (int j = TOP_N - 1; j > i; j--) {
                    topScores[j] = topScores[j - 1];
                    topNames[j]  = topNames[j - 1];
                }
                topScores[i] = score;
                topNames[i]  = name;
                guardar();
                return;
            }
        }
    }

    private static String normalizarIniciales(String s) {
        if (s == null) s = "";
        s = s.trim().toUpperCase();

        // deja solo letras/números
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isLetterOrDigit(c)) sb.append(c);
            if (sb.length() == 3) break;
        }
        while (sb.length() < 3) sb.append('-');
        return sb.toString();
    }

    public static int getMejorScore() { return topScores[0]; }
    public static String getMejorNombre() { return topNames[0]; }

    public static void resetTop() {
        for (int i = 0; i < TOP_N; i++) {
            topScores[i] = 0;
            topNames[i] = "---";
        }
        guardar();
    }
}
