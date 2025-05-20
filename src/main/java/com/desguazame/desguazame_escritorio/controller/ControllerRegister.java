package com.desguazame.desguazame_escritorio.controller;

import com.desguazame.desguazame_escritorio.App;
import com.desguazame.desguazame_escritorio.model.DataVerify;
import com.desguazame.desguazame_escritorio.model.User;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.socket;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.user;
import com.desguazame.desguazame_escritorio.util.FormUtils;
import com.desguazame.desguazame_escritorio.view.JOptionError;
import com.desguazame.desguazame_escritorio.view.StageWait;
import java.io.*;
import java.util.*;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.*;
import java.util.logging.*;
import javafx.application.*;
import javafx.concurrent.*;
import javafx.fxml.*;
import javafx.scene.layout.*;

/**
 * Controlador de la vista de registro de usuarios en la aplicación JavaFX.
 * <p>
 * Esta clase se encarga de gestionar la interacción del usuario con el formulario de registro,
 * realizar la validación de campos, enviar los datos al servidor y manejar las respuestas.
 * También muestra una ventana de carga mientras se realiza la operación.
 * </p>
 * 
 * @author Charlie
 */
public class ControllerRegister {

    /**
     * Contenedor principal del formulario de registro que contiene los campos de entrada.
     */
    @FXML
    private HBox hBoxRegister;

    /**
     * Cambia la vista actual a la pantalla de inicio de sesión.
     *
     * @throws IOException si no se puede cargar la vista "login".
     */
    @FXML
    private void changeLogin() throws IOException {
        App.setRoot("login");
    }

    /**
     * Método llamado cuando el usuario pulsa el botón de registro.
     * <p>
     * Lanza una tarea en segundo plano que realiza el registro del usuario. Mientras se realiza,
     * muestra una ventana de carga. Si se produce un error, muestra un mensaje de error.
     * </p>
     */
    @FXML
    private void onRegisterButtonClick() {
        StageWait stageWait = new StageWait();
        stageWait.showLoadingWindow(); // Muestra la ventana de carga

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                registerUser(); 
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
                    JOptionError.showError("Error", "Error al registrar usuario.");
                });
            }
        };

        new Thread(task).start();
    }

     /**
     * Método encargado de validar los campos del formulario, convertir datos numéricos,
     * enviar la información al servidor y crear el objeto {@code User} en caso de éxito.
     * <p>
     * Utiliza un {@link CountDownLatch} para esperar la respuesta del servidor y un {@link AtomicBoolean}
     * para saber si el registro fue exitoso. Si falla la validación o hay un error de formato,
     * se muestra una ventana de error.
     * </p>
     *
     * @throws IOException si ocurre un error al redirigir la vista tras el registro exitoso.
     */
    private void registerUser() throws IOException {
        HashMap<String, String> campos = FormUtils.readNodeFields(hBoxRegister);
        System.out.println("com.desguazame.desguazame_escritorio.controller.ControllerRegister.registerUser()" + campos.toString());
        for (Map.Entry<String, String> entry : campos.entrySet()) {
            String clave = entry.getKey();
            String valor = "NUMERO_CUENTA".equals(clave) ? entry.getValue().replace(" ", "") : entry.getValue();
            if (!DataVerify.verificarDato(clave, valor)) {
                JOptionError.showError(clave, valor);
                return;
            }
        }
        int codePostal = 0;
        long phone = 0;
        long cell = 0;
        long creditCard = 0;
        String[] dataValue = {"CODIGO_POSTAL", "TELEFONO", "MOVIL", "TARJETA_CREDITO"};
        String key = "";
        String value = "";
        try {
            for (String dv : dataValue) {
                switch (dv) {
                    case "CODIGO_POSTAL":
                        codePostal = Integer.parseInt(campos.get("CODIGO_POSTAL"));
                        key = "CODIGO_POSTAL";
                        value = campos.get("CODIGO_POSTAL");
                        break;
                    case "TELEFONO":
                        phone = Long.parseLong(campos.get("TELEFONO"));
                        key = "TELEFONO";
                        value = campos.get("TELEFONO");
                        break;
                    case "MOVIL":
                        cell = Long.parseLong(campos.get("MOVIL"));
                        key = "MOVIL";
                        value = campos.get("MOVIL");
                        break;
                    case "TARJETA_CREDITO":
                        creditCard = Long.parseLong(campos.get("TARJETA_CREDITO"));
                        key = "TARJETA_CREDITO";
                        value = campos.get("TARJETA_CREDITO");
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        } catch (NumberFormatException e) {
            JOptionError.showError(key, value);
            return;
        }
        System.out.println("Registro de usuario");
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean success = new AtomicBoolean(false);

        socket.registerUser(campos, latch, success);

        try {
            // Esperar respuesta del servidor con timeout por seguridad (por ejemplo, 10s)
            if (!latch.await(10, TimeUnit.SECONDS)) {
                Platform.runLater(() -> JOptionError.showError("Timeout", "No se recibió respuesta del servidor."));
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ControllerRegister.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (success.get()) {
            if (user == null) {
                user = new User(campos.get("NOMBRE"), campos.get("APELLIDO_A"), campos.get("APELLIDO_B"), campos.get("CONTRASENA"), campos.get("DIRECCION"), codePostal, campos.get("CIDUAD"), campos.get("MUNICIPIO"), phone, cell, campos.get("DNI_CIF"), campos.get("NUMERO_CUENTA"), campos.get("MAIL"), creditCard);
            } else {
                user.setName(campos.get("NOMBRE"));
                user.setfName(campos.get("APELLIDO_A"));
                user.setsName(campos.get("APELLIDO_B"));
                user.setPassword(campos.get("CONTRASENA"));
                user.setAddress(campos.get("DIRECCION"));
                user.setCodePostal(codePostal);
                user.setCity(campos.get("CIDUAD"));
                user.setMunicipality(campos.get("MUNICIPIO"));
                user.setPhone(phone);
                user.setCell(cell);
                user.setDni(campos.get("DNI"));
                user.setBank(campos.get("NUMERO_CUENTA"));
                user.setMail(campos.get("MAIL"));
                user.setCreditCard(creditCard);
            }

            App.setRoot("search");
        }
    }
}
