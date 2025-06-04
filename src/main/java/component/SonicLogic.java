package component;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.net.Connection;

import java.io.Serializable;

public class SonicLogic extends Component implements Serializable {

    public static void enviarMensaje(String titulo, Connection<Bundle> conexion) {
        Bundle bundle = new Bundle(titulo);
        conexion.send(bundle);
    }

    public static void imprimir(String titulo) {
        System.out.println(titulo);
    }
}
