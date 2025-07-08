package component.Personajes;

import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.scene.image.Image;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.image;

public class KnucklesComponent extends PlayerComponent {// Knuckles moves faster than the player

    AnimationChannel golpeando;

    public KnucklesComponent() {
        Image idle = image("Personajes/knuckles.png");
        Image golpe = image("Personajes/knuckles_golpe.png");
        velocidad_lateral_base = 300;
        velocidad_vertical_base = 200;
        //parado = new AnimationChannel(golpe, 10, 41, 112, Duration.seconds(1), 0, 9);
        parado = new AnimationChannel(idle, 11, 34, 44, Duration.seconds(1), 0, 0);
        caminando = new AnimationChannel(idle, 11, 36, 44, Duration.seconds(0.8), 1, 5);
        saltando = new AnimationChannel(idle, 11, 37, 44, Duration.seconds(1), 7, 7);
        golpeando = new AnimationChannel(golpe, 10, 41, 112, Duration.seconds(1), 0, 9);
        texture = new AnimatedTexture(parado);
        texture.loop();
    }

    @Override
    public String getTipo() {
        return "knuckles";
    }

    @Override
    public void onUpdate(double tpf) {
        if (physics.isMovingX() && !physics.isMovingY()) {
            if (texture.getAnimationChannel() != caminando) {
                texture.loopAnimationChannel(caminando);
            }
        } else if (physics.isMovingY()) {
            if (texture.getAnimationChannel() != saltando) {
                texture.loopAnimationChannel(saltando);
            }
        } else {
            if (texture.getAnimationChannel() != parado && texture.getAnimationChannel() != golpeando) {
                texture.loopAnimationChannel(parado);
            }
        }
    }

    public void golpear() {
        if (texture.getAnimationChannel() != saltando) {
            System.out.println("Golpeando");
            texture.playAnimationChannel(golpeando);
            texture.loopAnimationChannel(parado);
        }
    }

    public void interactuar() {
        if (texture.getAnimationChannel() != saltando && texture.getAnimationChannel() != golpeando) {
            golpear();
        }
    }
}

