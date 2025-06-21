package Multiplayer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import Characters.*;

public class ServerListener implements Listener {

    private final Server server;
    private final DatosJuego datosJuego;

    public ServerListener(Server server, DatosJuego datosJuego) {
        this.server = server;
        this.datosJuego = datosJuego;
    }

    @Override
    public void received (com.esotericsoftware.kryonet.Connection connection, Object object) {
        switch (object.getClass().getSimpleName()) {
            case "Mensaje":
                Mensaje mensaje = (Mensaje) object;
                System.out.println("Mensaje recibido del cliente: " + mensaje.mensaje);
                // Aquí puedes procesar el mensaje recibido del cliente
                break;

            case "Respuesta":
                System.out.println("respuesta recibida: " + object);
                // Aquí puedes procesar el objeto del servidor si es necesario
                break;

            case "PosicionJugador":
                server.sendToAllExceptTCP(connection.getID(), object);

            case "KeepAlive":
                // No es necesario hacer nada, solo se recibe para mantener la conexión
                break;

            case "Tails":
                Tails tails = (Tails) object;
                System.out.println("Tails recibido");
                // Aquí puedes procesar el objeto Tails si es necesario
                break;

            case "Sonic":
                Sonic sonic = (Sonic) object;
                System.out.println("Sonic recibido");
                break;

            case "Knuckles":
                Knuckles knuckles = (Knuckles) object;
                System.out.println("Knuckles recibido");
                // Aquí puedes procesar el objeto Knuckles si es necesario
                break;

            default:
                System.err.println("Objeto desconocido recibido: " + object.getClass().getSimpleName());
                break;
        }
    }

    @Override
    public void disconnected (com.esotericsoftware.kryonet.Connection connection) {
        System.out.println("Cliente desconectado: " + connection.getID());
    }
}
