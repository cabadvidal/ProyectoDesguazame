package com.desguazame.desguazame_escritorio.controller;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
import com.desguazame.desguazame_escritorio.App;
import com.desguazame.desguazame_escritorio.model.CartData;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.socket;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.token;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.user;
import com.desguazame.desguazame_escritorio.view.CartView;
import com.desguazame.desguazame_escritorio.view.JOptionError;
import static com.desguazame.desguazame_escritorio.view.JOptionError.showError;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.json.*;

/**
 * Controlador FXML para la vista del carrito de compras.
 * <p>
 * Gestiona la inicialización de la vista, el cambio entre vistas (búsqueda y
 * login), y el proceso de selección y envío del tipo de pago. Utiliza sockets
 * para comunicarse con el servidor al realizar el pago.
 * </p>
 *
 * <p>
 * Vista asociada: <strong>cart.fxml</strong></p>
 *
 * @author Charlie
 */
public class ControllerCart implements Initializable {

    @FXML
    private VBox vBoxCart;

    @FXML
    private ToggleGroup optionGroup;

    @FXML
    private VBox vBoxCheckOut;

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

    /**
     * Carga la vista del carrito mediante la clase {@code CartView}, inyectando
     * los paneles de productos y de resumen de compra.
     *
     * @throws IOException Si ocurre un error al cargar los elementos visuales.
     */
    private void onLoad() throws IOException {
        new CartView(vBoxCart, vBoxCheckOut);
    }

    /**
     * Cambia a la vista search.
     *
     * @throws IOException si ocurre un error al cambiar a la vista "search".
     */
    @FXML
    private void changeSearch() throws IOException {
        App.setRoot("search");
    }

    /**
     * Cierra la sesión actual y vuelve a la vista de login.
     *
     * @throws IOException si ocurre un error al cambiar a la vista "login".
     */
    @FXML
    private void changeLogin() throws IOException {
        user = null;
        token = "";
        App.setRoot("login");
    }

    /**
     * Inicia el proceso de pago tras seleccionar el tipo de pago.
     * <p>
     * Verifica que se haya seleccionado una opción de pago. Luego, solicita al
     * servidor autorización para realizar el pago usando sockets. Si el
     * servidor responde correctamente, construye el JSON con los datos del
     * carrito y lo envía para su procesamiento.
     * </p>
     *
     * @throws IOException Si ocurre un error al interactuar con el sistema de
     * vistas.
     */
    @FXML
    private void changePay() throws IOException {
        RadioButton selectedRadio = (RadioButton) optionGroup.getSelectedToggle();
        if (selectedRadio == null) {
            showError("Tipo pago", "Seleccione el tipo de pago a realizar.");
            return;
        }
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean success = new AtomicBoolean(false);
        socket.activity("Realizar pago", latch, success);

        try {
            // Esperar respuesta del servidor con timeout por seguridad (por ejemplo, 10s)
            if (!latch.await(10, TimeUnit.SECONDS)) {
                Platform.runLater(() -> JOptionError.showError("Timeout", "No se recibió respuesta del servidor."));
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ControllerRegister.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Si la actividad fue exitosa, obtener los datos de las piezas para realizar el pago
        if (success.get()) {
            String metodoPago = selectedRadio.getText();

            CartData cD = new CartData(metodoPago);
            JSONObject data = cD.getDataJSON();
            System.out.println("El resultado " + data.toString());
            CountDownLatch latch2 = new CountDownLatch(1);
            AtomicBoolean success2 = new AtomicBoolean(false);
            socket.makePayment(data, latch2, success2);
            // Esperar la respuesta del servidor en un hilo separado
            new Thread(() -> {
                try {
                    latch.await(); // Bloquea hasta recibir respuesta
                    if (success2.get()) {
                        Platform.runLater(() -> {
                            try {
                                App.setRoot("pay"); // Cambio de escena si autenticación fue exitosa
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
        
        App.setRoot("pay");
    }
}
