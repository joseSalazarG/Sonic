package component.Personajes;

import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.scene.image.Image;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.image;

public class TailsComponent extends PlayerComponent {

    public TailsComponent() {
        Image image = image("Personajes/tails.png");

        MAX_SALTOS = 300;
        velocidad_lateral_base -= 50; // se mueve ligeramenta mas lento que el jugador

        parado = new AnimationChannel(image, 4, 32, 42, Duration.seconds(1), 1, 1);
        caminando = new AnimationChannel(image, 4, 32, 42, Duration.seconds(0.66), 0, 3);

        texture = new AnimatedTexture(parado);
        texture.loop();
    }
    
}
