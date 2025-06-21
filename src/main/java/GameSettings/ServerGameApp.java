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

public class ServerGameApp extends GameApplication implements Serializable{
    private final int anchoPantalla = 1400;
    private final int altoPantalla = 700;
    private Connection<Bundle> conexion;
    private List<Connection> conexiones = new ArrayList<>();
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
        // manejo de mensajes
        
        conexion.addMessageHandlerFX((connection, bundle) -> {
            switch (bundle.getName()) {

                case "Mover a la izquierda":
                    System.out.println("Jugador movio a la izquierda");
                    //player.getComponent(PlayerComponent.class).moverIzquierda();
                    break;

                case "Mover a la derecha":
                    System.out.println("Jugador movio a la derecha");
                    //player.getComponent(PlayerComponent.class).moverDerecha();
                    break;

                case "Saltar":
                    System.out.println("Jugador salto");
                    //player.getComponent(PlayerComponent.class).saltar();
                    break;

                case "Detente":
                    System.out.println("Jugador se detuvo");
                    //player.getComponent(PlayerComponent.class).detener();
                    break;

                case "Crear Sonic":
                    // responderle al cliente que envio el mensaje
                    GameLogic.enviarMensaje("Crear Sonic", connection);
                    // para decirle a los demas que un jugador escogio Sonic
                    for (Connection<Bundle> conn : conexiones) {
                        if (conn != connection) {
                            GameLogic.enviarMensaje("Alguien escogio Sonic", connection);
                        }
                    }
                    break;
             
                case "Crear Tails":
                    
                    for (Connection<Bundle> conn : conexiones) {
                        if (conn != connection) {

                        }
                        //
                    }
                    break;

                case "Hola":
                    System.out.println("sonic se conecto");
                    break;
            }
        });
        conexiones.add(conexion);
    }

    /*
    public void enviarCarta(){
        for (Connection conn : conexiones) {
            UnoLogic.enviarMensaje("Nueva carta del servidor", carta_del_servidor, conn);
        }
    }
    */
}