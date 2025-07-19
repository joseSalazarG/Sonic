package component.Items;

import static com.almasb.fxgl.dsl.FXGL.*;

import com.almasb.fxgl.entity.component.Component;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PapelComponent extends Component {

    @Override
    public void onAdded() {
        Image image = image("Items/papel.png");
        ImageView view = new ImageView(image);
        view.setFitWidth(21);   // Ajusta el tamaño según tu sprite
        view.setFitHeight(21);
        entity.getViewComponent().addChild(view);
    }
}