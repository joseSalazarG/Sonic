package Characters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Tails extends Template {

    Animation<TextureRegion> fly;

    enum State {
        Standing, Walking, Jumping, Flying
    }

    public Tails() {
        setWidth(0.5f);
        setHeight(0.5f);
        MAX_VELOCITY = 10f;
        JUMP_VELOCITY = 40f;
        DAMPING = 0.90f;

        // Initialize animations here if needed
    }

    @Override
    public void create() {
        // load the koala frames, split them, and assign them to Animations
        texture = new Texture("Personajes/tails.png");
        TextureRegion[] regions = TextureRegion.split(texture, 32, 42)[0];
        stand = new Animation<TextureRegion>(0, regions[1]);
        jump = new Animation<TextureRegion>(0, regions[0]);
        walk = new Animation<TextureRegion>(0.15f, regions[0], regions[2]);
        walk.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        fly = new Animation<TextureRegion>(0.1f, regions[3]);
        // figure out the width and height of the koala for collision
        // detection and rendering by converting a koala frames pixel
        // size into world units (1 unit == 16 pixels)
        setWidth(1 / 16f * regions[0].getRegionWidth());
        setHeight(1 / 16f * regions[0].getRegionHeight());
    }

    @Override
    public void jump() {
        velocity.y += Template.JUMP_VELOCITY;
        state = Template.State.Jumping;
        grounded = false;
        // Add logic for flying if needed
        if (state == Template.State.Jumping) {
            // Example: start flying animation
            // setAnimation(fly);

        }
    }

    @Override
    public void render(float deltaTime, Batch batch) {
        // based on the koala state, get the animation frame
        TextureRegion frame = switch (state) {
            case Standing -> stand.getKeyFrame(stateTime);
            case Walking -> walk.getKeyFrame(stateTime);
            case Jumping -> jump.getKeyFrame(stateTime);
            case Flying -> fly.getKeyFrame(stateTime);
        };

        // draw the koala, depending on the current velocity
        // on the x-axis, draw the koala facing either right
        // or left
        batch.begin();
        if (facesRight) {
            batch.draw(frame, position.x, position.y, Template.WIDTH, Template.HEIGHT);
        } else {
            batch.draw(frame, position.x + Template.WIDTH, position.y, -Template.WIDTH, Template.HEIGHT);
        }
        batch.end();
    }
}
