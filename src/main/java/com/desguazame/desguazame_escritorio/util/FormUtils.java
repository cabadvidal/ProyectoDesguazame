/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desguazame.desguazame_escritorio.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Set;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

/**
 * Clase de utilidades para leer campos de formularios JavaFX contenidos en
 * estructuras {@link VBox} y {@link HBox}.
 * <p>
 * Esta clase proporciona métodos estáticos que permiten extraer los valores de
 * campos de entrada comunes como {@link TextField},
 * {@link PasswordField}, {@link ComboBox} y {@link CheckBox}, presentes dentro
 * de contenedores {@link VBox} o {@link HBox}. Además, permite navegar
 * recursivamente en contenedores anidados de tipo {@link VBox} y {@link HBox},
 * extrayendo los valores de cada nodo de forma organizada.</p>
 * <p>
 * El propósito de esta clase es facilitar la lectura de los datos introducidos
 * en un formulario, especialmente útil en aplicaciones que utilizan JavaFX para
 * crear interfaces gráficas de usuario (GUIs) interactivas.</p>
 *
 * <p>
 * Ejemplo de uso:</p>
 * <pre>{@code
 * VBox formulario = new VBox();
 * HashMap<String, String> campos = FormUtils.readNodeFields(formulario);
 * }</pre>
 *
 * @author Charlie
 */
public class FormUtils {

    /**
     * Extrae los valores de todos los
     * {@link TextField}, {@link PasswordField}, {@link ComboBox} y
     * {@link CheckBox} contenidos directa o recursivamente dentro de un
     * contenedor {@link HBox}, asumiendo que puede contener también otros
     * {@link VBox}.
     * <p>
     * Este método recursivo recorre todos los nodos hijos de un {@link HBox},
     * incluyendo contenedores anidados como {@link VBox}, y extrae los valores
     * de los campos de texto, contraseñas, combos y checkboxes.</p>
     *
     * @param hBox El contenedor {@link HBox} raíz desde el cual leer los
     * campos.
     * @return Un {@link HashMap} que mapea los IDs de los nodos a sus valores
     * de texto. El {@link HashMap} tendrá como clave el ID del campo y como
     * valor el texto o el valor correspondiente del campo.
     */
    public static HashMap<String, String> readNodeFields(HBox hBox) {
        HashMap<String, String> campos = new HashMap<>();

        for (Node node : hBox.getChildren()) {
            if (node instanceof VBox) {
                campos.putAll(readNodeFields((VBox) node));
            }
        }

        return campos;
    }

    /**
     * Extrae los valores de todos los
     * {@link TextField}, {@link PasswordField}, {@link ComboBox} y
     * {@link CheckBox} contenidos directa o recursivamente dentro de un
     * contenedor {@link VBox}, o anidados dentro de otros contenedores
     * {@link VBox} o {@link HBox}.
     * <p>
     * Este método recursivo recorre todos los nodos hijos de un {@link VBox},
     * incluyendo contenedores anidados, y extrae los valores de los campos de
     * texto, contraseñas, combos y checkboxes. Los campos de texto relacionados
     * con información sensible (como teléfono, móvil, DNI, correo electrónico,
     * etc.) son procesados para eliminar espacios innecesarios.</p>
     *
     * @param container El contenedor {@link VBox} raíz desde el cual leer los
     * campos.
     * @return Un {@link HashMap} que mapea los IDs de los nodos a sus valores
     * de texto. El {@link HashMap} tendrá como clave el ID del campo y como
     * valor el texto o el valor correspondiente del campo.
     */
    public static HashMap<String, String> readNodeFields(VBox container) {
        HashMap<String, String> campos = new HashMap<>();
        Set<String> camposConTrim = Set.of("TELEFONO", "MOVIL", "NUMERO_CUENTA", "DNI", "MAIL", "CODIGO_POSTAL");
        for (Node node : container.getChildren()) {
            if (node instanceof TextField) {
                TextField textField = (TextField) node;
                if (camposConTrim.contains(textField.getId())) {
                    putIfValid(campos, textField.getId(), textField.getText().trim().replace(" ", ""));
                } else {
                    putIfValid(campos, textField.getId(), textField.getText());
                }
            } else if (node instanceof PasswordField) {
                PasswordField passwordField = (PasswordField) node;
                putIfValid(campos, passwordField.getId(), passwordField.getText().trim());
            } else if (node instanceof ComboBox) {
                ComboBox<?> comboBox = (ComboBox<?>) node;
                Object value = comboBox.getValue();
                putIfValid(campos, comboBox.getId(), value != null ? value.toString() : "");
            } else if (node instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) node;
                putIfValid(campos, checkBox.getId(), Boolean.toString(checkBox.isSelected()));
            } else if (node instanceof VBox || node instanceof HBox) {
                campos.putAll(readNodeFields((VBox) node));
            }
        }

