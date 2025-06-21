package Multiplayer;

import java.util.HashMap;
import java.util.Map;

public class DatosJuego {

    private final Map<String, PosicionJugador> posiciones = new HashMap<>();

    public synchronized void actualizarPosicion(String nombre, float x, float y) {
        posiciones.put(nombre, new PosicionJugador(nombre, x, y));
    }

    public synchronized PosicionJugador getPosicion(String nombre) {
        return posiciones.get(nombre);
    }

    public synchronized Map<String, PosicionJugador> getTodasLasPosiciones() {
        return new HashMap<>(posiciones);
    }
}
