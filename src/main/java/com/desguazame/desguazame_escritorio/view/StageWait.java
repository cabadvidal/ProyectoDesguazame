/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desguazame.desguazame_escritorio.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Clase que gestiona una ventana de carga modal para indicar al usuario que se
 * está realizando una operación.
 * <p>
 * La ventana muestra un mensaje y un indicador de progreso circular. Es modal y
 * no se puede cerrar manualmente por el usuario hasta que la operación haya
 * finalizado.
 * </p>
 *
 * @author Charlie
 */
public class StageWait {

    private Stage loadingStage;

    /**
     * Muestra una ventana modal con un mensaje de "Realizando registro..." y un
     * indicador de progreso.
     * <p>
     * Esta ventana bloquea la interacción con otras ventanas de la aplicación
     * hasta que se cierra mediante el método {@link #closeLoadingWindow()}.
     * </p>
     */
    public void showLoadingWindow() {
        Label label = new Label("Realizando registro...");
        ProgressIndicator pi = new ProgressIndicator();

        VBox box = new VBox(10, label, pi);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));

        Scene scene = new Scene(box, 250, 100);

        loadingStage = new Stage();
        loadingStage.setTitle("Por favor, espere");
        loadingStage.setScene(scene);
        loadingStage.initModality(Modality.APPLICATION_MODAL); // bloquea hasta que se cierre
        loadingStage.setResizable(false);
        loadingStage.setOnCloseRequest(e -> e.consume()); // impide cerrar

        loadingStage.show();
    }

    /**
     * Cierra la ventana de carga si está visible.
     * <p>
     * Este método debe llamarse cuando finaliza la operación que requiere
     * espera.
     * </p>
     */
    public void closeLoadingWindow() {
        if (loadingStage != null) {
            loadingStage.close();
        }
    }

}
