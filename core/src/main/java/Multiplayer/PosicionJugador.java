package Multiplayer;

// En Multiplayer/PosicionJugador.java
public class PosicionJugador {
    public float x, y;
    public String nombre; // o un ID para identificar al jugador

    public PosicionJugador() {}

    public PosicionJugador(String nombre, float x, float y) {
        this.nombre = nombre;
        this.x = x;
        this.y = y;
    }


}
