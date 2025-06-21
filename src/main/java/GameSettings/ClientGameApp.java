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

import component.SonicLogic;
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
        gameSettings.setTitle("Jugador");
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

    private void Jugar(){
        getGameWorld().addEntityFactory(new GameFactory());
        spawn("fondo");
        player = null;
        Level level = setLevelFromMap("level.tmx");
        player = (Player) spawn("sonic", 50, 150);
        //SonicLogic.enviarMensaje();
    }

    private void onClient() {
        // manejo de mensajes
        conexion.addMessageHandlerFX((conexion, bundle) -> {
            switch (bundle.getName()) {
                case "Mano inicial":
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
                SonicLogic.enviarMensaje("Mover a la izquierda", conexion);
                player.moverIzquierda();
            }
            @Override
            protected void onActionEnd() {
                SonicLogic.enviarMensaje("Detente", conexion);
                player.detener();
            }
        }, KeyCode.A, VirtualButton.LEFT);

        getInput().addAction(new UserAction("Mover a la derecha") {
            @Override
            protected void onAction() {
                SonicLogic.enviarMensaje("Mover a la derecha", conexion);
                player.moverDerecha();
            }
            @Override
            protected void onActionEnd() {
                SonicLogic.enviarMensaje("Detente", conexion);
                player.detener();
            }
        }, KeyCode.D, VirtualButton.RIGHT);

        getInput().addAction(new UserAction("Saltar") {
            @Override
            protected void onActionBegin() {
                SonicLogic.enviarMensaje("Saltar", conexion);
                player.saltar();
            }
        }, KeyCode.W, VirtualButton.A); // xbox

    };
}