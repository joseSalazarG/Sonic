package component.Personajes;

import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.scene.image.Image;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.image;

public class KnucklesComponent extends PlayerComponent {// Knuckles moves faster than the player

    public KnucklesComponent() {
        Image image = image("Personajes/knuckles.png");

        velocidad_lateral_base -= 100; // se mueve ligeramenta mas lento que el jugador
        velocidad_vertical_base -= 50; // es mas pesado que el jugador

        parado = new AnimationChannel(image, 4, 32, 42, Duration.seconds(1), 1, 1);
        caminando = new AnimationChannel(image, 4, 32, 42, Duration.seconds(0.66), 0, 3);

        texture = new AnimatedTexture(parado);
        texture.loop();
    }
}

