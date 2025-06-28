package GameSettings;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.virtual.VirtualButton;
import com.almasb.fxgl.multiplayer.MultiplayerService;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.app.GameApplication;
import static com.almasb.fxgl.dsl.FXGL.*;

import java.util.HashMap;
import java.util.Map;

import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.net.Connection;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import component.GameFactory;
import javafx.scene.text.Text;
import javafx.scene.Scene;

// TODO arreglar empujes entre entidades para mejor sincronizacion

public class ClientGameApp extends GameApplication {

    private final int anchoPantalla = 1400;
    private final int altoPantalla = 700;
    private Connection<Bundle> conexion;
    private Map<String, Player> personajeRemotos = new HashMap<>();
    private Player player;
    private String miID;
    private String personajePendiente = null;
    private int contadorAnillos = 0;
    private Text textoAnillos;

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(anchoPantalla);
        gameSettings.setHeight(altoPantalla);
        gameSettings.setTitle("Jugador Sonic");
        gameSettings.addEngineService(MultiplayerService.class);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GameFactory());
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
                personajePendiente = "sonic";
                
                stage.close();
                startNetworkAndGame();
            });
            btnTails.setOnAction(e -> {
                personajePendiente = "tails";
                
                stage.close();
                startNetworkAndGame();
            });
            btnKnuckles.setOnAction(e -> {
                personajePendiente = "knuckles";
                
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
        setLevelFromMap("test - copia.tmx");
        var client = getNetService().newTCPClient("localhost", 55555);
        client.setOnConnected(conn -> {
            conexion = conn;
            getExecutor().startAsyncFX(() -> onClient());
            System.out.println("Cliente conectado");
        });
        client.connectAsync();
    }

    private void onClient() {
        conexion.addMessageHandlerFX((conexion, bundle) -> {
            switch (bundle.getName()) {
                case "TuID": {
                    miID = bundle.get("id");
                    Bundle solicitar = new Bundle("SolicitarCrearPersonaje");
                    solicitar.put("id", miID);
                    solicitar.put("tipo", personajePendiente);
                    conexion.send(solicitar);

                    // Envia posición inicial para sincronizar servidor
                    Bundle sync = new Bundle("SyncPos");
                    sync.put("id", miID);
                    sync.put("x", 50);
                    sync.put("y", 150);
                    conexion.send(sync);
                    break;
                }

                case "CrearRobotEnemigo": {
                    Number xNum = bundle.get("x");
                    Number yNum = bundle.get("y");
                    double x = xNum.doubleValue();
                    double y = yNum.doubleValue();
                    if (getGameWorld().getEntitiesByType(component.GameFactory.EntityType.ROBOT_ENEMIGO).isEmpty()) {
                        spawn("robotEnemigo", x, y);
                    }
                    break;
                }

                case "crearRing": {
                    double x = ((Number) bundle.get("x")).doubleValue();
                    double y = ((Number) bundle.get("y")).doubleValue();
                    String id = bundle.get("id");

                    Entity ring = spawn("ring", x, y);
                    ring.getProperties().setValue("id", id); // Guarda el id para identificarlo luego
                    break;
                }

                case "AnilloRecogido": {
                    String ringId = bundle.get("ringId");

                    getGameWorld().getEntitiesByType(GameFactory.EntityType.RING).stream()
                        .filter(r -> ringId.equals(r.getProperties().getString("id")))
                        .findFirst()
                        .ifPresent(Entity::removeFromWorld);

                    // Actualiza contador si el jugador es uno mismo
                    String playerId = bundle.get("playerId");
                    if (playerId.equals(miID)) {
                        contadorAnillos++;
                        textoAnillos.setText("anillos: " + contadorAnillos);
                    }
                    break;
                }


                case "Crear Personaje": {
                    String id = bundle.get("id");
                    String tipo = bundle.get("tipo");
                    double x = ((Number)bundle.get("x")).doubleValue();
                    double y = ((Number)bundle.get("y")).doubleValue();

                    if (id.equals(miID)) {
                        if (player == null) {
                            Entity entidad = spawn(tipo, x, y);
                            player = (Player) entidad;
                            // mitad de la pantalla en x, y un poco mas abajo en y
                            getGameScene().getViewport().bindToEntity(player, anchoPantalla/2.0, altoPantalla/1.75);
                            getGameScene().getViewport().setLazy(true);
                        } else {
                            player.setX(x);
                            player.setY(y);
                        }
                    } else {
                        Player remotePlayer = personajeRemotos.get(id);
                        if (remotePlayer == null) {
                            Entity entidad = spawn(tipo, x, y);
                            remotePlayer = (Player) entidad;;
                            personajeRemotos.put(id, remotePlayer);
                        } else {
                            remotePlayer.setX(x);
                            remotePlayer.setY(y);
                        }
                    }

                    break;
                }

                case "Mover a la izquierda":
                case "Mover a la derecha":
                case "Saltar":
                case "Detente": {
                    String moveId = bundle.get("id");
                    if (moveId.equals(miID)) {
                        return; // Ignora tus propios mensajes
                    }
                    Player remotePlayer = personajeRemotos.get(moveId);
                    if (remotePlayer == null) {
                        return;
                    }
                    switch (bundle.getName()) {
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
                    String syncId = bundle.get("id");
                    if (syncId.equals(miID)) {
                        return; // Ignorarte a ti mismo
                    }

                    Player remotePlayer = personajeRemotos.get(syncId);
                    if (remotePlayer != null) {
                        Number xNum = bundle.get("x");
                        Number yNum = bundle.get("y");
                        remotePlayer.setX(xNum.doubleValue());
                        remotePlayer.setY(yNum.doubleValue());
                    } else {
                    }
                    break;
                }
                case "SyncRobotPos": {
                    var robots = getGameWorld().getEntitiesByType(GameFactory.EntityType.ROBOT_ENEMIGO);
                    if (!robots.isEmpty()) {
                        Entity robot = robots.get(0);
                        double x = ((Number) bundle.get("x")).doubleValue();
                        double y = ((Number) bundle.get("y")).doubleValue();
                        robot.setPosition(x, y);
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
        getInput().addAction(new UserAction("Mover a la izquierda") {
            @Override
            protected void onAction() {
                if (player == null) return;
                Bundle bundle = new Bundle("Mover a la izquierda");
                bundle.put("id", miID);
                conexion.send(bundle);
                player.moverIzquierda();
                Bundle sync = new Bundle("SyncPos");
                sync.put("id", miID);
                sync.put("x", player.getX());
                sync.put("y", player.getY());
                conexion.send(sync);
            }
            @Override
            protected void onActionEnd() {
                if (player == null) return;
                Bundle bundle = new Bundle("Detente");
                bundle.put("id", miID);
                conexion.send(bundle);
                player.detener();
                Bundle sync = new Bundle("SyncPos");
                sync.put("id", miID);
                sync.put("x", player.getX());
                sync.put("y", player.getY());
                conexion.send(sync);
            }
        }, KeyCode.A, VirtualButton.LEFT);

        getInput().addAction(new UserAction("Mover a la derecha") {
            @Override
            protected void onAction() {
                if (player == null) return;
                Bundle bundle = new Bundle("Mover a la derecha");
                bundle.put("id", miID);
                conexion.send(bundle);
                player.moverDerecha();
                Bundle sync = new Bundle("SyncPos");
                sync.put("id", miID);
                sync.put("x", player.getX());
                sync.put("y", player.getY());
                conexion.send(sync);
            }
            @Override
            protected void onActionEnd() {
                if (player == null) return;
                Bundle bundle = new Bundle("Detente");
                bundle.put("id", miID);
                conexion.send(bundle);
                player.detener();
                Bundle sync = new Bundle("SyncPos");
                sync.put("id", miID);
                sync.put("x", player.getX());
                sync.put("y", player.getY());
                conexion.send(sync);
            }
        }, KeyCode.D, VirtualButton.RIGHT);

        getInput().addAction(new UserAction("Saltar") {
            @Override
            protected void onActionBegin() {
                if (player == null) return;
                Bundle bundle = new Bundle("Saltar");
                bundle.put("id", miID);
                conexion.send(bundle);
                player.saltar();
                Bundle sync = new Bundle("SyncPos");
                sync.put("id", miID);
                sync.put("x", player.getX());
                sync.put("y", player.getY());
                conexion.send(sync);
            }
        }, KeyCode.W, VirtualButton.A);
    }

    @Override
    protected void initUI() {
        textoAnillos = new Text("anillos: 0");
        textoAnillos.setStyle("-fx-font-size: 24px; -fx-fill: white;");
        addUINode(textoAnillos, 20, 20);
    }

    @Override
    protected void initPhysics() {
        onCollisionBegin(GameFactory.EntityType.PLAYER, GameFactory.EntityType.RING, (player, ring) -> {
            String ringId = ring.getProperties().getString("id");
            Bundle recoger = new Bundle("RecogerAnillo");
            recoger.put("ringId", ringId);
            recoger.put("playerId", miID);
            conexion.send(recoger);
        });
    }

}
