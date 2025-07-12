package component.Personajes;

import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.scene.image.Image;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.image;

public class SonicComponent extends PlayerComponent {

    // animacion para Super Sonic
    AnimationChannel transformacion ;
    AnimationChannel superIdle;
    AnimationChannel superVuelo;
    //

    public SonicComponent() {
        Image idle = image("Personajes/sonic.png");
        Image superSonicImage = image("Personajes/super_sonic.png");
        Image camina = image("Personajes/sonic_camina.png");
        velocidad_lateral_base = 400;
        velocidad_vertical_base = 240;
        // animaciones para Sonic
        parado = new AnimationChannel(idle, 4, 32, 42, Duration.seconds(1), 1, 1);
        caminando = new AnimationChannel(camina, 6, 44, 42, Duration.seconds(0.66), 0, 5);
        saltando = new AnimationChannel(camina, 7, 44, 42, Duration.seconds(1), 6, 6);
        // animacion para Super Sonic
        transformacion = new AnimationChannel(superSonicImage, 4, 44 , 60, Duration.seconds(1), 0, 5);
        superIdle = new AnimationChannel(superSonicImage, 7, 44, 60, Duration.seconds(1), 6, 6);
        superVuelo = new AnimationChannel(superSonicImage, 8, 44, 60, Duration.seconds(0.66), 7, 7);
        //
        texture = new AnimatedTexture(parado);
        texture.loop();
    }

    @Override
    public String getTipo() {
        return "sonic";
    }

    public void transformarSuperSonic() {
        texture.playAnimationChannel(transformacion);
        texture.setOnCycleFinished(() -> {
            texture.loopAnimationChannel(superIdle);
        });
    }
}
