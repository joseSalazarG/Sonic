package component;

import com.almasb.fxgl.dsl.views.ScrollingBackgroundView;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.entity.EntityFactory;
import static com.almasb.fxgl.dsl.FXGL.*;
import com.almasb.fxgl.entity.SpawnData;

import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import component.Personajes.KnucklesComponent;
import component.Personajes.SonicComponent;
import component.Personajes.TailsComponent;
import javafx.geometry.Point2D;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.Spawns;
import GameSettings.Player;

public class GameFactory implements EntityFactory {

    public enum EntityType {
        PLAYER, FONDO, PLATAFORMA, KNUCKLES
    }

    @Spawns("fondo")
    public Entity newBackground(SpawnData data) {
        return entityBuilder()
                .view(new ScrollingBackgroundView(texture("background/forest.png").getImage(), getAppWidth(), getAppHeight()))
                .zIndex(-1)
                .with(new IrremovableComponent())
                .build();
    }
/*
    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        physics.addGroundSensor(new HitBox("GROUND_SENSOR", new Point2D(16, 38), BoundingShape.box(6, 8)));

        // this avoids player sticking to walls
        physics.setFixtureDef(new FixtureDef().friction(0.0f));

        return entityBuilder(data)
                .type(EntityType.PLAYER)
                .bbox(new HitBox(new Point2D(5,5), BoundingShape.circle(12)))
                .bbox(new HitBox(new Point2D(10,25), BoundingShape.box(10, 17)))
                .with(physics)
                .with(new CollidableComponent(true))
                .with(new IrremovableComponent())
                .with(new PlayerComponent())
                .build();
    } */

    @Spawns("knuckles")
    public Player knuckles(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        physics.addGroundSensor(new HitBox("GROUND_SENSOR", new Point2D(16, 38), BoundingShape.box(6, 8)));
        // this avoids player sticking to walls
        physics.setFixtureDef(new FixtureDef().friction(0.0f));

        Player player = new Player();
        player.setType(EntityType.KNUCKLES);
        player.bbox(new HitBox(new Point2D(5,5), BoundingShape.circle(12)));
        player.bbox(new HitBox(new Point2D(10,25), BoundingShape.box(10, 17)));
        player.addComponent(physics);
        player.addComponent(new CollidableComponent(true));
        player.addComponent(new IrremovableComponent());
        player.addComponent(new KnucklesComponent());

        return player;
    }

    @Spawns("tails")
    public Player tails(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        physics.addGroundSensor(new HitBox("GROUND_SENSOR", new Point2D(16, 38), BoundingShape.box(6, 8)));
        // this avoids player sticking to walls
        physics.setFixtureDef(new FixtureDef().friction(0.0f));

        Player player = new Player();
        player.setType(EntityType.PLAYER);
        player.bbox(new HitBox(new Point2D(5,5), BoundingShape.circle(12)));
        player.bbox(new HitBox(new Point2D(10,25), BoundingShape.box(10, 17)));
        player.addComponent(physics);
        player.addComponent(new CollidableComponent(true));
        player.addComponent(new IrremovableComponent());
        player.addComponent(new TailsComponent());

        return player;
    }

    @Spawns("sonic")
    public Player sonic(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        physics.addGroundSensor(new HitBox("GROUND_SENSOR", new Point2D(16, 38), BoundingShape.box(6, 8)));
        // this avoids player sticking to walls
        physics.setFixtureDef(new FixtureDef().friction(0.0f));

        Player player = new Player();
        player.setType(EntityType.PLAYER);
        player.bbox(new HitBox(new Point2D(5,5), BoundingShape.circle(12)));
        player.bbox(new HitBox(new Point2D(10,25), BoundingShape.box(10, 17)));
        player.addComponent(physics);
        player.addComponent(new CollidableComponent(true));
        player.addComponent(new IrremovableComponent());
        player.addComponent(new SonicComponent());

        return player;
    }

    @Spawns("plataforma")
    public Entity newPlatform(SpawnData data) {
        return entityBuilder(data)
                .type(EntityType.PLATAFORMA)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .build();
    }

}