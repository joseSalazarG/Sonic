package component;

import GameSettings.Player;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.time.TimerAction;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGL.getGameTimer;

public class GameLogic extends Component implements Serializable {

    private Text textoBasuraGlobal = crearTextoGlobal();
    private static String caucho = "Cauchos: 0";
    private static String anillos = "Anillos: 0";
    private static String vidas = "Vidas: 3";
    private static String basura = "Basura: 0";
    private static String papel = "Papel: 0";

    public static void imprimir(String titulo) {
        System.out.println(titulo);
    }

    public static void actualizarUI() {
        getGameScene().clearUINodes();
        addText(anillos, 20, 20);
        addText(basura, 180, 20);
        addText(papel, 350, 20);
        addText(caucho, 500, 20);
        addText(vidas, 20, 75);
        botonAyuda();
    }

    private static void botonAyuda() {
        ImageView botonAyuda = new ImageView("assets/textures/Escenario/boton_ayuda.png");
        botonAyuda.setFitWidth(50);
        botonAyuda.setFitHeight(50);
        botonAyuda.setFitHeight(50);
        botonAyuda.setTranslateX(850);
        botonAyuda.setTranslateY(30);

        botonAyuda.setOnMouseClicked(e -> {
            System.out.println("Ayuda");
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "Escribe aqui las reglas",
                    ButtonType.CLOSE);
            alert.setTitle("Mensaje de Ayuda");
            alert.setHeaderText("REGLAS");
            alert.showAndWait();
        });

        getGameScene().addUINode(botonAyuda);
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

    public static void setAnillos(String mensaje) {
        anillos = mensaje;
        actualizarUI();
    }

    public static void setBasura(String mensaje) {
        basura = mensaje;
        actualizarUI();
    }

    public static void setPapel(String mensaje) {
        papel = mensaje;
        actualizarUI();
    }

    public static void setCaucho(String mensaje) {
        caucho = mensaje;
        actualizarUI();
    }

    public static void setVidas(String mensaje) {
        vidas = mensaje;
        actualizarUI();
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
