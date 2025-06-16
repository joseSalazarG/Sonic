package io.github.sonic.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import main.Main;
import com.esotericsoftware.kryonet.Client;

import java.io.IOException;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true; // Recommended, but not required.
       // Client client = iniciarCliente();
       // initialize(new Main(client, configuration));
    }

    private static Client iniciarCliente() throws IOException {
        try {
            Client client = new Client();
            client.start();
            client.connect(5000, "localhost", 54555, 54777);
            return client;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
