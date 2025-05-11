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
 *
 * @author Charlie
 */
public class StageWait {

    private Stage loadingStage;

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

    public void closeLoadingWindow() {
        if (loadingStage != null) {
            loadingStage.close();
        }
    }

}