        return campos;
    }

    /**
     * Añade al mapa una entrada solo si el ID no es nulo ni vacío.
     * <p>
     * Este método se asegura de que solo se inserten entradas válidas en el
     * mapa, es decir, aquellas cuyo ID no sea {@code null} ni esté vacío.</p>
     *
     * @param map Mapa donde insertar la entrada.
     * @param id ID del nodo.
     * @param value Valor del nodo.
     */
    private static void putIfValid(HashMap<String, String> map, String id, String value) {
        if (id != null && !id.isEmpty()) {
            map.put(id, value);
        }
    }

    /**
     * Realiza una solicitud HTTP GET a la URL especificada y devuelve el
     * contenido en formato {@link JSONObject}.
     *
     * <p>
     * Este método abre una conexión a la URL indicada, lee su contenido como
     * una cadena, y la convierte a un objeto JSONArray usando la biblioteca
     * org.json.</p>
     *
     * <p>
     * Si ocurre un error de red, de sintaxis en la URL o durante el parseo
     * JSON, se capturan las excepciones y se imprime un mensaje de error por
     * consola. En caso de fallo, se devuelve {@code null}.</p>
     *
     * @param url La dirección URL desde la cual se desea obtener el JSON.
     * @return Un objeto {@link JSONArray} con los datos leídos o {@code null}
     * si ocurre algún error.
     */
    public static synchronized JSONArray readJsonDeUrl(String url) {
        InputStream is = null;
        JSONArray json = null;
        try {
            // Crear URI desde la URL
            URI uri = new URI(url);
            // Abrir flujo de entrada
            is = uri.toURL().openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            String linea;
            StringBuilder jsonText = new StringBuilder();

            while ((linea = rd.readLine()) != null) {
                jsonText.append(linea);
            }

            // Convertir el texto a un objeto JSON
            json = new JSONArray(jsonText.toString());

        } catch (URISyntaxException e) {
            System.err.println("URL inválida: " + url);
            e.printStackTrace();
        } catch (JSONException e) {
            System.err.println("Error al parsear JSON desde: " + url);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error de I/O al acceder a: " + url);
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    System.err.println("Error al cerrar el InputStream: " + e);
                }
            }
        }
        return json;
    }

    /**
     * Función que lee el fichero 'conf.xml' y devuelve el contenido del
     * 'tagName' que recibe como parámetro.
     *
     * @param tagName Contiene la etiqueta a consultar.
     * @return Devuelve el contenido de la etiqueta.
     */
    public static String leerXML(String tagName) {
        String url = null;
        try {
            // Leer el archivo XML desde recursos
            InputStream inputStream = FormUtils.class.getClassLoader().getResourceAsStream("com/desguazame/desguazame_escritorio/xml/conf.xml");

            if (inputStream == null) {
                throw new FileNotFoundException("Archivo conf.xml no encontrado en resources/xml/");
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();

            // Obtener el contenido de la etiqueta pasada como 'tagName'
            url = doc.getElementsByTagName(tagName).item(0).getTextContent();

        } catch (ParserConfigurationException | IOException | org.xml.sax.SAXException e) {
            System.err.println("Error al leer el XML: " + e);
        }

        return url;
    }
}
