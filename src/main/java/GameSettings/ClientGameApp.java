package GameSettings;

import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.core.asset.AssetType;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.virtual.VirtualButton;
import com.almasb.fxgl.multiplayer.MultiplayerService;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.app.GameApplication;
import static com.almasb.fxgl.dsl.FXGL.*;
import static javafx.scene.text.Font.loadFont;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import component.GameLogic;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Font;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.time.TimerAction;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import component.GameFactory;
import component.Enemigos.EggmanComponent;
import component.Enemigos.RobotComponent;
import component.Personajes.KnucklesComponent;
import component.Personajes.PlayerComponent;
import component.Personajes.SonicComponent;
import component.Personajes.TailsComponent;
import javafx.scene.text.Text;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;

// TODO arreglar empujes entre entidades para mejor sincronizacion

public class ClientGameApp extends GameApplication {

    private final int anchoPantalla = 1000;
    private final int altoPantalla = 600;
    private Connection<Bundle> conexion;
    private Map<String, Player> personajeRemotos = new HashMap<>();
    private Player player;
    private String miID;
    private String personajePendiente = null;
    private int contadorAnillos = 0;
    private int contadorBasura = 0;
    private int contadorPapel = 0;
    private int contadorCaucho = 0;
    private boolean flag_Interactuar = false;
    private Entity stand_by;
    private boolean invulnerable = false;
    public GameLogic gameLogic;

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
        getGameScene().setBackgroundColor(Color.DARKBLUE);
        Music music = getAssetLoader().loadMusic("OST.mp3");
        //getAudioPlayer().loopMusic(music);
        gameLogic = new GameLogic();
        gameLogic.init();
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
        setLevelFromMap("mapazo.tmx");
        var client = getNetService().newTCPClient("localhost", 55555);
        client.setOnConnected(conn -> {
            conexion = conn;
            Bundle hola = new Bundle("Hola");
            conn.send(hola);
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

                    // Envia posicion inicial para sincronizar servidor
                    Bundle sync = new Bundle("SyncPos");
                    sync.put("id", miID);
                    sync.put("x", 50);
                    sync.put("y", 150);
                    conexion.send(sync);
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
                        gameLogic.cambiarTextoAnillos("anillos: " + contadorAnillos);
                    }
                    break;
                }

                case "RobotEliminado": {
                    String robotId = bundle.get("robotId");

                    getGameWorld().getEntitiesByType(GameFactory.EntityType.ROBOT_ENEMIGO).stream()
                        .filter(r -> robotId.equals(r.getProperties().getString("id")))
                        .findFirst()
                        .ifPresent(Entity::removeFromWorld);

                    break;
                }

                case "BasuraRecogida": {
                    String trashId = bundle.get("trashId");
                    List<Entity> basuras = new ArrayList<>();
                    basuras.addAll(getGameWorld().getEntitiesByType(GameFactory.EntityType.BASURA));
                    basuras.addAll(getGameWorld().getEntitiesByType(GameFactory.EntityType.PAPEL));
                    basuras.addAll(getGameWorld().getEntitiesByType(GameFactory.EntityType.CAUCHO));

                    basuras.stream()
                        .filter(r -> trashId.equals(r.getProperties().getString("id")))
                        .findFirst()
                        .ifPresent(entity -> {
                            String tipoBasura = entity.getProperties().getString("tipo");
                            entity.removeFromWorld();

                            if (bundle.get("playerId").equals(miID)) {
                                switch (tipoBasura) {
                                    case "papel":
                                        contadorPapel++;
                                        gameLogic.cambiarTextoPapel("Papel: " + contadorPapel);
                                        break;
                                    case "caucho":
                                        contadorCaucho++;
                                        gameLogic.cambiarTextoCaucho("Caucho: " + contadorCaucho);
                                        break;
                                    case "basura":
                                        contadorBasura++;
                                        gameLogic.cambiarTextoBasura("Basura: " + contadorBasura);
                                        break;
                                }
                            }
                        });

                    break;
                }

                case "CrearRobotEnemigo": {
                    double x = ((Number) bundle.get("x")).doubleValue();
                    double y = ((Number) bundle.get("y")).doubleValue();
                    String id = bundle.get("id");
                    Entity robot = spawn("robotEnemigo", x, y);
                    robot.getProperties().setValue("id", id); // Guarda el id para identificarlo luego
                    break;
                }

