package component;

import GameSettings.Player;
import com.almasb.fxgl.core.collection.PropertyMap;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Connection;

import java.io.Serializable;

/**.
 * Todo los metodos para enviar mensajes se encuentran aquí.
 */
public class MultiplayerLogic implements Serializable {


    public static void enviarMensaje(String titulo, Connection<Bundle> conexion) {
        Bundle bundle = new Bundle(titulo);
        conexion.send(bundle);
    }

    public static void solicitarCrearPersonaje(String personajeSeleccionado, String clientID, Connection<Bundle> conexion) {
        Bundle solicitar = new Bundle("SolicitarCrearPersonaje");
        solicitar.put("id", clientID);
        solicitar.put("tipo", personajeSeleccionado);
        conexion.send(solicitar);
    }

    public static void moverIzquierda(String clientID, Player player, Connection<Bundle> conexion) {
        Bundle bundle = new Bundle("Mover a la izquierda");
        bundle.put("id", clientID);
        conexion.send(bundle);
        player.moverIzquierda();
        sincronizarPosiciones(clientID, player.getX(), player.getY(), conexion);
    }

    public static void moverDerecha(String clientID, Player player, Connection<Bundle> conexion) {
        Bundle bundle = new Bundle("Mover a la derecha");
        bundle.put("id", clientID);
        conexion.send(bundle);
        player.moverDerecha();
        sincronizarPosiciones(clientID, player.getX(), player.getY(), conexion);
    }

    public static void detenerMovimiento(String clientID, Player player, Connection<Bundle> conexion) {
        Bundle bundle = new Bundle("Detener movimiento");
        bundle.put("id", clientID);
        conexion.send(bundle);
        player.detener();
        sincronizarPosiciones(clientID, player.getX(), player.getY(), conexion);
    }

    public static void saltar(String clientID, Player player, Connection<Bundle> conexion) {
        Bundle bundle = new Bundle("Saltar");
        bundle.put("id", clientID);
        conexion.send(bundle);
        player.saltar();
        sincronizarPosiciones(clientID, player.getX(), player.getY(), conexion);
    }

    public static void interactuar(String clientID, Player player, Connection<Bundle> conexion) {
        Bundle bundle = new Bundle("Interactuar");
        bundle.put("id", clientID);
        conexion.send(bundle);
        player.interactuar();
    }

    //Todo: puedes cambiar el nombre de este metodo a sincronizar
    public static void sincronizarPosiciones(String clientID, double x, double y, Connection<Bundle> conexion) {
        Bundle bundle = new Bundle("SyncPos");
        bundle.put("id", clientID);
        bundle.put("x", x);
        bundle.put("y", y);
        conexion.send(bundle);
    }

    public static void atacarEggman(String eggmanId, Connection<Bundle> conexion) {
        Bundle atacarEggman = new Bundle("atacarEggman");
        atacarEggman.put("eggmanId", eggmanId);
        conexion.send(atacarEggman);
    }

    public static void recogerBasura(String clientID, String trashId, String tipo_de_basura, Connection<Bundle> conexion) {
        Bundle recoger = new Bundle("RecogerBasura");
        recoger.put("trashId", trashId);
        recoger.put("playerId", clientID);
        recoger.put("tipo", tipo_de_basura);
        conexion.send(recoger);
    }

    public static void recogerAnillos(String clientID, String ringId, Connection<Bundle> conexion) {
        Bundle recoger = new Bundle("RecogerAnillo");
        recoger.put("ringId", ringId);
        recoger.put("playerId", clientID);
        conexion.send(recoger);
    }

    public static void recogerEsmeralda(String clientID, String esmeraldaID, Connection<Bundle> conexion) {
        Bundle recoger = new Bundle("RecogerEsmeralda");
        recoger.put("esmeraldaId", esmeraldaID);
        recoger.put("playerId", clientID);
        conexion.send(recoger);
    }

    public static void solicitarVariables(Connection<Bundle> conexion) {
        enviarMensaje("SolicitandoVariables", conexion);
    }

}
