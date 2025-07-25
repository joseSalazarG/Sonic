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
import component.Enemigos.EggmanComponent;
import component.Personajes.KnucklesComponent;
import component.Personajes.SonicComponent;
import component.Personajes.TailsComponent;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.Spawns;
import GameSettings.Player;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import com.almasb.fxgl.multiplayer.NetworkComponent;

/**
 * GameFactory define los tipos de entidades que se pueden crear en el juego.
 * Cada tipo de entidad tiene su propio metodo de creacion que se invoca
 * al momento de instanciar una entidad en el juego.
 */

public class GameFactory implements EntityFactory {
    

    public enum EntityType {
        PLAYER, FONDO, TIERRA, ROBOT_ENEMIGO, RING, AGUA, BASURA, ARBOL, PAPEL, CAUCHO, EGGMAN, KNUCKLES, SONIC, TAILS, ESMERALDA
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

    @Spawns("sonic")
    public Player sonic(SpawnData data) {
        PhysicsComponent fisicas = new PhysicsComponent();
        fisicas.setBodyType(BodyType.DYNAMIC); // esto le agrega gravedad al personaje
        fisicas.addGroundSensor(new HitBox("GROUND_SENSOR", new Point2D(16, 38), BoundingShape.box(6, 8)));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setFriction(1.0f);  // Esto es lo mejor que encontre para reducir al minimo los empujes, pero no lo soluciona
        fixtureDef.setRestitution(0.2f); // rebote
        fisicas.setFixtureDef(fixtureDef);

        Player player = new Player();

        player.setType(EntityType.PLAYER);
        player.setTipo("sonic");
        player.getBoundingBoxComponent().addHitBox(new HitBox(new Point2D(5,5), BoundingShape.circle(12)));
        player.getBoundingBoxComponent().addHitBox(new HitBox(new Point2D(10,25), BoundingShape.box(10, 17)));

        player.addComponent(fisicas);
        player.addComponent(new CollidableComponent(true));
        player.getComponent(CollidableComponent.class).addIgnoredType(EntityType.PLAYER);
        player.addComponent(new IrremovableComponent());
        player.addComponent(new SonicComponent());

        player.setPosition(data.getX(), data.getY());
        player.getProperties().setValue("altura", 32.0);

        return player;
    }

    @Spawns("tails")
    public Player tails(SpawnData data) {
        PhysicsComponent fisicas = new PhysicsComponent();
        fisicas.setBodyType(BodyType.DYNAMIC); // esto le agrega gravedad al personaje
        fisicas.addGroundSensor(new HitBox("GROUND_SENSOR", new Point2D(16, 38), BoundingShape.box(6, 8)));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setFriction(1.0f); // friccion
        fixtureDef.setRestitution(0.2f); // rebote
        fisicas.setFixtureDef(fixtureDef);

        Player player = new Player();

        player.setType(EntityType.PLAYER);
        player.setTipo("tails");
        player.getBoundingBoxComponent().addHitBox(new HitBox(new Point2D(5,5), BoundingShape.circle(12)));
        player.getBoundingBoxComponent().addHitBox(new HitBox(new Point2D(10,25), BoundingShape.box(10, 17)));

        player.addComponent(fisicas);
        player.addComponent(new CollidableComponent(true));
        player.getComponent(CollidableComponent.class).addIgnoredType(EntityType.PLAYER);
        player.addComponent(new IrremovableComponent());
        player.addComponent(new TailsComponent());

        player.setPosition(data.getX(), data.getY());
        player.getProperties().setValue("altura", 32.0);

        return player;
    }

    @Spawns("knuckles")
    public Player knuckles(SpawnData data) {
        PhysicsComponent fisicas = new PhysicsComponent();
        fisicas.setBodyType(BodyType.DYNAMIC); // esto le agrega gravedad al personaje
        fisicas.addGroundSensor(new HitBox("GROUND_SENSOR", new Point2D(16, 38), BoundingShape.box(6, 8)));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setFriction(1.0f); // friccion
        fixtureDef.setRestitution(0.2f); // rebote
        fisicas.setFixtureDef(fixtureDef);

        Player player = new Player();

        player.setType(EntityType.PLAYER);
        player.setTipo("knuckles");
        player.getBoundingBoxComponent().addHitBox(new HitBox(new Point2D(5,5), BoundingShape.circle(12)));
        player.getBoundingBoxComponent().addHitBox(new HitBox(new Point2D(10,24), BoundingShape.box(10, 17)));

        player.addComponent(fisicas);
        player.addComponent(new CollidableComponent(true));
        player.getComponent(CollidableComponent.class).addIgnoredType(EntityType.PLAYER);
        player.addComponent(new IrremovableComponent());
        player.addComponent(new KnucklesComponent());

        player.setPosition(data.getX(), data.getY());
        player.getProperties().setValue("altura", 32.0);

        return player;
    }

    @Spawns("eggman")
    public Player eggman(SpawnData data) {
        PhysicsComponent fisicas = new PhysicsComponent();
        fisicas.setBodyType(BodyType.KINEMATIC);

        FixtureDef fixture = new FixtureDef();
        fixture.setFriction(1.0f);
        fixture.setRestitution(0.0f);
        fisicas.setFixtureDef(fixture);

        Player eggman = new Player();
        eggman.setType(EntityType.EGGMAN);
        eggman.getBoundingBoxComponent().addHitBox(new HitBox(new Point2D(10, 10), BoundingShape.circle(40)));
        eggman.addComponent(fisicas);
        eggman.addComponent(new CollidableComponent(true));
        eggman.getProperties().setValue("altura", 100.0);
        eggman.addComponent(new EggmanComponent());

        eggman.getProperties().setValue("id", java.util.UUID.randomUUID().toString());

        return eggman;
    }

    @Spawns("plataforma")
    public Entity newPlatform(SpawnData data) {
        PhysicsComponent fisicas = new PhysicsComponent();
        fisicas.setBodyType(BodyType.STATIC);

        return entityBuilder(data)
                .type(EntityType.TIERRA)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(fisicas)
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

    @Spawns("robotEnemigo")
    public Entity robotEnemigo(SpawnData data) {
        PhysicsComponent fisicas = new PhysicsComponent();
        fisicas.setBodyType(BodyType.DYNAMIC);

        Entity robot = entityBuilder(data)
                .type(EntityType.ROBOT_ENEMIGO)
                .bbox(new HitBox(new Point2D(10, 10), BoundingShape.circle(35))) // radio 32 centrada
                .with(fisicas)
                .with(new CollidableComponent(true))
                .with(new component.Enemigos.RobotComponent())
                .with("altura", 100.0)
                .build(); 

        robot.getProperties().setValue("id", java.util.UUID.randomUUID().toString()); 

        return robot;
    }

    @Spawns("esmeralda")
    public Entity esmeralda(SpawnData data) {
        Entity esmeralda = entityBuilder(data)
                .type(EntityType.ESMERALDA)
                .bbox(new HitBox(new Point2D(0, 0), BoundingShape.circle(12))) 
                .with(new component.Items.EsmeraldaComponent())
                .with(new CollidableComponent(true))
                .with("esmeraldaId", UUID.randomUUID().toString())
                .build();
        esmeralda.getProperties().setValue("esmeraldaId", java.util.UUID.randomUUID().toString());
        return esmeralda;
    }
}