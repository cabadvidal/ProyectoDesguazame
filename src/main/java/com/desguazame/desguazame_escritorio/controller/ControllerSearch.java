package com.desguazame.desguazame_escritorio.controller;

import com.desguazame.desguazame_escritorio.App;
import com.desguazame.desguazame_escritorio.model.SearchData;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.searchData;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.searchMenuURL;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.socket;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.user;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.token;
import com.desguazame.desguazame_escritorio.util.FormUtils;
import static com.desguazame.desguazame_escritorio.util.FormUtils.readJsonDeUrl;
import com.desguazame.desguazame_escritorio.view.JOptionError;
import com.desguazame.desguazame_escritorio.view.SearchView;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.logging.*;
import javafx.application.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.json.*;

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

    @FXML
    private ComboBox<String> cbCategory;

    @FXML
    private ComboBox<String> cbBrand;

    @FXML
    private ComboBox<String> cbModel;
    
    @FXML
    private TextField txtSearch;

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

    /**
     * Solicita y carga en el ComboBox {@code cbBrand} la lista de marcas
     * disponibles.
     *
     * @throws IOException si ocurre un error al obtener los datos desde el
     * servidor.
     */
    @FXML
    private void hoverBrand() throws IOException {
        String table = "MARCAS";
        String url = searchMenuURL + table;

        JSONArray data = readJsonDeUrl(url);

        System.out.println("resultado " + data.toString());
        ObservableList<String> brandList = FXCollections.observableArrayList();
        brandList.add("Marcas");
        
        for (int i = 0; i < data.length(); i++) {
            JSONObject obj = data.getJSONObject(i);
            brandList.add(obj.getString("NOMBRE_MARCA")); // extraemos solo el nombre
        }

        cbBrand.setItems(brandList); // cargamos en el ComboBox
    }

    /**
     * Solicita y carga en el ComboBox {@code cbCategory} la lista de categorías
     * disponibles.
     *
     * @throws IOException si ocurre un error al obtener los datos desde el
     * servidor.
     */
    @FXML
    private void hoverCategory() throws IOException {
        String table = "CATEGORIAS";
        String url = searchMenuURL + table;

        JSONArray data = readJsonDeUrl(url);

        System.out.println("resultado " + data.toString());
        ObservableList<String> categoryList = FXCollections.observableArrayList();
        categoryList.add("Categorías");
        
        for (int i = 0; i < data.length(); i++) {
            JSONObject obj = data.getJSONObject(i);
            categoryList.add(obj.getString("NOMBRE_CATEGORIA"));
        }

        cbCategory.setItems(categoryList);
    }

    /**
     * Solicita los modelos correspondientes a la marca seleccionada y los carga
     * en el ComboBox {@code cbModel}.
     *
     * @throws IOException si ocurre un error al obtener los datos desde el
     * servidor.
     */
    @FXML
    private void onClickBrand() throws IOException {
        String brand = cbBrand.getValue();
        brand = brand.toUpperCase();
        String table = "MODELOS";
        String url = searchMenuURL + table + "&MARCA=" + brand;

        JSONArray data = readJsonDeUrl(url);
        System.out.println("resultado " + data.toString());
        ObservableList<String> modelList = FXCollections.observableArrayList();
        modelList.add("Modelos");
        
        for (int i = 0; i < data.length(); i++) {
            JSONObject obj = data.getJSONObject(i);
            modelList.add(obj.getString("NOMBRE_MODELO"));
        }

        cbModel.setItems(modelList);
    }
    
    @FXML
    private void searchAPI() throws IOException {
        String txtS = txtSearch.getText();
        String category = cbCategory.getValue();
        String brand = cbBrand.getValue();
        String model = cbModel.getValue();
        String url = searchData;
        if(!txtS.equals("Buscar...")){
            url += "TEXTO=" + txtS;
        }
        if(!category.equals("Categorías")) {
            url += url.charAt(url.length() - 1) == '?' ? "CATEGORIAS=" + category : "&CATEGORIAS=" + category;
        }
        if(!brand.equals("Marcas")) {
            url += url.charAt(url.length() - 1) == '?' ? "MARCAS=" + brand : "&MARCAS=" + brand;
        }
        if(!model.equals("Modelos")) {
            url += url.charAt(url.length() - 1) == '?' ? "MODELOS=" + model : "&MODELOS=" + model;
        }
        
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean success = new AtomicBoolean(false);
        socket.activity("Realizar búsqueda", latch, success);
        final String FINAL_URL = url;
        try {
            // Esperar respuesta del servidor con timeout por seguridad (por ejemplo, 10s)
            if (!latch.await(10, TimeUnit.SECONDS)) {
                Platform.runLater(() -> JOptionError.showError("Timeout", "No se recibió respuesta del servidor."));
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ControllerRegister.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (success.get()) {
            // Usar CompletableFuture para leer los datos de forma asincrónica
            CompletableFuture<JSONArray> future = CompletableFuture.supplyAsync(() -> {
                return FormUtils.readJsonDeUrl(FINAL_URL);
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
}
