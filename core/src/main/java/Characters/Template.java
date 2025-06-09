package Characters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lombok.Getter;

public abstract class Template {

    /** The player character, has state and state time, */
    static float WIDTH;
    static float HEIGHT;
    static float MAX_VELOCITY = 10f;
    static float JUMP_VELOCITY = 40f;
    static float DAMPING = 0.87f;
    protected Texture texture;
    public Animation<TextureRegion> stand;
    public Animation<TextureRegion> walk;
    public Animation<TextureRegion> jump;

    enum State {
        Standing, Walking, Jumping
    }

    public final Vector2 position = new Vector2();
    public final Vector2 velocity = new Vector2();
    State state = State.Walking;
    public float stateTime = 0;
    public boolean facesRight = true;
    @Getter
    boolean grounded = false;


    public void setWidth(float width) {
        Template.WIDTH = width;
    }
    public void setHeight(float height) {
        Template.HEIGHT = height;
    }
    public float getWidth() {
        return Template.WIDTH;
    }
    public float getHeight() {
        return Template.HEIGHT;
    }
    public float getMaxVelocity() {return MAX_VELOCITY;}
    public float getDamping() {return DAMPING;}

    public void jump() {
        velocity.y += Template.JUMP_VELOCITY;
        state = Template.State.Jumping;
        grounded = false;
    }

    public void stand() {
        velocity.x = 0;
        if (grounded) state = State.Standing;
        stateTime = 0; // reset the state time to avoid flickering
    }

    public void resetJump(float y, float height) {
        position.y = y + height;
        // if we hit the ground, mark us as grounded so we can jump
        grounded = true;
    }

    public void moveLeft() {
        velocity.x = -Template.MAX_VELOCITY;
        if (grounded) state = State.Walking;
        facesRight = false;
    }

    public void moveRight() {
        velocity.x = Template.MAX_VELOCITY;
        if (grounded) state = State.Walking;
        facesRight = true;
    }

    public void create() {
        // load the koala frames, split them, and assign them to Animations
        texture = new Texture("koalio.png");
        TextureRegion[] regions = TextureRegion.split(texture, 18, 26)[0];
        stand = new Animation<TextureRegion>(0, regions[0]);
        jump = new Animation<TextureRegion>(0, regions[1]);
        walk = new Animation<TextureRegion>(0.15f, regions[2], regions[3], regions[4]);
        walk.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        // figure out the width and height of the koala for collision
        // detection and rendering by converting a koala frames pixel
        // size into world units (1 unit == 16 pixels)
        setWidth(1 / 16f * regions[0].getRegionWidth());
        setHeight(1 / 16f * regions[0].getRegionHeight());
    }

    public void render(float deltaTime, Batch batch) {
        // based on the koala state, get the animation frame
        TextureRegion frame = switch (state) {
            case Standing -> stand.getKeyFrame(stateTime);
            case Walking -> walk.getKeyFrame(stateTime);
            case Jumping -> jump.getKeyFrame(stateTime);
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
