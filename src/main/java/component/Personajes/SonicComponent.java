package component.Personajes;

import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.scene.image.Image;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.image;
import static com.almasb.fxgl.dsl.FXGL.play;

public class SonicComponent extends PlayerComponent {

    // animacion para Super Sonic
    AnimationChannel transformacion ;
    public static final AnimationChannel NORMAL_IDLE = new AnimationChannel(image("Personajes/sonic.png"), 21, 32, 42, Duration.seconds(4.5), 0, 20);
    public static final AnimationChannel NORMAL_CAMINA = new AnimationChannel(image("Personajes/sonic_camina.png"), 6, 44, 42, Duration.seconds(0.66), 0, 5);
    public static final AnimationChannel NORMAL_SALTO = new AnimationChannel(image("Personajes/sonic_camina.png"), 7, 44, 42, Duration.seconds(0.66), 6, 6);
    public static final AnimationChannel SUPER_IDLE = new AnimationChannel(image("Personajes/super_sonic.png"), 7, 44, 60, Duration.seconds(1), 6, 6);
    public static final AnimationChannel SUPER_VUELO = new AnimationChannel(image("Personajes/super_sonic.png"), 8, 44, 60, Duration.seconds(0.66), 7, 7);

    public SonicComponent() {
        velocidad_lateral_base = 400;
        velocidad_vertical_base = 240;
        // animaciones para Sonic
        parado = NORMAL_IDLE;
        caminando = NORMAL_CAMINA;
        saltando = NORMAL_SALTO;
        // animacion para Super Sonic
        //transformacion = new AnimationChannel(superSonicImage, 4, 44 , 60, Duration.seconds(1), 0, 5);
        texture = new AnimatedTexture(parado);
        texture.loop();
    }

    @Override
    public String getTipo() {
        return "sonic";
    }

    public void transformarSuperSonic() {
        /*
        texture.playAnimationChannel(transformacion);
        texture.setOnCycleFinished(() -> {
            texture.loopAnimationChannel(superIdle);
        }); */
        parado = SUPER_IDLE;
        caminando = SUPER_VUELO;
        saltando = SUPER_VUELO;
    }

    public void destransformar() {
        parado = NORMAL_IDLE;
        caminando = NORMAL_CAMINA;
        saltando = NORMAL_SALTO;
    }

    @Override
    public void saltar() {
        super.saltar();
        play("salto.wav");
    }

}
