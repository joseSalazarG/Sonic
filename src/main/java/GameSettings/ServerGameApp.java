package GameSettings;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.multiplayer.MultiplayerService;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.app.GameApplication;

import static GameSettings.Entities.posicionesBasura;
import static GameSettings.Entities.posicionesRings;
import static GameSettings.Entities.posicionesRobots;
import static com.almasb.fxgl.dsl.FXGL.*;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.net.Connection;
import component.GameFactory;
import component.Personajes.PlayerComponent;
import component.Personajes.SonicComponent;
import component.GameLogic;
import component.Enemigos.EggmanComponent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ServerGameApp extends GameApplication implements Serializable{
    private final int anchoPantalla = 800;
    private final int altoPantalla = 500;
    private Connection<Bundle> conexion;
    private List<Connection> conexiones = new ArrayList<>();
    private List<Bundle> personajesExistentes = new ArrayList<>();
    private com.almasb.fxgl.net.Server<Bundle> server;
    private Map<String, Entity> anillos = new HashMap<>();
    private Map<String, Entity> basuras = new HashMap<>();
    private Map<String, Entity> robots = new HashMap<>();
    private Map<String, Entity> eggmanBoss = new HashMap<>();
    private Player player;
    private int totalBasura = 0;
    private Set<Integer> eventosDisparados = new HashSet<>();
    

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(anchoPantalla);
        gameSettings.setHeight(altoPantalla);
        gameSettings.setTitle("Server");
        gameSettings.addEngineService(MultiplayerService.class);
    }

   @Override
    protected void initGame() {
        server = getNetService().newTCPServer(55555);
        server.setOnConnected(conn -> {
            // Genera un id unico para el nuevo cliente
            String nuevoId = UUID.randomUUID().toString();
            Bundle tuId = new Bundle("TuID");
            tuId.put("id", nuevoId);
            conn.send(tuId);

            conexiones.add(conn); // Agrega la conexion a la lista

            getExecutor().startAsyncFX(() -> onServer(conn));
        });
        System.out.println("Servidor creado");
        server.startAsync();
        Jugar();
        
    }

      private void Jugar(){
        getGameWorld().addEntityFactory(new GameFactory());
        player = null;
        Level level = setLevelFromMap("mapazo.tmx");
       

        // Spawnea todos los anillos en el servidor
        for (int[] pos : posicionesRings) {
            String ringId = UUID.randomUUID().toString();
            Entity ring = spawn("ring", pos[0], pos[1]);
            ring.getProperties().setValue("id", ringId); // Guarda el id en las propiedades
            anillos.put(ringId, ring);
        }

        for (int[] pos : posicionesRobots) {
            String robotId = UUID.randomUUID().toString();
            Entity robot = spawn("robotEnemigo", pos[0], pos[1]);
            robot.getProperties().setValue("id", robotId);
            robots.put(robotId, robot);
        }
        

        String[] tiposDeBasura = { "basura", "papel", "caucho" };

        for (int[] pos : posicionesBasura) {
            String tipo = tiposDeBasura[(int)(Math.random() * tiposDeBasura.length)];
            String id = UUID.randomUUID().toString();

            Entity basuraEntidad = spawn(tipo, pos[0], pos[1]);
            basuraEntidad.getProperties().setValue("id", id);
            basuraEntidad.getProperties().setValue("tipo", tipo); // Guardamos el tipo

            basuras.put(id, basuraEntidad);
        }
        totalBasura = basuras.size();

    }

    private void verificarEventoBasura() {
        int cantidadRestante = basuras.size();

        if (cantidadRestante <= 7 && !eventosDisparados.contains(7)) {
            eventosDisparados.add(7);

            System.out.println("arbol hizo spawn");

            Entity arbol = spawn("arbol", 400, 1200); 

            Bundle spawnArbol = new Bundle("SpawnArbol");
            spawnArbol.put("x", arbol.getX());
            spawnArbol.put("y", arbol.getY());
            server.broadcast(spawnArbol);
        }
        else if (cantidadRestante <= 6 && !eventosDisparados.contains(6)) {
            eventosDisparados.add(6);

            System.out.println("eggman hizo spawn");
            String eggmanId = UUID.randomUUID().toString();
            Entity eggman = spawn("eggman", 1330, 340);
            eggman.getProperties().setValue("id", eggmanId);
            eggmanBoss.put(eggmanId, eggman);

            Bundle crearEggman = new Bundle("CrearEggman");
            crearEggman.put("x", eggman.getX());
            crearEggman.put("y", eggman.getY());
            crearEggman.put("id", eggmanId);
            server.broadcast(crearEggman);
        }
    }

    public void onServer(Connection<Bundle> connection) {
        connection.addMessageHandlerFX((conn, bundle) -> {
            switch (bundle.getName()) {
                case "Mover a la izquierda":
                case "Mover a la derecha":
                case "Saltar":
                case "Detente":
                    for (Connection<Bundle> c : conexiones) {
                        if (c != conn) {
                            c.send(bundle);
                        }
                    }
                    break;

                case "SyncPos":
                    String syncId = bundle.get("id");
                    for (Bundle personaje : personajesExistentes) {
                        Object personajeId = personaje.get("id");
                        if (syncId.equals(personajeId)) {
                            personaje.put("x", bundle.get("x"));
                            personaje.put("y", bundle.get("y"));
                            break;
                        }
                    }
                    for (Connection<Bundle> c : conexiones) {
                        if (c != conn) {
                            c.send(bundle);
                        }
                    }
                    break;

                case "SolicitarCrearPersonaje": {
                    String id = bundle.get("id");
                    String tipo = bundle.get("tipo");

                    Bundle personajeExistente = personajesExistentes.stream()
                        .filter(p -> p.get("id").equals(id))
                        .findFirst()
                        .orElse(null);

                    double x = 50, y = 150;
                    if (personajeExistente == null) {
                        System.out.println("VERIFICACION!!!!!");
                        Bundle personaje = new Bundle("Crear Personaje");
                        personaje.put("id", id);
                        personaje.put("tipo", tipo);
                        personaje.put("x", x);
                        personaje.put("y", y);
                        personajesExistentes.add(personaje);
                        server.broadcast(personaje);
                    } else {
                        System.out.println("EXISTENTE!!!");
                        server.broadcast(personajeExistente);
                    }
                    break;
                }

                case "Interactuar": {
                    String id = bundle.get("id");
                    String tipo = bundle.get("tipo");
                }
                
                case "Hola": //NO LO BORRES :P
                    System.out.println("sonic se conecto");
                    verificarEventoBasura();

                    for (Bundle personaje : personajesExistentes) {
                        Bundle copia = new Bundle("Crear Personaje");
                        copia.put("id", personaje.get("id"));
                        copia.put("tipo", personaje.get("tipo"));
                        copia.put("x", personaje.get("x"));
                        copia.put("y", personaje.get("y"));
                        //System.out.println("Enviando personaje " + copia.get("id") + " en (" + copia.get("x") + "," + copia.get("y") + ")");
                        conn.send(copia);
                    }

                    // Envia todos los anillos a este cliente
                    for (Map.Entry<String, Entity> entry : anillos.entrySet()) {
                        String id = entry.getKey();
                        Entity ring = entry.getValue();
                        System.out.println("Enviando anillo)");
                        Bundle crearRing = new Bundle("crearRing");
                        crearRing.put("x", ring.getX());
                        crearRing.put("y", ring.getY());
                        crearRing.put("id", id);  // Enviar ID al cliente
                        conn.send(crearRing);
                    }
                    
                    // Envia todas las basuras al cliente
                    for (Map.Entry<String, Entity> entry : basuras.entrySet()) {
                        String id = entry.getKey();
                        Entity basuraEntidad = entry.getValue();
                        String tipo = basuraEntidad.getProperties().getString("tipo");

                        Bundle crear = new Bundle("crearbasura");  // Un solo tipo de mensaje, tenia pensando enviarle con un + tipo, pero es mejor un solo case
                        crear.put("x", basuraEntidad.getX());
                        crear.put("y", basuraEntidad.getY());
                        crear.put("id", id);
                        crear.put("tipo", tipo);  // Esto es para que sepa que tipo es
                        conn.send(crear);
                    }

                    // Envia todos los robots al cliente
                    for (Map.Entry<String, Entity> entry : robots.entrySet()) {
                        String id = entry.getKey();
                        Entity robot = entry.getValue();
                        System.out.println("Enviando robot)");
                        Bundle crearRobot = new Bundle("CrearRobotEnemigo");
                        crearRobot.put("x", robot.getX());
                        crearRobot.put("y", robot.getY());
                        crearRobot.put("id", id);
                        conn.send(crearRobot);
                    }

                    Bundle estadoBasura = new Bundle("EstadoBasuraGlobal");
                    estadoBasura.put("total", totalBasura);
                    estadoBasura.put("restante", basuras.size());
                    conn.send(estadoBasura);
                    break;

                case "RecogerAnillo": {
                    String playerId = bundle.get("playerId");
                    String ringId = bundle.get("ringId");

                    // Elimina el anillo en el servidor
                    Entity ring = anillos.get(ringId);
                    if (ring != null) {
                        ring.removeFromWorld();
                        anillos.remove(ringId);
                    }

                    // Notifica a todos los clientes
                    Bundle anilloRecogido = new Bundle("AnilloRecogido");
                    anilloRecogido.put("playerId", playerId);
                    anilloRecogido.put("ringId", ringId);
                    server.broadcast(anilloRecogido);
                    break;
                }

                case "EliminarRobot": {
                    String playerId = bundle.get("playerId");
                    String robotId = bundle.get("robotId");

                    // Elimina el robot en el servidor
                    Entity robot = robots.get(robotId);
                    if (robot != null) {
                        robot.removeFromWorld();
                        robots.remove(robotId);
                    }

                    // Notifica a todos los clientes
                    Bundle robotEliminado = new Bundle("RobotEliminado");
                    robotEliminado.put("playerId", playerId);
                    robotEliminado.put("robotId", robotId);
                    server.broadcast(robotEliminado);
                    break;
                }

                case "DañoEggman": {
                    String eggmanId = bundle.get("eggmanId");
                    String playerId = bundle.get("playerId");

                    Entity eggman = eggmanBoss.get(eggmanId);
                    if (eggman != null) {
                        EggmanComponent egg = eggman.getComponent(EggmanComponent.class);
                        egg.restarVida();
                        if (egg.estaMuerto()){
                            Bundle eggmanEliminado = new Bundle("EggmanEliminado");
                            eggmanEliminado.put("playerId", playerId);
                            eggmanEliminado.put("eggmanId", eggmanId);
                            server.broadcast(eggmanEliminado);
                        }
                    }
                    
                    break;
                }

                case "RecogerBasura": {
                    String playerId = bundle.get("playerId");
                    String trashId = bundle.get("trashId");
                    String tipoJugador = bundle.get("tipo");  // aseguramos minúsculas

                    Entity trash = basuras.get(trashId);
                    if (trash != null) {
                        String tipoBasura = trash.getProperties().getString("tipo").toLowerCase();

                        System.out.println("Intentando recoger basura. Jugador tipo: " + tipoJugador + ", Basura tipo: " + tipoBasura);
                        boolean puedeRecoger =
                            tipoBasura.equals("basura") ||  // todos pueden recoger basura normal
                            (tipoBasura.equals("caucho") && tipoJugador.equals("knuckles")) ||
                            (tipoBasura.equals("papel") && tipoJugador.equals("tails"));

                        if (!puedeRecoger) {
                            System.out.println("No puede recoger basura: jugador " + tipoJugador + " con basura " + tipoBasura);
                            break; 
                        }
                        trash.removeFromWorld();
                        basuras.remove(trashId);
                        verificarEventoBasura();

                        Bundle estadoActualizado = new Bundle("EstadoBasuraGlobal");
                        estadoActualizado.put("total", totalBasura);
                        estadoActualizado.put("restante", basuras.size());
                        server.broadcast(estadoActualizado);

                        Bundle basuraRecogida = new Bundle("BasuraRecogida");
                        basuraRecogida.put("playerId", playerId);
                        basuraRecogida.put("trashId", trashId);
                        server.broadcast(basuraRecogida);
                    } else {
                        System.out.println("Basura con id " + trashId + " no encontrada.");
                    }

                    break;
                }
            }
        });
    }

    @Override
    protected void onUpdate(double tpf) {
        super.onUpdate(tpf);

        var robots = getGameWorld().getEntitiesByType(GameFactory.EntityType.ROBOT_ENEMIGO);
        if (!robots.isEmpty()) {
            Entity robot = robots.get(0);
            Bundle posRobot = new Bundle("SyncRobotPos");
            posRobot.put("x", robot.getX());
            posRobot.put("y", robot.getY());
            server.broadcast(posRobot);
        }

        var eggmans = getGameWorld().getEntitiesByType(GameFactory.EntityType.EGGMAN); // definilo en tu GameFactory
        if (!eggmans.isEmpty()) {
            Entity eggman = eggmans.get(0);
            Bundle posEggman = new Bundle("SyncEggmanPos");
            posEggman.put("x", eggman.getX());
            posEggman.put("y", eggman.getY());
            server.broadcast(posEggman);
        }
    }
}