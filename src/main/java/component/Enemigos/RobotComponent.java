package component.Enemigos;

import static com.almasb.fxgl.dsl.FXGL.*;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
public class RobotComponent extends Component {

    private double velocidad = 50;
    private PhysicsComponent fisica;
    private double startX;
    private double movimiento = 0;
    private int direccion = 1; 
    private double distancia = 100;

    @Override
    public void onAdded() {
        Image image = image("Enemigos/enemigo.png");
        ImageView view = new ImageView(image);
        view.setFitWidth(64);
        view.setFitHeight(64);
        entity.getViewComponent().addChild(view);

        fisica = entity.getComponent(PhysicsComponent.class);
        startX = entity.getX();
    }

    @Override
    public void onUpdate(double tpf) {
        fisica.setVelocityX(velocidad * direccion);
        movimiento += Math.abs(entity.getX() - startX - movimiento * direccion);

        if (movimiento >= distancia) {
            direccion *= -1;
            movimiento = 0;
            startX = entity.getX();
        }
    }
}