package component.Items;

import static com.almasb.fxgl.dsl.FXGL.*;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EsmeraldaComponent extends Component {

    // Lista de colores de las esmeraldas
     static final ArrayList<String> colores = new ArrayList<>(Arrays.asList("amarilla", "azul", "cyan", "gris", "morada", "roja", "verde"));

    @Override
    public void onAdded() {
        String color = colores.get(random(0, colores.size() - 1));
        colores.remove(color);
        Image image = image("Items/" + color + ".png");
        ImageView view = new ImageView(image);
        view.setFitWidth(18); // se esta escalando al doble del original
        view.setFitHeight(14);
        entity.getViewComponent().addChild(view);
    }
    
}
