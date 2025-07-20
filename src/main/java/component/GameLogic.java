package component;

import GameSettings.Player;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.time.TimerAction;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGL.getGameTimer;

public class GameLogic extends Component implements Serializable {

    private Text textoCaucho;
    private Text textoAnillos;
    private Text textoBasuraGlobal = crearTextoGlobal();
    private Text textoVidas;
    private Text textoBasura;
    private Text textoPapel;

    /**
     * Inicializa los textos que se mostrarán en la interfaz del juego.
     */
    public GameLogic() {
        textoCaucho = new Text("Caucho: 0");
        textoCaucho.setStyle("-fx-font-size: 24px; -fx-fill: red;");
        textoCaucho.setFont(Font.font("Impact", 24));
        textoAnillos = new Text("Anillos: 0");
        textoAnillos.setStyle("-fx-font-size: 24px; -fx-fill: yellow;");
        textoAnillos.setFont(Font.font("Impact", 24));
        textoVidas = new Text("Vidas: 3");
        textoVidas.setStyle("-fx-font-size: 24px; -fx-fill: green;");
        textoVidas.setFont(Font.font("Impact", 24));
        textoBasura = new Text("Basura: 0");
        textoBasura.setStyle("-fx-font-size: 24px; -fx-fill: blue;");
        textoBasura.setFont(Font.font("Impact", 24));
        textoPapel = new Text("Papel: 0");
        textoPapel.setStyle("-fx-font-size: 24px; -fx-fill: white;");
        textoPapel.setFont(Font.font("Impact", 24));

        addUINode(textoAnillos, 20, 20);
        addUINode(textoBasura, 140, 20);
        addUINode(textoPapel, 240, 20);
        addUINode(textoCaucho, 330, 20);
        addUINode(textoVidas, 20, 75);
    }

    public static void imprimir(String titulo) {
        System.out.println(titulo);
    }

    public static void agregarBarra(float num) {
        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress(num);
        if (num > 0.75) {
            progressBar.setStyle("-fx-accent: red; -fx-background-color: transparent;");
        } else if (num > 0.5) {
            progressBar.setStyle("-fx-accent: orange; -fx-background-color: transparent;");
        } else {
            progressBar.setStyle("-fx-accent: green; -fx-background-color: transparent;");
        }
        progressBar.setPrefWidth(200);
        progressBar.setPrefHeight(10);
        progressBar.setLayoutX(20);
        progressBar.setLayoutY(30);
        getGameScene().getRoot().getChildren().add(progressBar);
    }

    /**
     * Aplica un filtro de color a la pantalla del juego.
     *
     * @param num Un valor entre 0 y 1 que determina el grado del filtro de color.
     */
    public static void filtroColor(float num) {
        //0.15 es el maximo
        ColorAdjust filtro = new ColorAdjust();
        filtro.setHue(num * 0.15); // Cambia el tono
        getGameScene().getRoot().setEffect(filtro);
    }

    public static void agregarTexto(String mensaje, String color, int size, int x, int y) {
        Text texto = new Text(mensaje);
        texto.setStyle("-fx-font-size: 24px; -fx-fill: color;");
        texto.setFont(Font.font("Impact", size));
        addUINode(texto, x, y);
    }

    private static Text crearTextoGlobal() {
        Text texto = new Text("Basura restante: 0");
        texto.setStyle("-fx-font-size: 24px; -fx-fill: white;");
        texto.setFont(Font.font("Impact", 24));
        addUINode(texto, 770, 20);
        return texto;
    }

    public void cambiarTextoBasuraGlobal(String mensaje) {
        textoBasuraGlobal.setText(mensaje);
    }

    public void cambiarTextoAnillos(String mensaje) {
        textoAnillos.setText(mensaje);
    }

    public void cambiarTextoCaucho(String mensaje) {
        textoCaucho.setText(mensaje);
    }

    public void cambiarTextoVidas(String mensaje) {
        textoVidas.setText(mensaje);
    }

    public void cambiarTextoBasura(String mensaje) {
        textoBasura.setText(mensaje);
    }

    public void cambiarTextoPapel(String mensaje) {
        textoPapel.setText(mensaje);
    }

    public void init() {
        // Inicialización de la lógica del juego
        textoCaucho.setText("Caucho: 0");
        textoAnillos.setText("Anillos: 0");
        textoVidas.setText("Vidas: 3");
        textoBasura.setText("Basura: 0");
        textoPapel.setText("Papel: 0");
    }

    /**
     * Activa la invencibilidad del jugador despues de ser golpeado por un enemigo.
     *
     * @param milisegundos Duración de la invencibilidad en milisegundos.
     * @param player       El jugador al que se le activará la invencibilidad.
     */
    public static void activarInvencibilidad(int milisegundos, Player player) {
        player.setInvencibilidad(true);

        TimerAction blinkAction = getGameTimer().runAtInterval(() -> {
            player.getViewComponent().setVisible(!player.getViewComponent().isVisible());
        }, Duration.millis(200));

        getGameTimer().runOnceAfter(() -> {
            player.setInvencibilidad(false);
            player.getViewComponent().setVisible(true);
            blinkAction.expire();
        }, Duration.millis(milisegundos));
    }

    /**
     * Muestra un mensaje de victoria y cierra el juego.
     */
    public static void Ganaste(int anillos, int basura, int papel, int caucho, int robots) {
        String mensaje = String.format(
                "¡Ganaste!\n\nEstadisticas:\n" +
                        "- Anillos: %d\n" +
                        "- Basura: %d\n" +
                        "- Papel: %d\n" +
                        "- Caucho: %d\n" +
                        "- Robots destruidos: %d",
                anillos, basura, papel, caucho, robots
        );

        getDialogService().showMessageBox(mensaje, () -> {
            FXGL.getGameController().exit();
        });
    }

    /**
     * Muestra un mensaje de derrota y cierra el juego.
     */
    public static void gameOver() {
        getDialogService().showMessageBox("Game Over", () -> {
            FXGL.getGameController().exit();
        });
    }
}
