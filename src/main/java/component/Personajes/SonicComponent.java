package component.Personajes;

import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.scene.image.Image;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.image;

public class SonicComponent extends PlayerComponent {

    public SonicComponent() {
        Image image = image("Personajes/sonic.png");
        // Sonic tiene una velocidad lateral base más alta que el jugador
        velocidad_lateral_base += 50; // se mueve más rápido que el jugador

        // Definición de las animaciones para Sonic
        parado = new AnimationChannel(image, 4, 32, 42, Duration.seconds(1), 1, 1);
        caminando = new AnimationChannel(image, 4, 32, 42, Duration.seconds(0.66), 0, 3);

        texture = new AnimatedTexture(parado);
        texture.loop();
    }

    
}
