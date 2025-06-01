/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desguazame.desguazame_escritorio.controller;

import com.desguazame.desguazame_escritorio.App;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.PASS;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.USR;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.socket;
import com.desguazame.desguazame_escritorio.util.FormUtils;
import com.desguazame.desguazame_escritorio.view.JOptionError;
import com.desguazame.desguazame_escritorio.view.StageWait;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.*;
import javafx.concurrent.*;
import javafx.fxml.*;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

/**
 * Controlador JavaFX para la ventana de inicio de sesión.
 *
 * <p>
 * Gestiona los eventos del formulario de login, como el cambio a la ventana de
 * registro, la autenticación del usuario y la navegación hacia la pantalla
 * principal de búsqueda. También maneja la interfaz de carga durante el proceso
 * de login.</p>
 *
 * <p>
 * Utiliza un hilo separado para realizar la autenticación de forma asíncrona y
 * sincroniza la respuesta del servidor mediante un {@link CountDownLatch}.</p>
 *
 * <p>
 * Los campos del formulario son leídos dinámicamente desde el VBox contenedor
 * mediante la clase {@link FormUtils}.</p>
 *
 * @author Charlie
 */
public class ControllerLogin implements Initializable {

    /**
     * Contenedor principal del formulario de login que contiene los campos de
     * entrada.
     */
    @FXML
    private VBox vBoxLogin;

    @FXML
    private TextField usuario;

    @FXML
    private PasswordField password;

    /**
     * Cambia a la escena de registro del usuario.
     *
     * @throws IOException si ocurre un error al cargar la escena "register".
     */
    @FXML
    private void changeRegister() throws IOException {
        App.setRoot("register");
    }

    /**
     * Evento que se lanza al hacer clic en el botón de login.
     *
     * <p>
     * Inicia una tarea asíncrona que muestra una ventana de carga mientras se
     * realiza el proceso de autenticación del usuario. Al finalizar la tarea,
     * se cambia a la escena principal si la autenticación es exitosa, o se
     * muestra un mensaje de error si falla.</p>
     *
     * @throws IOException si ocurre un error durante el cambio de escena.
     */
    @FXML
    private void onLoginButtonClick() throws IOException {
        StageWait stageWait = new StageWait();
        stageWait.showLoadingWindow(); // Muestra la ventana de carga

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                changeSearch(); // Proceso de autenticación
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
     * Realiza el proceso de login mediante socket y cambia a la ventana de
     * búsqueda si es exitoso.
     *
     * <p>
     * Lee los datos del formulario, los envía al servidor a través del socket y
     * espera la respuesta utilizando un {@link CountDownLatch}. Si la
     * autenticación es válida, cambia a la escena "search".</p>
     *
     * @throws IOException si ocurre un error al cambiar de escena.
     */
    private void changeSearch() throws IOException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean success = new AtomicBoolean(false);

        HashMap<String, String> campos = FormUtils.readNodeFields(vBoxLogin);

        socket.authUser(campos, latch, success);

        // Esperar la respuesta del servidor en un hilo separado
        new Thread(() -> {
            try {
                latch.await(); // Bloquea hasta recibir respuesta
                if (success.get()) {
                    Platform.runLater(() -> {
                        try {
                            App.setRoot("search"); // Cambio de escena si autenticación fue exitosa
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

    private void onLoad() throws IOException {
        usuario.setText(USR);
        password.setText(PASS);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            try {
                onLoad(); // Se ejecutará cuando la ventana esté completamente cargada
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
