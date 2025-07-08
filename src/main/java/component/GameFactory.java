package component;

import com.almasb.fxgl.dsl.views.ScrollingBackgroundView;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.entity.EntityFactory;
import static com.almasb.fxgl.dsl.FXGL.*;

import java.util.UUID;

import com.almasb.fxgl.entity.SpawnData;

import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import component.Personajes.KnucklesComponent;
import component.Personajes.SonicComponent;
import component.Personajes.TailsComponent;
import component.Enemigos.EggmanComponent;
import component.Items.TrashComponent;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.Spawns;
import GameSettings.Player;

public class GameFactory implements EntityFactory {
    

    public enum EntityType {
        PLAYER, FONDO, TIERRA, ROBOT_ENEMIGO, RING, AGUA, BASURA, ARBOL, PAPEL, CAUCHO, EGGMAN, KNUCKLES, SONIC, TAILS
    }

    @Spawns("fondo")
    public Entity newBackground(SpawnData data) {
        return entityBuilder()
                .view(new ScrollingBackgroundView(texture("background/background2.png").getImage(), getAppWidth(), getAppHeight(), Orientation.HORIZONTAL, 0.2))
                .zIndex(-1)
                .with(new IrremovableComponent())
                .at(0, 0)
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

    @Spawns("sonic")
    public Player sonic(SpawnData data) {
            PhysicsComponent physics = new PhysicsComponent();
            physics.setBodyType(BodyType.DYNAMIC);
            physics.addGroundSensor(new HitBox("GROUND_SENSOR", new Point2D(16, 38), BoundingShape.box(6, 8)));

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.setFriction(1.0f);  // Esto es lo mejor que encontre para reducir al minimo los empujes, pero no lo soluciona
            fixtureDef.setRestitution(0.0f);
            physics.setFixtureDef(fixtureDef);

            Player player = new Player();

            player.setType(EntityType.PLAYER);
            player.setTipo("sonic");
            player.getBoundingBoxComponent().addHitBox(new HitBox(new Point2D(5,5), BoundingShape.circle(12)));
            player.getBoundingBoxComponent().addHitBox(new HitBox(new Point2D(10,25), BoundingShape.box(10, 17)));

            player.addComponent(physics);
            player.addComponent(new CollidableComponent(true));
            player.addComponent(new IrremovableComponent());
            player.addComponent(new SonicComponent());

            player.setPosition(data.getX(), data.getY());

        return player;
    }

    @Spawns("tails")
    public Player tails(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        physics.addGroundSensor(new HitBox("GROUND_SENSOR", new Point2D(16, 38), BoundingShape.box(6, 8)));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setFriction(1.0f);
        fixtureDef.setRestitution(0.0f);
        physics.setFixtureDef(fixtureDef);

        Player player = new Player();

        player.setType(EntityType.PLAYER);
        player.setTipo("tails");
        player.getBoundingBoxComponent().addHitBox(new HitBox(new Point2D(5,5), BoundingShape.circle(12)));
        player.getBoundingBoxComponent().addHitBox(new HitBox(new Point2D(10,25), BoundingShape.box(10, 17)));

        player.addComponent(physics);
        player.addComponent(new CollidableComponent(true));
        player.addComponent(new IrremovableComponent());
        player.addComponent(new TailsComponent());

        player.setPosition(data.getX(), data.getY());

        return player;
    }

    @Spawns("knuckles")
    public Player knuckles(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        physics.addGroundSensor(new HitBox("GROUND_SENSOR", new Point2D(16, 38), BoundingShape.box(6, 8)));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setFriction(1.0f);
        fixtureDef.setRestitution(0.0f);
        physics.setFixtureDef(fixtureDef);

        Player player = new Player();

        player.setType(EntityType.PLAYER);
        player.setTipo("knuckles");
        player.getBoundingBoxComponent().addHitBox(new HitBox(new Point2D(5,5), BoundingShape.circle(12)));
        player.getBoundingBoxComponent().addHitBox(new HitBox(new Point2D(10,24), BoundingShape.box(10, 17)));

        player.addComponent(physics);
        player.addComponent(new CollidableComponent(true));
        player.addComponent(new IrremovableComponent());
        player.addComponent(new KnucklesComponent());

        player.setPosition(data.getX(), data.getY());

        return player;
    }

    @Spawns("plataforma")
    public Entity newPlatform(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.STATIC);

        return entityBuilder(data)
                .type(EntityType.TIERRA)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(physics)
                .build();
    }

