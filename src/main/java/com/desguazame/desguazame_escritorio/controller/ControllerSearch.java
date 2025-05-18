package com.desguazame.desguazame_escritorio.controller;

import com.desguazame.desguazame_escritorio.App;
import com.desguazame.desguazame_escritorio.model.CarPart;
import com.desguazame.desguazame_escritorio.model.SearchData;
import com.desguazame.desguazame_escritorio.model.Sockets;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.carList;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.socket;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.user;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.token;
import com.desguazame.desguazame_escritorio.util.FormUtils;
import com.desguazame.desguazame_escritorio.view.JOptionError;
import com.desguazame.desguazame_escritorio.view.SearchView;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Controlador de la vista de búsqueda de la aplicación JavaFX.
 * <p>
 * Esta clase se encarga de mostrar un mensaje de bienvenida al usuario
 * autenticado y permite cerrar sesión y volver a la pantalla de login.
 * </p>
 *
 * Implementa {@link Initializable} para ejecutar acciones al cargar la vista.
 *
 * @author Charlie
 */
public class ControllerSearch implements Initializable {

    /**
     * Etiqueta que muestra un mensaje de bienvenida personalizado al usuario.
     */
    @FXML
    private Label welcome;

    @FXML
    private VBox vBoxMarket;
    
    @FXML
    private Label lblCartCount;

    private SearchView search;

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
     * Cambia a la vista cart.
     *
     * @throws IOException si ocurre un error al cambiar a la vista "cart".
     */
    @FXML
    private void changeCart() throws IOException {
        App.setRoot("cart");
    }

    /**
     * Método encargado de establecer el mensaje de bienvenida al usuario
     * utilizando los datos almacenados en el objeto {@code user}.
     *
     * @throws IOException si ocurre un error inesperado durante la carga.
     */
    private void onLoad() throws IOException {
        String welcomer = "Bienvenido/a, " + user.getfName() + " " + user.getsName() + ", " + user.getName();
        welcome.setText(welcomer);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean success = new AtomicBoolean(false);
        socket.activity("Obtener datos piezas", latch, success);
        try {
            // Esperar respuesta del servidor con timeout por seguridad (por ejemplo, 10s)
            if (!latch.await(10, TimeUnit.SECONDS)) {
                Platform.runLater(() -> JOptionError.showError("Timeout", "No se recibió respuesta del servidor."));
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ControllerRegister.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Si la actividad fue exitosa, obtener los datos de las piezas
        if (success.get()) {
            // Usar CompletableFuture para leer los datos de forma asincrónica
            CompletableFuture<JSONArray> future = CompletableFuture.supplyAsync(() -> {
                return FormUtils.readJsonDeUrl("http://192.168.1.122:10010/ObtenerPiezas");
            });

            // Esperar el resultado y procesarlo
            future.thenAccept(data -> {
                if (data != null) {
                    Platform.runLater(() -> {
                        System.out.println("Los datos de las piezas: " + data.toString());
                        
                        JSONArray dataArray = new JSONArray(data);
                        new SearchData(vBoxMarket, dataArray, lblCartCount);
                    });
                } else {
                    Platform.runLater(() -> {
                        JOptionError.showError("Error", "No se pudieron obtener los datos.");
                    });
                }
            }).exceptionally(ex -> {
                Platform.runLater(() -> {
                    JOptionError.showError("Error", "Hubo un problema al obtener los datos: " + ex.getMessage());
                });
                return null;
            });
        }
    }

    /**
     * Inicializa la vista de búsqueda tras su carga.
     * <p>
     * Llama al método {@code onLoad()} para establecer el mensaje de
     * bienvenida.
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
