package GameSettings;

import com.almasb.fxgl.app.MenuItem;
import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.virtual.VirtualButton;
import com.almasb.fxgl.multiplayer.MultiplayerService;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.app.GameApplication;
import static com.almasb.fxgl.dsl.FXGL.*;

import java.util.*;

import com.almasb.fxgl.ui.FXGLButton;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.ui.FXGLDialogFactoryServiceProvider;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import component.GameFactory;
import component.Personajes.KnucklesComponent;
import component.Personajes.SonicComponent;
import component.Personajes.TailsComponent;
import javafx.scene.text.Text;
import javafx.scene.Scene;

public class ClientGameApp extends GameApplication {

    private final int anchoPantalla = 1000;
    private final int altoPantalla = 600;
    private Connection<Bundle> conexion;
    private Map<String, Player> personajeRemotos = new HashMap<>();
    private Player player;
    private int contadorAnillos = 0;
    private int contadorBasura = 0;
    private int contadorPapel = 0;
    private int contadorCaucho = 0;
    private boolean flag_Interactuar = false;
    public String clientID;
    private int contadorRobot = 0;

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(anchoPantalla);
        gameSettings.setHeight(altoPantalla);
        gameSettings.setTitle("J-Sonic");
        gameSettings.setAppIcon("icono.png");
        gameSettings.addEngineService(MultiplayerService.class);
        gameSettings.setFontUI("SegaSonic.ttf");
        // una tiene que servir
        gameSettings.setFontGame("SegaSonic.ttf");
        gameSettings.setFontText("SegaSonic.ttf");
        //
        gameSettings.setVersion("0.4");
        gameSettings.setMainMenuEnabled(true);
        gameSettings.setEnabledMenuItems(EnumSet.of(MenuItem.EXTRA));
        gameSettings.getCredits().addAll(Arrays.asList(
                "Jose Luis      - Programador en jefe",
                "DemoLinkle   - Programador",
                "Jose Luis      - Artista",
                "Jose Luis      - Disenador de niveles",
                "Jose Luis      - Tester",
                "DemoLinkle   - Tester",
                "Jose Luis      - Productor",
                "Jose Luis      - Director",
                "Rex              - Apoyo moral"
        ));
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GameFactory());
        menuDePersonaje();
        getGameScene().setBackgroundColor(Color.DARKBLUE);
        Music music = getAssetLoader().loadMusic("OST.mp3");
        // Fixme: cambiar la musica, ya no me gusta esta
        getAudioPlayer().loopMusic(music);
    }

    // Todo: puedo pasar esta pantalla a su respectiva clase
    // Todo: quizas hacer que se pueda cambiar de personaje en cualquier momento si no estan los 3
    private void menuDePersonaje() {
        getExecutor().startAsyncFX(() -> {
            Stage stage = new Stage();
            VBox root = new VBox(15);
            root.setAlignment(Pos.CENTER);
            Text titulo = FXGL.getUIFactoryService().newText("Selecciona tu personaje");
            titulo.setFill(Color.BLACK);

            FXGLButton btnSonic = (FXGLButton) FXGL.getUIFactoryService().newButton("Sonic");
            btnSonic.setTextFill(Color.BLUE);
            btnSonic.setOnAction(e -> {
                getWorldProperties().setValue("personaje", "sonic");
                stage.close();
                Jugar();
            });

            FXGLButton btnTails = (FXGLButton) FXGL.getUIFactoryService().newButton("Tails");
            btnTails.setTextFill(Color.YELLOW);
            btnTails.setOnAction(e -> {
                getWorldProperties().setValue("personaje", "tails");
                stage.close();
                Jugar();
            });

            FXGLButton btnKnuckles = (FXGLButton) FXGL.getUIFactoryService().newButton("Knuckles");
            btnKnuckles.setTextFill(Color.RED);
            btnKnuckles.setOnAction(e -> {
                getWorldProperties().setValue("personaje", "knuckles");
                stage.close();
                Jugar();
            });

            root.getChildren().addAll(titulo, btnSonic, btnTails, btnKnuckles);
            root.setBackground(Background.fill(Color.LIGHTGRAY));
            Scene scene = new Scene(root, 400, 200);
            stage.setScene(scene);
            stage.setTitle("Selecciona personaje");
            stage.show();
        });
    }

    private void Jugar() {
        spawn("fondo");
        setLevelFromMap("mapazo.tmx");
        var client = getNetService().newTCPClient("localhost", 55555);
        client.setOnConnected(conn -> {
            conexion = conn;
            MultiplayerLogic.enviarMensaje("Hola", conexion);
            getExecutor().startAsyncFX(() -> onClient());
            System.out.println("Cliente conectado");
        });
        client.connectAsync();
    }

    /**
     * Maneja los mensajes que recibe del servidor.
     */
    private void onClient() {
        conexion.addMessageHandlerFX((conexion, bundle) -> {
            switch (bundle.getName()) {
                // Orden de mensajes
                // 1. El cliente se conecta al servidor
                // 2. El servidor al recibir el mensaje "Hola" del cliente, le responde con "TuID"
                case "TuID": {
                    clientID = bundle.get("id");
                    MultiplayerLogic.solicitarCrearPersonaje(getWorldProperties().getValue("personaje"), clientID, conexion);
                    // Envia posicion inicial para sincronizar servidor
                    MultiplayerLogic.sincronizarPosiciones(clientID, 50, 150, conexion);
                    break;
                }

                case "AnilloRecogido": {
                    String ringId = bundle.get("ringId");
                    getGameWorld().getEntitiesByType(GameFactory.EntityType.RING).stream()
                        .filter(r -> ringId.equals(r.getProperties().getString("id")))
                        .findFirst()
                        .ifPresent(Entity::removeFromWorld);
                    // Actualiza contador si el jugador es uno mismo
                    String quienRecogioAnillo = bundle.get("playerId");
                    if (quienRecogioAnillo.equals(clientID)) {
                        contadorAnillos++;
                        GameLogic.setAnillos("Anillos: " + contadorAnillos);
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

                case "EggmanEliminado": {
                    GameLogic.Ganaste(contadorAnillos, contadorBasura, contadorPapel, contadorCaucho, contadorRobot);
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
                            // TODO: estos contadores puedo hacerlos invisibles en la UI y unicamente usarlos cuando se guarden las estadisticas
                            if (bundle.get("playerId").equals(clientID)) {
                                switch (tipoBasura) {
                                    case "papel":
                                        contadorPapel++;
                                        GameLogic.setPapel("Papel: " + contadorPapel);
                                        break;
                                    case "caucho":
                                        contadorCaucho++;
                                        GameLogic.setCaucho("Caucho: " + contadorCaucho);
                                        break;
                                    case "basura":
                                        contadorBasura++;
                                        GameLogic.setBasura("Basura: " + contadorBasura);
                                        break;
                                }
                            }
                        });
                    break;
                }

                case "Cantidad Basura Total": {
                    int total = bundle.get("total");
                    int restante = bundle.get("restante");

                    GameLogic.agregarBarra((float) restante / total);
                    GameLogic.filtroColor((float) restante / total); // Cambia el tono
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
                    getNotificationService().pushNotification("¡Eggman ha aparecido!");
                    double x = ((Number) bundle.get("x")).doubleValue();
                    double y = ((Number) bundle.get("y")).doubleValue();
                    String id = bundle.get("id");
                    Entity entidad = spawn("eggman", 1500, 10);
                    Player eggman = (Player) entidad;
                    eggman.getProperties().setValue("id", id); // Guarda el id para identificarlo luego
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

                /* Orden de mensajes
                 * 1. El cliente solicita crear un personaje
                 * 2. El servidor responde con "Crear Personaje" y la posicion del personaje
                 * 3. El cliente crea el personaje y lo sincroniza con el servidor
                 *
                 *la variable player es nula hasta este momento */
                case "Crear Personaje": {
                    String id = bundle.get("id");
                    String tipo = bundle.get("tipo");
                    double x = ((Number)bundle.get("x")).doubleValue();
                    double y = ((Number)bundle.get("y")).doubleValue();

                    if (id.equals(clientID)) {
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
                    GameLogic.actualizarUI();
                    break;
                }

                case "CrearEsmeralda": {
                    double x = bundle.get("x");
                    double y = bundle.get("y");
                    String esmeraldaId = bundle.get("esmeraldaId");
                    Entity esmeralda = spawn("esmeralda", x, y);
                    esmeralda.getProperties().setValue("esmeraldaId", esmeraldaId);
                    break;
                }

                case "EliminarEsmeralda": {
                    String esmeraldaID = bundle.get("esmeraldaId");

                    getGameWorld().getEntitiesByType(GameFactory.EntityType.ESMERALDA).stream()
                            .filter(r -> esmeraldaID.equals(r.getProperties().getString("esmeraldaId")))
                            .findFirst()
                            .ifPresent(Entity::removeFromWorld);
                    break;
                }

                case "SyncPos": {
                    String syncId = bundle.get("id");
                    if (syncId.equals(clientID)) {
                        return; // Ignorarte a ti mismo
                    }

                    Player remotePlayer = personajeRemotos.get(syncId);
                    if (remotePlayer != null) {
                        Number xNum = bundle.get("x");
                        Number yNum = bundle.get("y");
                        remotePlayer.setX(xNum.doubleValue());
                        remotePlayer.setY(yNum.doubleValue());
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
                    if (moveId.equals(clientID)) {
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

                case "Desbloquear Transformacion":
                    getWorldProperties().setValue("transformacion", true);
                    break;
            }
        });
    }

    /**
     * Cada frame se envia la posicion del jugador al servidor para sincronizar
     * @param tpf time per frame
     */
    @Override
    protected void onUpdate(double tpf) {
        if (conexion != null && player != null) {
            if (System.currentTimeMillis() % 100 < 16) {
                MultiplayerLogic.sincronizarPosiciones(clientID, player.getX(), player.getY(), conexion);
            }
        }
    }
            
    /**
     * Cuando detecta una tecla presionada, se ejecuta una accion
     */
    @Override
    protected void initInput() {
        // ESTA FEO PERO FUNCIONA LA SINCRONIZACION, si uso los metodos de MultiplayerLogic se pierde la sincronizacion
        getInput().addAction(new UserAction("Mover a la izquierda") {
            @Override
            protected void onAction() {
                if (player == null) return;
                Bundle bundle = new Bundle("Mover a la izquierda");
                bundle.put("id", clientID);
                conexion.send(bundle);
                player.moverIzquierda();
                Bundle sync = new Bundle("SyncPos");
                sync.put("id", clientID);
                sync.put("x", player.getX());
                sync.put("y", player.getY());
                conexion.send(sync);
            }
            @Override
            protected void onActionEnd() {
                if (player == null) return;
                Bundle bundle = new Bundle("Detente");
                bundle.put("id", clientID);
                conexion.send(bundle);
                player.detener();
                Bundle sync = new Bundle("SyncPos");
                sync.put("id", clientID);
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
                bundle.put("id", clientID);
                conexion.send(bundle);
                player.moverDerecha();
                Bundle sync = new Bundle("SyncPos");
                sync.put("id", clientID);
                sync.put("x", player.getX());
                sync.put("y", player.getY());
                conexion.send(sync);
            }
            @Override
            protected void onActionEnd() {
                if (player == null) return;
                Bundle bundle = new Bundle("Detente");
                bundle.put("id", clientID);
                conexion.send(bundle);
                player.detener();
                Bundle sync = new Bundle("SyncPos");
                sync.put("id", clientID);
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
                bundle.put("id", clientID);
                conexion.send(bundle);
                player.saltar();
                Bundle sync = new Bundle("SyncPos");
                sync.put("id", clientID);
                sync.put("x", player.getX());
                sync.put("y", player.getY());
                conexion.send(sync);
            }
        }, KeyCode.W, VirtualButton.A);

        //todo: encontrar algo que hacer
        getInput().addAction(new UserAction("Interactuar") {
            @Override
            protected void onActionBegin() {
                if (player == null) return;
                if (flag_Interactuar) { // Solo interactuar si se ha activado la bandera
                    // enviar mensaje al servidor para interactuar con el entorno
                    MultiplayerLogic.interactuar(clientID, player, conexion);
                }
            }
        }, KeyCode.E);

        getInput().addAction(new UserAction("Transformar") {
            @Override
            protected void onActionBegin() {
                // POV: ojala tuviera las promesas de typescript
                if (player == null) return;
                if (getWorldProperties().getBoolean("transformacion")) {
                    player.transformarSuperSonic();
                }
            }
        }, KeyCode.P);

        getInput().addAction(new UserAction("Desactivar filtro") {
            @Override
            protected void onActionBegin() {
                GameLogic.filtroColor(0);
            }
        }, KeyCode.L);
    }

    /**
     * Inicializa la colision entre entidades.
     */
    @Override
    protected void initPhysics() {

        onCollisionBegin(GameFactory.EntityType.PLAYER, GameFactory.EntityType.RING, (tu, ring) -> {
            play("recoger.wav");
            String ringId = ring.getProperties().getString("id");
            MultiplayerLogic.recogerAnillos(clientID, ringId, conexion);
        });

        onCollisionBegin(GameFactory.EntityType.PLAYER, GameFactory.EntityType.ESMERALDA, (player, esmeralda) -> {
            if (player.hasComponent(SonicComponent.class)){
                play("recoger.wav");
                String esmeraldaId = esmeralda.getProperties().getString("esmeraldaId");
                MultiplayerLogic.recogerEsmeralda(clientID, esmeraldaId, conexion);
            }
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

        onCollisionBegin(GameFactory.EntityType.PLAYER, GameFactory.EntityType.CAUCHO, (player, caucho) -> {
            if (player.hasComponent(KnucklesComponent.class)) {
                recogerBasura((Player)player, caucho);
                flag_Interactuar = true;
            }
        });

       onCollisionBegin(GameFactory.EntityType.PLAYER, GameFactory.EntityType.ROBOT_ENEMIGO, (tu, robot) -> {
            // si eres invencible y no estas transformado no haces nada
            if (((Player) tu).isInvencible() && !((Player) tu).estaTransformado()) {
                return;
            }

            double alturaPlayer = tu.getHeight();
            double alturaRobot = robot.getHeight();

            double bottomPlayer = tu.getY() + alturaPlayer;
            double topRobot = robot.getY();

            boolean golpeDesdeArriba = bottomPlayer <= topRobot + 10;
            // si el golpe es desde arriba o si el jugador esta transformado el enemigo muere
            if (golpeDesdeArriba || ((Player) tu).estaTransformado()) {
               String robotId = robot.getProperties().getString("id");
               Bundle eliminar = new Bundle("EliminarRobot");
               eliminar.put("robotId", robotId);
               eliminar.put("playerId", clientID);
               conexion.send(eliminar);
               tu.getComponent(PhysicsComponent.class).setVelocityY(-300);
               if (((Player) tu).equals(player)) {
                    contadorRobot++;
               }
            } else {
                perderVidas();
            }
       });

        onCollisionBegin(GameFactory.EntityType.PLAYER, GameFactory.EntityType.EGGMAN, (tu, eggman) -> {
            double alturaPlayer = player.getHeight();
            double alturaEggman = eggman.getHeight();

            double bottomPlayer = player.getY() + alturaPlayer;
            double topEggman = eggman.getY();

            boolean golpeDesdeArriba = bottomPlayer <= topEggman + 10;

            if (((Player) eggman).isInvencible() ){
                // no hacer nada :v
            } else if  (golpeDesdeArriba) {
                String eggmanId = eggman.getProperties().getString("id");
                MultiplayerLogic.atacarEggman(eggmanId, conexion);
                GameLogic.activarInvencibilidad(3000, (Player) eggman);
                player.getComponent(PhysicsComponent.class).setVelocityY(-300);
            }
            else {
                perderVidas();
            }
        });
    }

    /**
     * Inicializa las variables del juego.
     * Estas variables estan disponibles cuando se inicia el juego
     */
    @Override
    protected void initGameVars(Map<String, Object> vars) {
        // this creates an observable integer variable with value 3
        //vars.put("lives", 3);
        vars.put("transformacion", false);
        vars.put("personaje", "");
    }

    /**
     * Cuando el jugador recoge basura, se envia un mensaje al servidor para que lo elimine
     * @param player El jugador que recoge la basura
     * @param basuraEntidad La entidad de la basura
     */
    private void recogerBasura(Player player, Entity basuraEntidad) {
        String basuraId = basuraEntidad.getProperties().getString("id");
        String tipo = player.getTipo();
        MultiplayerLogic.recogerBasura(clientID, basuraId, tipo, conexion);
    }

    /**
     * Si el jugador tiene anillos, los pierde y se activa la invencibilidad
     * Si no tiene anillos, pierde una vida y se activa la invencibilidad
     * Si el jugador se queda sin vidas, se termina el juego
     */
    private void perderVidas() {
        if (player.isInvencible()){
            return;
        }

        long ahora = System.currentTimeMillis();

        if (contadorAnillos > 0) {
            play("perder_anillos.wav");
            contadorAnillos = 0;
            GameLogic.setAnillos("Anillos: " + contadorAnillos);
            GameLogic.activarInvencibilidad(3000, player);
            return;
        }

        play("muerte.wav");
        player.restarVida();
        GameLogic.setVidas("Vidas: " + player.getVidas());
        if (player.estaMuerto()) {
            GameLogic.gameOver();
        } else {
            GameLogic.activarInvencibilidad(3000, player);
        }
    }
}