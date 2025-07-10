package component.Personajes;

import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.scene.image.Image;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.image;

public class TailsComponent extends PlayerComponent {

    private int vidas = 3;

    public TailsComponent() {
        Image idle = image("Personajes/tails.png");
        Image salta_anim = image("Personajes/tails_Salta.png");
        Image camina_anim = image("Personajes/tails_caminando.png");
        MAX_SALTOS = 300;
        velocidad_lateral_base = 300; // se mueve ligeramenta mas lento que el jugador
        velocidad_vertical_base = 200; // es mas pesado que el jugador
        parado = new AnimationChannel(idle, 22, 41, 42, Duration.seconds(4), 0, 21);
        caminando = new AnimationChannel(camina_anim, 8, 49, 44, Duration.seconds(0.8), 0, 7);
        saltando = new AnimationChannel(salta_anim, 2, 41, 44, Duration.seconds(0.66), 0, 1);
        texture = new AnimatedTexture(parado);
        texture.loop();
    }

    @Override
    public String getTipo() {
        return "tails";
    }

    public int getVidas() { 
        return vidas; 
    }

    public void restarVida() {
        vidas--; 
    }

    public boolean estaMuerto() {
        return vidas <= 0; 
    }
}
