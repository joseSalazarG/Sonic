package GameSettings;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.HitBox;
import component.Personajes.KnucklesComponent;
import component.Personajes.SonicComponent;
import component.Personajes.TailsComponent;

/**
 *
 * Esta clase sirve de puente entre los componentes
 * de cada personaje y como interactuan con el mundo.
 *
 */
public class Player extends Entity {

    public Player() {
        super();
    }

    public void moverIzquierda() {
        this.getComponentOptional(KnucklesComponent.class).ifPresent(KnucklesComponent::moverIzquierda);
        this.getComponentOptional(TailsComponent.class).ifPresent(TailsComponent::moverIzquierda);
        this.getComponentOptional(SonicComponent.class).ifPresent(SonicComponent::moverIzquierda);
    }

    public void moverDerecha() {
        this.getComponentOptional(KnucklesComponent.class).ifPresent(KnucklesComponent::moverDerecha);
        this.getComponentOptional(TailsComponent.class).ifPresent(TailsComponent::moverDerecha);
        this.getComponentOptional(SonicComponent.class).ifPresent(SonicComponent::moverDerecha);
    }

    public void saltar() {
        this.getComponentOptional(KnucklesComponent.class).ifPresent(KnucklesComponent::saltar);
        this.getComponentOptional(TailsComponent.class).ifPresent(TailsComponent::saltar);
        this.getComponentOptional(SonicComponent.class).ifPresent(SonicComponent::saltar);
    }

    public void detener() {
        this.getComponentOptional(KnucklesComponent.class).ifPresent(KnucklesComponent::detener);
        this.getComponentOptional(TailsComponent.class).ifPresent(TailsComponent::detener);
        this.getComponentOptional(SonicComponent.class).ifPresent(SonicComponent::detener);
    }

    public Player bbox(HitBox hitBox) {
        this.getBoundingBoxComponent().addHitBox(hitBox);
        return this;
    }
}
