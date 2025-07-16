package component.Personajes;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGL.image;
import static com.almasb.fxgl.dsl.FXGL.play;

public abstract class PlayerComponent extends Component {

    protected PhysicsComponent fisicas;
    protected AnimatedTexture texture;
    protected AnimationChannel parado, caminando, saltando;
    protected int saltosPermitidos = 2;
    protected int velocidad_lateral_base; // Default horizontal speed
    protected int velocidad_vertical_base;
    static int MAX_SALTOS = 2;

    public abstract String getTipo();

    @Override
    public void onAdded() {
        entity.getTransformComponent().setScaleOrigin(new Point2D(16, 21));
        entity.getViewComponent().addChild(texture);

        fisicas.onGroundProperty().addListener((obs, old, tocandoPiso) -> {
            if (tocandoPiso) {
                saltosPermitidos = MAX_SALTOS;
            }
        });
    }

    @Override
    public void onUpdate(double tpf) {
        if (fisicas.isMovingX() && !fisicas.isMovingY()) {
            if (texture.getAnimationChannel() != caminando) {
                texture.loopAnimationChannel(caminando);
            }
        } else if (fisicas.isMovingY()) {
            if (texture.getAnimationChannel() != saltando) {
                texture.loopAnimationChannel(saltando);
            }
        } else {
            if (texture.getAnimationChannel() != parado) {
                texture.loopAnimationChannel(parado);
            }
        }
    }

    public void moverIzquierda() {
        getEntity().setScaleX(-1);
        fisicas.setVelocityX(-velocidad_lateral_base);
    }

    public void moverDerecha() {
        getEntity().setScaleX(1);
        fisicas.setVelocityX(velocidad_lateral_base);
    }

    public void detener() {
        fisicas.setVelocityX(0);
    }

    public void saltar() {
        if (saltosPermitidos == 0)
            return;

        fisicas.setVelocityY(-velocidad_vertical_base); // negativo para ir hacia arriba

        saltosPermitidos--;
    }
}
