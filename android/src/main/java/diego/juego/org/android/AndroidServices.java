package diego.juego.org.android;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import diego.juego.org.PlatformServices;

public class AndroidServices implements PlatformServices {

    private final Vibrator vibrator;

    public AndroidServices(Context context) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void vibrate(int ms) {
        if (vibrator == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE)
            );
        } else {
            vibrator.vibrate(ms);
        }
    }

    @Override
    public void vibratePattern(long[] pattern) {
        if (vibrator == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(pattern, -1)
            );
        } else {
            vibrator.vibrate(pattern, -1);
        }
    }
}
