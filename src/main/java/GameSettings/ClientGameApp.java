package GameSettings;

import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.virtual.VirtualButton;
import com.almasb.fxgl.multiplayer.MultiplayerService;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.app.GameApplication;
import static com.almasb.fxgl.dsl.FXGL.*;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.net.Connection;

import component.GameLogic;
import javafx.scene.input.KeyCode;
import component.GameFactory;

public class ClientGameApp extends GameApplication {

    private final int anchoPantalla = 1400;
    private final int altoPantalla = 700;
    private Connection<Bundle> conexion;
    private Player player;

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(anchoPantalla);
        gameSettings.setHeight(altoPantalla);
        gameSettings.setTitle("Jugador Sonic");
        gameSettings.addEngineService(MultiplayerService.class);
    }

    @Override
    protected void initGame() {
        var client = getNetService().newTCPClient("localhost", 55555);
        client.setOnConnected(conn -> {
            conexion = conn;
            getGameWorld().addEntityFactory(new GameFactory());
            getExecutor().startAsyncFX(() -> onClient());
            System.out.println("Cliente conectado");
        });
        client.connectAsync();
        Jugar();
    }

    private void Jugar() {
        getGameWorld().addEntityFactory(new GameFactory());
        //
        spawn("fondo");
        Level level = setLevelFromMap("level.tmx");
        player = (Player) spawn("tails", 50, 150);
        GameLogic.enviarMensaje("Crear Tails", conexion);
        player = (Player) spawn("tails", 50, 150);
        //GameLogic.enviarMensaje("Crear Sonic", conexion);
    }

    private void onClient() {
        // manejo de mensajes
        /* 
        getService(MultiplayerService.class).addEntityReplicationReceiver(conexion, getGameWorld());
        getService(MultiplayerService.class).addInputReplicationSender(conexion, getInput());
        getService(MultiplayerService.class).addPropertyReplicationReceiver(conexion, getWorldProperties());
        */

        conexion.addMessageHandlerFX((conexion, bundle) -> {
            switch (bundle.getName()) {
                case "Crear Sonic":
                    break;
                
                case "Crear Tails":
                    
                    break;

                case "1":
                    break;

                case "2":
                    break;

                case "3":
                    break;
            }
        });
    }
    
    @Override
    protected void initInput() {
        //fixme: No funciona los botones virtuales en el cliente, no se por qué
        getInput().addAction(new UserAction("Mover a la izquierda") {
            @Override
            protected void onAction() {
                GameLogic.enviarMensaje("Mover a la izquierda", conexion);
                player.moverIzquierda();
            }
            @Override
            protected void onActionEnd() {
                GameLogic.enviarMensaje("Detente", conexion);
                player.detener();
            }
        }, KeyCode.A, VirtualButton.LEFT);

        getInput().addAction(new UserAction("Mover a la derecha") {
            @Override
            protected void onAction() {
                GameLogic.enviarMensaje("Mover a la derecha", conexion);
                player.moverDerecha();
            }
            @Override
            protected void onActionEnd() {
                GameLogic.enviarMensaje("Detente", conexion);
                player.detener();
            }
        }, KeyCode.D, VirtualButton.RIGHT);

        getInput().addAction(new UserAction("Saltar") {
            @Override
            protected void onActionBegin() {
                GameLogic.enviarMensaje("Saltar", conexion);
                player.saltar();
            }
        }, KeyCode.W, VirtualButton.A); // xbox

    };
}