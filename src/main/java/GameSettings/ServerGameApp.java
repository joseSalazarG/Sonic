package GameSettings;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.multiplayer.MultiplayerService;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.app.GameApplication;
import static com.almasb.fxgl.dsl.FXGL.*;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.net.Connection;
import component.GameFactory;
import component.Personajes.PlayerComponent;
import component.GameLogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServerGameApp extends GameApplication implements Serializable{
    private final int anchoPantalla = 500;
    private final int altoPantalla = 300;
    private Connection<Bundle> conexion;
    private List<Connection> conexiones = new ArrayList<>();
    private List<Bundle> personajesExistentes = new ArrayList<>();
    private Player player;

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(anchoPantalla);
        gameSettings.setHeight(altoPantalla);
        gameSettings.setTitle("Server");
        gameSettings.addEngineService(MultiplayerService.class);
    }

    @Override
    protected void initGame() {
        var server = getNetService().newTCPServer(55555);
        server.setOnConnected(conn -> {
            // Genera un id unico para el nuevo cliente
            String nuevoId = UUID.randomUUID().toString();
            Bundle tuId = new Bundle("TuID");
            tuId.put("id", nuevoId);
            conn.send(tuId);

            conexion = conn;
            getExecutor().startAsyncFX(this::onServer);
        });
        System.out.println("Servidor creado");
        server.startAsync();
        Jugar();
}

    private void Jugar(){
        getGameWorld().addEntityFactory(new GameFactory());
        spawn("fondo");
        player = null;
        Level level = setLevelFromMap("level.tmx");
        //player = spawn("player", 50, 150);
    }

    public void onServer() {
    conexion.addMessageHandlerFX((connection, bundle) -> {
        switch (bundle.getName()) {
            case "Mover a la izquierda":
            case "Mover a la derecha":
            case "Saltar":
            case "Detente":
                for (Connection<Bundle> conn : conexiones) {
                    if (conn != connection) {
                        conn.send(bundle);
                    }
                }
                break;
            // manda una actualizacion con la posicion de cada personaje
            case "SyncPos":
                String syncId = bundle.get("id");
                for (Bundle personaje : personajesExistentes) {
                    Object personajeId = personaje.get("id");
                    if (personajeId != null && personajeId.equals(syncId)) {
                        personaje.put("x", bundle.get("x"));
                        personaje.put("y", bundle.get("y"));
                        break;
                    }
                }
                for (Connection conn : conexiones) {
                    if (conn != connection) {
                        conn.send(bundle);
                    }
                }
                break;

            case "Crear Personaje":
                boolean existe = personajesExistentes.stream().anyMatch(
                    b -> b.get("id").equals(bundle.get("id"))
                );
                if (!existe) {
                    personajesExistentes.add(bundle);
                }
                for (Connection<Bundle> conn : conexiones) {
                    if (conn != connection) {
                        conn.send(bundle);
                    }
                }
                break;

            case "Hola":
                System.out.println("sonic se conecto");
                break;
        }
    });
    conexiones.add(conexion);

    // supuestamente acomoda el retraso, pero es pura paja
    for (Bundle personajeBundle : personajesExistentes) {
        Bundle crear = new Bundle("Crear Personaje");
        crear.put("id", personajeBundle.get("id"));
        crear.put("tipo", personajeBundle.get("tipo"));
        crear.put("x", personajeBundle.get("x"));
        crear.put("y", personajeBundle.get("y"));
        conexion.send(crear);

        Bundle sync = new Bundle("SyncPos");
        sync.put("id", personajeBundle.get("id"));
        sync.put("x", personajeBundle.get("x"));
        sync.put("y", personajeBundle.get("y"));
        conexion.send(sync);
    }
}

}