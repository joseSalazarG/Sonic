package component.Personajes;

import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.scene.image.Image;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.image;

public class TailsComponent extends PlayerComponent {

    public TailsComponent() {
        Image idle = image("Personajes/tails_Idle.png");
        Image salta_anim = image("Personajes/tails_Salta.png");
        MAX_SALTOS = 300;
        velocidad_lateral_base = 300; // se mueve ligeramenta mas lento que el jugador
        velocidad_vertical_base = 200; // es mas pesado que el jugador
        parado = new AnimationChannel(idle, 18, 41, 42, Duration.seconds(4), 0, 17);
        caminando = new AnimationChannel(idle, 4, 32, 42, Duration.seconds(0.66), 0, 3);
        saltando = new AnimationChannel(salta_anim, 2, 41, 42, Duration.seconds(0.66), 0, 1);
        texture = new AnimatedTexture(parado);
        texture.loop();
    }

    @Override
    public String getTipo() {
        return "tails";
    }
}