                case "CrearEggman": {
                    double x = ((Number) bundle.get("x")).doubleValue();
                    double y = ((Number) bundle.get("y")).doubleValue();

                    if (getGameWorld().getEntitiesByType(GameFactory.EntityType.EGGMAN).isEmpty()) {
                        spawn("eggman", x, y);
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

                case "crearbasura": {
                    double x = ((Number) bundle.get("x")).doubleValue();
                    double y = ((Number) bundle.get("y")).doubleValue();
                    String id = bundle.get("id");
                    String tipo = bundle.get("tipo");  

                    Entity basura = spawn(tipo, x, y);
                    basura.getProperties().setValue("id", id);
                    basura.getProperties().setValue("tipo", tipo);

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
                            getGameScene().getViewport().bindToEntity(player, anchoPantalla/2.0, altoPantalla/1.5);
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

                case "SyncEggmanPos": {
                    var eggmans = getGameWorld().getEntitiesByType(GameFactory.EntityType.EGGMAN);
                    if (!eggmans.isEmpty()) {
                        Entity eggman = eggmans.get(0);
                        double x = ((Number) bundle.get("x")).doubleValue();
                        double y = ((Number) bundle.get("y")).doubleValue();
                        eggman.setPosition(x, y);
                    }
                    break;
                }


                case "EstadoBasuraGlobal": {
                    int total = bundle.get("total");
                    int restante = bundle.get("restante");

                    GameLogic.agregarBarra((float) restante / total);
                    GameLogic.filtroColor((float) restante / total); // Cambia el tono
                    gameLogic.cambiarTextoBasuraGlobal("Basura restante: " + restante + "/" + total);
                    break;
                }

                case "SpawnArbol": {
                    double x = ((Number) bundle.get("x")).doubleValue();
                    double y = ((Number) bundle.get("y")).doubleValue();

                    spawn("arbol", x, y);
                    System.out.println("¡Arbol spawnado por el servidor!");
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

        getInput().addAction(new UserAction("Interactuar") {
            @Override
            protected void onActionBegin() {
                if (player == null) return;
                if (flag_Interactuar) { // Solo interactuar si se ha activado la bandera
                    // Enviar mensaje al servidor para interactuar con el entorno
                    Bundle bundle = new Bundle("Interactuar");
                    bundle.put("id", miID);
                    bundle.put("tipo", player.getTipo());
                    conexion.send(bundle);
                    player.interactuar();
                    recogerBasura(player, stand_by); // Llama al metodo recogerBasura con la entidad stand_by
                    // }
                }
            }
        }, KeyCode.E);
    }

    @Override
    protected void initUI() {
        //fixme: no funciona la fuente personalizada
        //Font fuente = getAssetLoader().load(AssetType.FONT,"SegaSonic.ttf");
        //GameLogic.agregarTexto("Anillos: 0", "yellow", 24, 20, 20);
        //GameLogic.agregarTexto("Basura: 0", "blue", 24, 20, 50);
        //GameLogic.agregarTexto("Papel: 0", "white", 24, 20, 80);
        //GameLogic.agregarTexto("Caucho: 0", "red", 24, 20, 110);
        //GameLogic.agregarTexto("Basura restante: 0", "orange", 24, 700, 20);
        //GameLogic.agregarTexto("Vidas: 3", "green", 24, 700, 50);
    }

   @Override
    protected void initPhysics() {

        onCollisionBegin(GameFactory.EntityType.PLAYER, GameFactory.EntityType.RING, (player, ring) -> {
            play("recoger.wav");
            String ringId = ring.getProperties().getString("id");
            Bundle recoger = new Bundle("RecogerAnillo");
            recoger.put("ringId", ringId);
            recoger.put("playerId", miID);
            conexion.send(recoger);
        });

        onCollisionBegin(GameFactory.EntityType.PLAYER, GameFactory.EntityType.BASURA, (player, basura) -> {
            if (player.hasComponent(SonicComponent.class) || player.hasComponent(TailsComponent.class) || player.hasComponent(KnucklesComponent.class)) {
                recogerBasura((Player)player, basura);
            }
        });

        onCollisionBegin(GameFactory.EntityType.PLAYER, GameFactory.EntityType.PAPEL, (player, papel) -> {
            if (player.hasComponent(TailsComponent.class)) {
                recogerBasura((Player)player, papel);
            }
        });

        onCollisionBegin(GameFactory.EntityType.KNUCKLES, GameFactory.EntityType.CAUCHO, (player, caucho) -> {
            if (player.hasComponent(KnucklesComponent.class)) {
                flag_Interactuar = true;
                stand_by = caucho; // Guarda la entidad caucho para interactuar
            }
        });

        onCollisionEnd(GameFactory.EntityType.PLAYER, GameFactory.EntityType.CAUCHO, (player, caucho) -> {
            flag_Interactuar = false;
        });

       onCollisionBegin(GameFactory.EntityType.PLAYER, GameFactory.EntityType.BASURA, (playerEntity, basura) -> {
           if (playerEntity.hasComponent(SonicComponent.class) || playerEntity.hasComponent(TailsComponent.class) || playerEntity.hasComponent(KnucklesComponent.class)) {
               recogerBasura((Player) playerEntity, basura);
           }
       });

       onCollisionBegin(GameFactory.EntityType.PLAYER, GameFactory.EntityType.ROBOT_ENEMIGO, (player, robot) -> {
           double alturaPlayer = player.getHeight();
           double alturaRobot = robot.getHeight();

           double bottomPlayer = player.getY() + alturaPlayer;
           double topRobot = robot.getY();

           boolean golpeDesdeArriba = bottomPlayer <= topRobot + 10;

           if (golpeDesdeArriba) {
               String robotId = robot.getProperties().getString("id");
               Bundle eliminar = new Bundle("EliminarRobot");
               eliminar.put("robotId", robotId);
               eliminar.put("playerId", miID);
               conexion.send(eliminar);
               player.getComponent(PhysicsComponent.class).setVelocityY(-300);
           } else {
               perderVidas();
           }
       });

        onCollisionBegin(GameFactory.EntityType.PLAYER, GameFactory.EntityType.EGGMAN, (player, eggman) -> {
            double alturaPlayer = player.getHeight();
            double alturaEggman = eggman.getHeight();

            double bottomPlayer = player.getY() + alturaPlayer;
            double topEggman = eggman.getY();

            boolean golpeDesdeArriba = bottomPlayer <= topEggman + 10;

            if (golpeDesdeArriba) {
                //todo: enviar mensaje al servidor para quitarle vidas al eggman
                //perderVidas(eggman);

                if (player.hasComponent(PhysicsComponent.class)) {
                    player.getComponent(PhysicsComponent.class).setVelocityY(-300);
                }
            } else {
                perderVidas();
            }
        });
    }

    private void recogerBasura(Player player, Entity basuraEntidad) {
        String trashId = basuraEntidad.getProperties().getString("id");

        String tipo = player.getTipo();

        Bundle recoger = new Bundle("RecogerBasura");
        recoger.put("trashId", trashId);
        recoger.put("playerId", miID);
        recoger.put("tipo", tipo);
        conexion.send(recoger);
    }

    private void perderVidas() {
        long ahora = System.currentTimeMillis();

        if (invulnerable){
            return;
        }
        
        if (contadorAnillos > 0) {
            play("perder_anillos.wav");
            contadorAnillos = 0;
            gameLogic.cambiarTextoAnillos("Anillos: " + contadorAnillos);
            activarInvencibilidad(3000, player);
            return; 
        }

        play("muerte.wav");
        player.restarVida();
        gameLogic.cambiarTextoVidas("Vidas: " + player.getVidas());
        if (player.estaMuerto()) {
            showGameOver();
        } else {
            activarInvencibilidad(3000, player);
        }
    }

    private void showGameOver() {
        getDialogService().showMessageBox("Game Over", () -> {
            FXGL.getGameController().exit();
        });
    }

    private void activarInvencibilidad(int milisegundos, Entity player) {
        invulnerable = true;

        TimerAction blinkAction = getGameTimer().runAtInterval(() -> {
            player.getViewComponent().setVisible(!player.getViewComponent().isVisible());
        }, Duration.millis(200));

        getGameTimer().runOnceAfter(() -> {
            invulnerable = false;
            player.getViewComponent().setVisible(true); 
            blinkAction.expire();
        }, Duration.millis(milisegundos));
    }
}