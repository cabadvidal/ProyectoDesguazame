/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desguazame.desguazame_escritorio.model;

import static com.desguazame.desguazame_escritorio.util.AppGlobals.carList;
import com.desguazame.desguazame_escritorio.view.SearchView;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.json.*;

/**
 * Clase encargada de procesar y cargar los datos de búsqueda de piezas en la
 * interfaz.
 * <p>
 * Esta clase convierte un {@code JSONArray} recibido desde el servidor en una
 * lista de objetos {@code CarPart}, que luego se utilizan para generar
 * dinámicamente la vista correspondiente mediante la clase {@code SearchView}.
 * </p>
 *
 * <p>
 * También limpia la lista anterior de piezas para evitar duplicados.</p>
 *
 * @author Charlie
 */
public class SearchData {

    /**
     * Constructor que inicializa y carga la lista de piezas desde los datos
     * JSON.
     *
     * @param vBox El contenedor de la interfaz donde se mostrarán las piezas.
     * @param dataArray El array de objetos JSON recibido del servidor con la
     * información de piezas.
     * @param lblCartCount Etiqueta que muestra la cantidad actual de elementos
     * en el carrito.
     */
    public SearchData(VBox vBox, JSONArray dataArray, Label lblCartCount) {
        chargeList(vBox, dataArray, lblCartCount);
    }

    /**
     * Procesa los datos del {@code JSONArray} y construye una lista de piezas
     * {@code CarPart}, que se almacena globalmente en {@code carList}.
     * <p>
     * Luego, se instancia la vista {@code SearchView} para mostrar los
     * resultados al usuario.
     * </p>
     *
     * @param vBox El contenedor de interfaz para insertar visualmente las
     * piezas.
     * @param dataArray El array JSON con los datos de las piezas obtenidos del
     * servidor.
     * @param lblCartCount Etiqueta de la interfaz que refleja la cantidad de
     * piezas en el carrito.
     */
    public void chargeList(VBox vBox, JSONArray dataArray, Label lblCartCount) {
        carList.clear();
        vBox.getChildren().clear();

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject dataObject = dataArray.getJSONObject(i);

            int id = dataObject.getInt("ID_PIEZAS");
            String description = dataObject.optString("DESCRIPCION", "");
            int weight = (int) Double.parseDouble(dataObject.optString("PESO", "0"));
            double price = Double.parseDouble(dataObject.optString("PRECIO", "0"));
            String reference = dataObject.optString("REFERENCIA", "");
            String year = dataObject.optString("FECHA_YEAR", "");

            // Separar imágenes por ';'
            String imagenesStr = dataObject.optString("IMAGENES", "");
            String[] imagenes = imagenesStr.isEmpty() ? new String[0] : imagenesStr.split(";");

            String categories = dataObject.optString("CATEGORIAS", "");
            String sale = dataObject.optString("RAZON_SOCIAL", "");
            String model = dataObject.optString("NOMBRE_MODELO", "");
            String brand = dataObject.optString("NOMBRE_MARCA", "");
            String company = dataObject.optString("RAZON_SOCIAL", "");

            CarPart cp = new CarPart(id, description, weight, price, reference, year, imagenes, categories, sale, model, brand, company);
            carList.add(cp);
        }

        new SearchView(vBox, lblCartCount);
    }
}
