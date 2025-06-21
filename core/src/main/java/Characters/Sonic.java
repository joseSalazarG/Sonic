package Characters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Sonic extends Template {

    public Sonic() {
        setWidth(0.5f);
        setHeight(0.5f);
        MAX_VELOCITY = 25f;
        JUMP_VELOCITY = 45f;
        DAMPING = 0.87f;
        // Initialize animations here if needed
    }

    @Override
    public void create() {
        // load the koala frames, split them, and assign them to Animations
        texture = new Texture("Personajes/playerSonic.png");
        TextureRegion[] regions = TextureRegion.split(texture, 32, 42)[0];
        stand = new Animation<TextureRegion>(0, regions[1]);
        jump = new Animation<TextureRegion>(0, regions[0]);
        walk = new Animation<TextureRegion>(0.15f, regions[0], regions[2]);
        walk.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        // figure out the width and height of the koala for collision
        // detection and rendering by converting a koala frames pixel
        // size into world units (1 unit == 16 pixels)
        setWidth(1 / 16f * regions[0].getRegionWidth());
        setHeight(1 / 16f * regions[0].getRegionHeight());
    }
}
