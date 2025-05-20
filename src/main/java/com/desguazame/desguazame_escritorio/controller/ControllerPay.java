/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desguazame.desguazame_escritorio.controller;

import com.desguazame.desguazame_escritorio.App;
import com.desguazame.desguazame_escritorio.view.ConfettiView;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;

/**
 *
 * @author Charlie
 */
public class ControllerPay implements Initializable {

    @FXML
    Pane pane;

    @FXML
    private void changeSearch() throws IOException {
        App.setRoot("search");
    }

    private void onLoad() throws IOException {
        Pane confettiOverlay = new Pane();
        confettiOverlay.setPickOnBounds(false); // para que no interfiera con eventos
        confettiOverlay.setMouseTransparent(true);

        // Asegura que ocupe todo el espacio del pane
        confettiOverlay.prefWidthProperty().bind(pane.widthProperty());
        confettiOverlay.prefHeightProperty().bind(pane.heightProperty());

        // Añádelo a tu layout principal
        pane.getChildren().add(confettiOverlay);

        // Lanza el hilo del confeti
        ConfettiView confettiThread = new ConfettiView(confettiOverlay);
        confettiThread.start();
    }

    /**
     * Inicializa la vista de búsqueda tras su carga.
     * <p>
     * Llama al método {@code onLoad()} para establece el menú.
     * </p>
     *
     * @param location La ubicación utilizada para resolver rutas relativas al
     * objeto raíz.
     * @param resources Los recursos utilizados para localizar la raíz del
     * objeto.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            onLoad();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
