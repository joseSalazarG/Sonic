package GameSettings;

import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.virtual.VirtualButton;
import com.almasb.fxgl.multiplayer.MultiplayerService;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.app.GameApplication;
import static com.almasb.fxgl.dsl.FXGL.*;

import java.util.HashMap;
import java.util.Map;

import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;

import component.GameLogic;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import component.GameFactory;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;

// TODO arreglar colisiones y spawn del jugador 1 en la pantalla del jugador 2

public class ClientGameApp extends GameApplication {

    private final int anchoPantalla = 1400;
    private final int altoPantalla = 700;
    private Connection<Bundle> conexion;
    private Map<String, Player> personajeRemotos = new HashMap<>();
    private Player player;
    private String miID;

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(anchoPantalla);
        gameSettings.setHeight(altoPantalla);
        gameSettings.setTitle("Jugador Sonic");
        gameSettings.addEngineService(MultiplayerService.class);
    }

    private String personajeSeleccionado = null;

    @Override
    protected void initGame() {
        miID = java.util.UUID.randomUUID().toString();
        getGameWorld().addEntityFactory(new GameFactory());

        // Menu de furros
        showCharacterSelectionMenu();
    }

    private void showCharacterSelectionMenu() {
        getExecutor().startAsyncFX(() -> {
            Stage stage = new Stage();
            VBox root = new VBox(15);
            root.setAlignment(Pos.CENTER);
            Text title = new Text("Selecciona tu personaje");
            Button btnSonic = new Button("Sonic");
            Button btnTails = new Button("Tails");
            Button btnKnuckles = new Button("Knuckles");

            btnSonic.setOnAction(e -> {
                personajeSeleccionado = "sonic";
                stage.close();
                startNetworkAndGame();
            });
            btnTails.setOnAction(e -> {
                personajeSeleccionado = "tails";
                stage.close();
                startNetworkAndGame();
            });
            btnKnuckles.setOnAction(e -> {
                personajeSeleccionado = "knuckles";
                stage.close();
                startNetworkAndGame();
            });

            root.getChildren().addAll(title, btnSonic, btnTails, btnKnuckles);
            Scene scene = new Scene(root, 300, 200);
            stage.setScene(scene);
            stage.setTitle("Selecciona personaje");
            stage.show();
        });
    }

     private void startNetworkAndGame() {
        spawn("fondo");
        setLevelFromMap("level.tmx");

        var client = getNetService().newTCPClient("localhost", 55555);
        client.setOnConnected(conn -> {
            conexion = conn;
            getExecutor().startAsyncFX(() -> onClient());
            System.out.println("Cliente conectado");
        });
        client.connectAsync();
    }

    private void Jugar() {
        getGameWorld().addEntityFactory(new GameFactory());
        //
        spawn("fondo");
        Level level = setLevelFromMap("level.tmx");
        //GameLogic.enviarMensaje("Crear Sonic", conexion);
    }

    private void onClient() {
        conexion.addMessageHandlerFX((conexion, bundleMsg) -> {
            switch (bundleMsg.getName()) {
                case "TuID": {
                    miID = bundleMsg.get("id");
                    player = (Player) spawn(personajeSeleccionado, 50, 150);

                    Bundle crearPersonaje = new Bundle("Crear Personaje");
                    crearPersonaje.put("id", miID);
                    crearPersonaje.put("tipo", personajeSeleccionado);
                    crearPersonaje.put("x", 50);
                    crearPersonaje.put("y", 150);
                    conexion.send(crearPersonaje);
                    break;
                }


                case "Crear Personaje": {
                    String id = bundleMsg.get("id");
                    String tipo = bundleMsg.get("tipo");
                    Number xNum = bundleMsg.get("x");
                    Number yNum = bundleMsg.get("y");
                    double x = xNum.doubleValue();
                    double y = yNum.doubleValue();
                    if (!id.equals(miID)) {
                        System.out.println("Creando jugador remoto: " + id);
                        Player remotePlayer = (Player) spawn(tipo, x, y);
                        personajeRemotos.put(id, remotePlayer);
                    } else {
                        System.out.println("Creando jugador local: " + id);
                    }
                    break;
                }
                case "Mover a la izquierda":
                case "Mover a la derecha":
                case "Saltar":
                case "Detente": {
                    String moveId = bundleMsg.get("id");
                    if (moveId.equals(miID)) return; // Ignora tus propios mensajes
                    Player remotePlayer = personajeRemotos.get(moveId);
                    if (remotePlayer == null) return;
                    switch (bundleMsg.getName()) {
                        case "Mover a la izquierda":
                            remotePlayer.moverIzquierda();
                            break;
                        case "Mover a la derecha":
                            remotePlayer.moverDerecha();
                            break;
                        case "Saltar":
                            remotePlayer.saltar();
                            break;
                        case "Detente":
                            remotePlayer.detener();
                            break;
                    }
                    break;
                }
                case "SyncPos": {
                    String syncId = bundleMsg.get("id");
                    if (syncId.equals(miID)) return;
                    Player remotePlayer = personajeRemotos.get(syncId);
                    if (remotePlayer != null) {
                        Number xNum = bundleMsg.get("x");
                        Number yNum = bundleMsg.get("y");
                        remotePlayer.setX(xNum.doubleValue());
                        remotePlayer.setY(yNum.doubleValue());
                    }
                    break;
                }
            }
        });
    }

    @Override
    protected void onUpdate(double tpf) {
        if (conexion != null && player != null) {
            if (System.currentTimeMillis() % 100 < 16) {
                Bundle bundle = new Bundle("SyncPos");
                bundle.put("id", miID);
                bundle.put("x", player.getX());
                bundle.put("y", player.getY());
                conexion.send(bundle);
            }
        }
    }
            
    @Override
    protected void initInput() {
        //fixme: No funciona los botones virtuales en el cliente, no se por qué
       getInput().addAction(new UserAction("Mover a la izquierda") {
            @Override
            protected void onAction() {
                Bundle bundle = new Bundle("Mover a la izquierda");
                bundle.put("id", miID);
                conexion.send(bundle);
                player.moverIzquierda();
            }
            @Override
            protected void onActionEnd() {
                Bundle bundle = new Bundle("Detente");
                bundle.put("id", miID);
                conexion.send(bundle);
                player.detener();
            }
        }, KeyCode.A, VirtualButton.LEFT);

         getInput().addAction(new UserAction("Mover a la derecha") {
            @Override
            protected void onAction() {
                Bundle bundle = new Bundle("Mover a la derecha");
                bundle.put("id", miID);
                conexion.send(bundle);
                player.moverDerecha();
            }
            @Override
            protected void onActionEnd() {
                Bundle bundle = new Bundle("Detente");
                bundle.put("id", miID);
                conexion.send(bundle);
                player.detener();
            }
        }, KeyCode.D, VirtualButton.RIGHT);

        getInput().addAction(new UserAction("Saltar") {
            @Override
            protected void onActionBegin() {
                Bundle bundle = new Bundle("Saltar");
                bundle.put("id", miID);
                conexion.send(bundle);
                player.saltar();
            }
        }, KeyCode.W, VirtualButton.A);

    };
}