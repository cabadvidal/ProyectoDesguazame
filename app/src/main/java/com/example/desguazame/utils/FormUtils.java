package com.example.desguazame.utils;

import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Clase de utilidades para formularios que ofrece métodos estáticos
 * relacionados con la obtención de datos desde URLs en formato JSON.
 * <p>
 * Su propósito principal es centralizar la lógica de comunicación HTTP
 * y conversión de datos para facilitar su uso en otras partes del sistema.
 * </p>
 *
 * <p>Utiliza la biblioteca <code>org.json</code> para convertir los datos
 * obtenidos desde URLs en objetos {@link JSONArray}.</p>
 *
 * <p>Diseñada para ser utilizada en contextos donde se necesita consultar
 * servicios REST o endpoints JSON desde el cliente.</p>
 *
 * @author Charlie
 */
public class FormUtils {
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
}
