package Multiplayer;

import Characters.Template;
import com.badlogic.gdx.graphics.Color;
import com.esotericsoftware.kryonet.Connection;
import global.PlayerAddEvent;
import global.PlayerRemoveEvent;
import global.PlayerTransferEvent;
//import de.saaslon.server.ServerFoundation;

import java.util.LinkedList;

public class PlayerHandler {

    public static final PlayerHandler INSTANCE = new PlayerHandler();

    private final LinkedList<Template> players;

    public PlayerHandler() {
        this.players = new LinkedList<>();
    }

    public Template getPlayerByConnection(final Connection connection) {
        for(final Template serverPlayer : this.players) {
            //if(serverPlayer.getConnection() == connection) {
              //  return serverPlayer;
            //}
        }

        return null;
    }

    public Template getPlayerByUsername(final String username) {
        for(final Template serverPlayer : this.players) {
            //if(serverPlayer.getUsername().equals(username)) {
            //    return serverPlayer;
            //}
        }

        return null;
    }

    public void update() {
        for(int i = 0; i < this.players.size(); i++) {
            //this.players.get(i).update();
        }
    }

    public void addPlayer(final Template serverPlayer) {
        for(Template all : this.players) {
            final PlayerAddEvent playerAddEvent = new PlayerAddEvent();
            //playerAddEvent.username = all.getUsername();
            playerAddEvent.x = all.getX();
            playerAddEvent.y = all.getY();

            //serverPlayer.getConnection().sendTCP(playerAddEvent);
        }

        final PlayerAddEvent playerAddEvent = new PlayerAddEvent();
        //playerAddEvent.username = serverPlayer.getUsername();
        playerAddEvent.x = serverPlayer.getX();
        playerAddEvent.y = serverPlayer.getY();

        //ServerFoundation.instance.getServer().sendToAllTCP(playerAddEvent);

        this.players.add(serverPlayer);
    }

    public void removePlayer(final Template serverPlayer) {
        this.players.remove(serverPlayer);

        final PlayerRemoveEvent playerRemoveEvent = new PlayerRemoveEvent();
        //playerRemoveEvent.username = serverPlayer.getUsername();

        //ServerFoundation.instance.getServer().sendToAllTCP(playerRemoveEvent);
    }

    public LinkedList<Template> getPlayers() {
        return players;
    }
}
