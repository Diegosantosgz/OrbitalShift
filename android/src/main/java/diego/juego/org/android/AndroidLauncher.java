package diego.juego.org.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import diego.juego.org.Main;

public class AndroidLauncher extends AndroidApplication {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        config.useImmersiveMode = true;
        config.useAccelerometer = true;
        config.useGyroscope = true;

        initialize(new Main(), config);
    }
}
