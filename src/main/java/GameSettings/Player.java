package GameSettings;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.physics.HitBox;
import component.Personajes.KnucklesComponent;
import component.Personajes.SonicComponent;
import component.Personajes.TailsComponent;

import static com.almasb.fxgl.dsl.FXGL.play;

/**
 *
 * Esta clase sirve de puente entre los componentes
 * de cada personaje y como interactuan con el mundo.
 *
 */
public class Player extends Entity {

    String tipo = "";
    private int vidas = 3;
    private Connection<Bundle> conexion;
    private boolean invencible = false;
    private boolean transformado = false;

    public Player() {
        super();
    }

    public void setInvencibilidad(boolean flag) {
        this.invencible = flag;
    }

    public boolean isInvencible() {
        return invencible;
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

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }

    public void interactuar() {
        this.getComponentOptional(KnucklesComponent.class).ifPresent(KnucklesComponent::interactuar);
        //this.getComponentOptional(TailsComponent.class).ifPresent(TailsComponent::interactuar);
        //this.getComponentOptional(SonicComponent.class).ifPresent(SonicComponent::interactuar);
    }

    public int getVidas() {
        return vidas;
    }

    public void restarVida() {
        vidas--;
    }

    public boolean estaMuerto() {
        return vidas <= 0;
    }

    public boolean estaTransformado() {
        return transformado;
    }

    public void setTransformado(boolean transformado) {}

    public void transformarSuperSonic() {
        if (!isInvencible()) {
            this.getComponentOptional(SonicComponent.class).ifPresent(SonicComponent::transformarSuperSonic);
            setTransformado(true);
        } else {
            this.getComponentOptional(SonicComponent.class).ifPresent(SonicComponent::destransformar);
            setTransformado(false);
        }
        setInvencibilidad(!isInvencible());
    }
}
