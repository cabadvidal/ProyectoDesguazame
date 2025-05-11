package com.desguazame.desguazame_escritorio.controller;

import com.desguazame.desguazame_escritorio.App;
import com.desguazame.desguazame_escritorio.threads.WebSocket;
import com.desguazame.desguazame_escritorio.util.AppGlobals;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.socket;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.user;
import com.desguazame.desguazame_escritorio.util.FormUtils;
import com.desguazame.desguazame_escritorio.view.JOptionError;
import com.desguazame.desguazame_escritorio.view.StageWait;
import io.socket.client.Socket;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ControllerLogin {

    @FXML
    private VBox vBoxLogin;

    /**
     * Método para cambiar a la ventana de registro.
     *
     * @throws IOException
     */
    @FXML
    private void changeRegister() throws IOException {
        App.setRoot("register");
    }

    @FXML
    private void onLoginButtonClick() throws IOException {
        StageWait stageWait = new StageWait();
        stageWait.showLoadingWindow(); // Muestra la ventana de carga

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                changeSearch();
                return null;
            }

            @Override
            protected void succeeded() {
                stageWait.closeLoadingWindow(); // Cierra si todo va bien
            }

            @Override
            protected void failed() {
                stageWait.closeLoadingWindow(); // Cierra aunque haya fallo
                Platform.runLater(() -> {
                    JOptionError.showError("Error", "Error al realizar autenticación.");
                });
            }
        };

        new Thread(task).start();
    }

    /**
     * Método para verificar login en el servidor y cambiar a la ventana de
     * usuario.
     *
     * @throws IOException
     */
    private void changeSearch() throws IOException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean success = new AtomicBoolean(false);
        HashMap<String, String> campos = FormUtils.readNodeFields(vBoxLogin);
        socket.authUser(campos, latch, success);

        // Esperar a que el socket devuelva la respuesta
        new Thread(() -> {
            try {
                latch.await(); // Espera hasta que authUser() termine y haga latch.countDown()
                if (success.get()) {
                    // Cambio de escena en el hilo de JavaFX
                    Platform.runLater(() -> {
                        try {
                            App.setRoot("search");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
