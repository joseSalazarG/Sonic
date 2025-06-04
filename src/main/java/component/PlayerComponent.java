package component;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;
import static com.almasb.fxgl.dsl.FXGL.image;

public class PlayerComponent extends Component {

    private PhysicsComponent physics;

    private AnimatedTexture texture;

    private AnimationChannel parado, caminando;

    private int saltosPermitidos = 2;

    public PlayerComponent() {

        Image image = image("player.png");

        parado = new AnimationChannel(image, 4, 32, 42, Duration.seconds(1), 1, 1);
        caminando = new AnimationChannel(image, 4, 32, 42, Duration.seconds(0.66), 0, 3);

        texture = new AnimatedTexture(parado);
        texture.loop();
    }

    @Override
    public void onAdded() {
        entity.getTransformComponent().setScaleOrigin(new Point2D(16, 21));
        entity.getViewComponent().addChild(texture);

        physics.onGroundProperty().addListener((obs, old, tocandoPiso) -> {
            if (tocandoPiso) {
                saltosPermitidos = 2;
            }
        });
    }

    @Override
    public void onUpdate(double tpf) {
        if (physics.isMovingX()) {
            if (texture.getAnimationChannel() != caminando) {
                texture.loopAnimationChannel(caminando);
            }
        } else {
            if (texture.getAnimationChannel() != parado) {
                texture.loopAnimationChannel(parado);
            }
        }
    }

    public void moverIzquierda() {
        getEntity().setScaleX(-1);
        physics.setVelocityX(-400);
    }

    public void moverDerecha() {
        getEntity().setScaleX(1);
        physics.setVelocityX(400);
    }

    public void detener() {
        physics.setVelocityX(0);
    }

    public void saltar() {
        if (saltosPermitidos == 0)
            return;

        physics.setVelocityY(-250);

        saltosPermitidos--;
    }
}
