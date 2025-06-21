package Multiplayer;

import com.esotericsoftware.kryonet.Listener;

public class ClientListener implements Listener {

    @Override
    public void received (com.esotericsoftware.kryonet.Connection connection, Object object) {
        switch (object.getClass().getSimpleName()) {
            case "Mensaje":
                Mensaje mensaje = (Mensaje) object;
                System.out.println("Mensaje recibido del servidor: " + mensaje.mensaje);
                // Aquí puedes procesar el mensaje recibido del servidor
                break;

            case "Respuesta":
                System.out.println("Respuesta recibida: " + object);
                // Aquí puedes procesar el objeto del cliente si es necesario
                break;

            case "PosicionJugador":
                PosicionJugador pos = (PosicionJugador) object;
                // Actualiza la posición del jugador 2 en la pantalla del jugador 1
                //jugador2.position.set(pos.x, pos.y);

            case "KeepAlive":
                // No es necesario hacer nada, solo se recibe para mantener la conexión
                break;

            default:
                System.err.println("Objeto desconocido recibido: " + object.getClass().getSimpleName());
                break;
        }
    }
}