    @Spawns("agua")
    public Entity newAgua(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.STATIC);

        return entityBuilder(data)
                .type(EntityType.AGUA)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(physics)
                .build();
    }

    @Spawns("robotEnemigo")
    public Entity robotEnemigo(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        return entityBuilder(data)
                .type(EntityType.ROBOT_ENEMIGO)
                .bbox(new HitBox(new Point2D(0, 0), BoundingShape.circle(24))) // radio 32 centrada
                .with(physics)
                .with(new CollidableComponent(true))
                .with(new component.Enemigos.RobotComponent())
                .build();
    }

    @Spawns("ring")
    public Entity ring(SpawnData data) {
        Entity ring = entityBuilder(data)
                .type(EntityType.RING)
                .bbox(new HitBox(new Point2D(0, 0), BoundingShape.circle(12))) 
                .with(new component.Items.RingComponent())
                .with(new CollidableComponent(true))
                .with("ringId", UUID.randomUUID().toString())
                .build();
        ring.getProperties().setValue("id", java.util.UUID.randomUUID().toString());
        return ring;
    }

    @Spawns("basura")
    public Entity basura(SpawnData data) {
        Entity basura = entityBuilder(data)
                .type(EntityType.BASURA)
                .bbox(new HitBox(new Point2D(0, 0), BoundingShape.circle(12)))
                .with(new component.Items.TrashComponent())
                .with(new CollidableComponent(true))
                .with("trashId", UUID.randomUUID().toString())
                .build();
        basura.getProperties().setValue("id", java.util.UUID.randomUUID().toString());
        return basura;
    }

    @Spawns("papel")
    public Entity papel(SpawnData data) {
        Entity papel = entityBuilder(data)
                .type(EntityType.PAPEL)
                .bbox(new HitBox(new Point2D(0, 0), BoundingShape.circle(12)))
                .with(new component.Items.PapelComponent())
                .with(new CollidableComponent(true))
                .with("trashId", UUID.randomUUID().toString())
                .build();
        papel.getProperties().setValue("id", java.util.UUID.randomUUID().toString());
        return papel;
    }

    @Spawns("caucho")
    public Entity caucho(SpawnData data) {
        Entity caucho = entityBuilder(data)
                .type(EntityType.CAUCHO)
                .bbox(new HitBox(new Point2D(0, 0), BoundingShape.circle(12)))
                .with(new component.Items.CauchoComponent())
                .with(new CollidableComponent(true))
                .with("trashId", UUID.randomUUID().toString())
                .build();
        caucho.getProperties().setValue("id", java.util.UUID.randomUUID().toString());
        return caucho;
    }

    @Spawns("arbol")
    public Entity newArbol(SpawnData data) {
        return entityBuilder(data)
                .type(EntityType.ARBOL)
                .view("Escenario/arbol.png") 
                .zIndex(1)
                .build();
    }

    @Spawns("eggman")
    public Entity patrulla(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.KINEMATIC);
       FixtureDef fixture = new FixtureDef();
        fixture.setFriction(1.0f);
        fixture.setRestitution(0.0f);
        physics.setFixtureDef(fixture);

        return entityBuilder(data)
                .type(EntityType.EGGMAN)
                .bbox(new HitBox(BoundingShape.box(32, 32)))
                .with(physics)
                .with(new CollidableComponent(true))
                .with(new EggmanComponent())
                .build();
    }
}