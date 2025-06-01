/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desguazame.desguazame_escritorio.util;

import java.io.*;
import java.net.*;
import java.util.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.*;
import org.json.*;
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
        Set<String> camposConTrim = Set.of("TELEFONO", "MOVIL", "NUMERO_CUENTA", "DNI", "MAIL", "CODIGO_POSTAL", "TARJETA_CREDITO");
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

    /**
     * Guarda un valor cifrado en binario asociado a una clave (tipo).
     * <p>
     * El valor se cifra utilizando el algoritmo AES en modo CBC con padding y
     * se almacena en un archivo binario "data.bin". Si el archivo ya existe, se
     * mantiene su contenido y se actualiza el valor indicado.</p>
     *
     * @param type Clave identificadora del dato (por ejemplo, "USER" o "PASS").
     * @param value Valor que se desea cifrar y guardar.
     */
    public static void writeBIN(String type, String value) {
        try {
            File file = new File("data.bin");
            HashMap<String, byte[]> data;

            // Si existe, cargamos contenido previo
            if (file.exists()) {
                try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                    data = (HashMap<String, byte[]>) in.readObject();
                }
            } else {
                data = new HashMap<>();
            }

            // Ciframos el valor
            byte[] encrypted = encrypt(value);
            data.put(type.toUpperCase(), encrypted);

            // Guardamos
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                out.writeObject(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Lee un valor cifrado desde el archivo binario y lo descifra.
     * <p>
     * Este método busca el dato asociado a la clave indicada, lo descifra
     * utilizando AES y devuelve el texto original.</p>
     *
     * @param type Clave identificadora del dato.
     * @return El valor descifrado correspondiente a la clave, o {@code null} si
     * no existe o ocurre un error.
     */
    public static String readBIN(String type) {
        try {
            File file = new File("data.bin");
            if (!file.exists()) {
                return "";
            }

            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                HashMap<String, byte[]> data = (HashMap<String, byte[]>) in.readObject();
                byte[] encrypted = data.get(type.toUpperCase());
                if (encrypted != null) {
                    return decrypt(encrypted);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Cifra un texto plano utilizando AES/CBC/PKCS5Padding.
     *
     * @param plainText Texto a cifrar.
     * @return Array de bytes cifrados.
     * @throws Exception Si ocurre un error durante el proceso de cifrado.
     */
    private static byte[] encrypt(String plainText) throws Exception {
        final String AES_KEY = "1234567890abcdef";
        final String AES_IV = "abcdef1234567890";
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(AES_IV.getBytes());
        SecretKeySpec keySpec = new SecretKeySpec(AES_KEY.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(plainText.getBytes());
    }

    /**
     * Descifra un array de bytes cifrados con AES/CBC/PKCS5Padding.
     *
     * @param cipherBytes Array de bytes cifrados.
     * @return Texto descifrado.
     * @throws Exception Si ocurre un error durante el descifrado.
     */
    private static String decrypt(byte[] cipherBytes) throws Exception {
        final String AES_KEY = "1234567890abcdef";
        final String AES_IV = "abcdef1234567890";
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(AES_IV.getBytes());
        SecretKeySpec keySpec = new SecretKeySpec(AES_KEY.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decrypted = cipher.doFinal(cipherBytes);
        return new String(decrypted);
    }
}
