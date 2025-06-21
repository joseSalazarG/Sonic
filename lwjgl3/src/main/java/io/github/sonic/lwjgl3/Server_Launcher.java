package io.github.sonic.lwjgl3;

import Multiplayer.ServerListener;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.esotericsoftware.kryonet.Server;
import main.Main_Server;
import com.esotericsoftware.kryonet.Client;

import java.io.IOException;

/** Launches the desktop (LWJGL3) application. */
public class Server_Launcher {
    public static void main(String[] args) {

        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.

        createApplication(iniciarServer());
    }

    private static Server iniciarServer() {
        Server server = new Server();
        server.start();
        try {
            server.bind(54555, 54777);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.addListener(new ServerListener(server, Main_Server.datosJuego));
        return server;
    }


    private static Lwjgl3Application createApplication(Server server) {
        return new Lwjgl3Application(new Main_Server(server), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("sonic_libgdx");
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
