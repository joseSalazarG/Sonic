package io.github.sonic.lwjgl3;

import Multiplayer.ClientListener;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import main.Main_Client;
import main.Main_Server;
import com.esotericsoftware.kryonet.Client;

import java.io.IOException;

public class Client_Launcher {

    public static void main(String[] args) {

        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.

        try {
            createApplication(iniciarCliente());
        } catch (IOException e) {
            // si no se conecta a un servidor entonces lo crea
            e.printStackTrace();
            System.out.println("No se pudo conectar al servidor, iniciando cliente...");
        }
    }

    private static Client iniciarCliente() throws IOException {
        Client client = new Client();
        client.start();
        client.connect(5000, "localhost", 54555, 54777);
        client.addListener(new ClientListener());
        return client;
    }

    private static Lwjgl3Application createApplication(Client client) {
        return new Lwjgl3Application(new Main_Client(client), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Cliente_libgdx");
        //// Vsync limits the frames per second to what your hardware can display, and helps eliminate
        //// screen tearing. This setting doesn't always work on Linux, so the line after is a safeguard.
        configuration.useVsync(true);
        //// Limits FPS to the refresh rate of the currently active monitor, plus 1 to try to match fractional
        //// refresh rates. The Vsync setting above should limit the actual FPS to match the monitor.
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
        //// useful for testing performance, but can also be very stressful to some hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.

        configuration.setWindowedMode(960, 640);
        //// You can change these files; they are in lwjgl3/src/main/resources/ .
        //// They can also be loaded from the root of assets/ .
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }
}
