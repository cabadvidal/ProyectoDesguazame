/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desguazame.desguazame_escritorio.view;

import java.util.Random;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Clase que representa una animación visual de confeti dentro de una interfaz JavaFX.
 * <p>
 * Utiliza un hilo en segundo plano que genera rectángulos de colores de forma periódica,
 * aplicando animaciones de caída y rotación para simular el efecto visual de confeti descendente.
 * La animación se ejecuta sobre un {@link javafx.scene.layout.Pane} proporcionado al constructor.
 * </p>
 *
 * <p>
 * Esta clase está pensada para ser usada con:
 * <ul>
 *     <li>Superficies de celebración o feedback visual tras eventos exitosos.</li>
 *     <li>Indicadores animados de éxito o recompensa.</li>
 * </ul>
 * </p>
 *
 * @author Charlie
 */
public class ConfettiView extends Thread {

    private final Pane overlayPane;
    private final boolean running = true;
    private final Random random = new Random();

    /**
     * Crea una instancia del efecto de confeti para el {@code overlayPane} especificado.
     * 
     * @param overlayPane el contenedor sobre el cual se mostrarán los rectángulos animados.
     */
    public ConfettiView(Pane overlayPane) {
        this.overlayPane = overlayPane;
        setDaemon(true); // Para que se cierre con la app
    }

    /**
     * Método principal del hilo. Lanza una tarea periódica en el hilo de la UI
     * para crear y animar confeti sobre el contenedor especificado.
     */
    @Override
    public void run() {
        while (running) {
            Platform.runLater(() -> {
                double width = overlayPane.getWidth() > 0 ? overlayPane.getWidth() : 800;
                double height = overlayPane.getHeight() > 0 ? overlayPane.getHeight() : 600;
                Rectangle confetti = createConfetti(width);
                overlayPane.getChildren().add(confetti);
                animateConfetti(confetti, height);
            });

            try {
                Thread.sleep(100); // Frecuencia de creación (ajustable)
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    /**
     * Crea un rectángulo con color aleatorio que representa un fragmento de confeti.
     *
     * @param width el ancho máximo del contenedor para posicionar aleatoriamente el confeti.
     * @return un nuevo nodo {@link Rectangle} estilizado y posicionado.
     */
    private Rectangle createConfetti(double width) {
        Rectangle rect = new Rectangle(6, 12);
        rect.setArcWidth(3);
        rect.setArcHeight(3);
        rect.setFill(Color.hsb(random.nextDouble() * 360, 1, 1));
        rect.setTranslateX(random.nextDouble() * width);
        rect.setTranslateY(-20);
        return rect;
    }

    /**
     * Aplica las animaciones de caída y rotación al fragmento de confeti.
     *
     * @param confetti el nodo a animar.
     * @param height   la altura máxima hasta donde debe caer el confeti.
     */
    private void animateConfetti(Node confetti, double height) {
        double fallDuration = 3 + random.nextDouble() * 2;

        TranslateTransition fall = new TranslateTransition(Duration.seconds(fallDuration), confetti);
        fall.setToY(height + 30);
        fall.setInterpolator(Interpolator.EASE_OUT);

        RotateTransition rotate = new RotateTransition(Duration.seconds(fallDuration), confetti);
        rotate.setByAngle(720 - random.nextInt(360));
        rotate.setInterpolator(Interpolator.LINEAR);

        ParallelTransition animation = new ParallelTransition(fall, rotate);
        animation.setOnFinished(e -> overlayPane.getChildren().remove(confetti));
        animation.play();
    }
}
