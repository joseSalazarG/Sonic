package component.Enemigos;

import static com.almasb.fxgl.dsl.FXGL.*;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.physics.PhysicsComponent;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

@Required(PhysicsComponent.class)

public class EggmanComponent extends Component {

    private PhysicsComponent physics;
    private double velocidad = 50;
    private double startX;
    private double distancia = 300;
    private int direccion = 1;
    private int vidas = 10;

    @Override
    public void onAdded() {
        startX = entity.getX();
        Image image = image("Enemigos/eggman.png");
        ImageView view = new ImageView(image);
        view.setFitWidth(100);
        view.setFitHeight(100);
        entity.getViewComponent().addChild(view);
        physics = entity.getComponent(PhysicsComponent.class);
    }

    @Override
    public void onUpdate(double tpf) {
        physics.setVelocityX(velocidad * direccion);

        double desplazamiento = Math.abs(entity.getX() - startX);
        if (desplazamiento >= distancia) {
            direccion *= -1;
            startX = entity.getX(); // Reinicia el punto de referencia
        }
    }

    public int getVidas() { 
        return vidas; 
    }

    public void restarVida() {
        vidas--; 
    }

    public boolean estaMuerto() {
        return vidas <= 0; 
    }
}

