package Multiplayer;

import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class ServerListener implements Listener {

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

            case "KeepAlive":
                // No es necesario hacer nada, solo se recibe para mantener la conexión
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
