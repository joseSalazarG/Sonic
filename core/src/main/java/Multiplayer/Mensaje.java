package Multiplayer;

public class Mensaje {
    public String mensaje;

    public Mensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Mensaje() {
        // Constructor por defecto necesario para KryoNet
    }
}
