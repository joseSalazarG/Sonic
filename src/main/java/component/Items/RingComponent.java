package component.Items;

import static com.almasb.fxgl.dsl.FXGL.*;

import com.almasb.fxgl.entity.component.Component;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class RingComponent extends Component {

    @Override
    public void onAdded() {
        Image image = image("Items/ring.png");
        ImageView view = new ImageView(image);
        view.setFitWidth(24);   // Ajusta el tamaño según tu sprite
        view.setFitHeight(24);
        entity.getViewComponent().addChild(view);
    }
}