/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desguazame.desguazame_escritorio.util;

import com.desguazame.desguazame_escritorio.model.CarPart;
import com.desguazame.desguazame_escritorio.model.Sockets;
import com.desguazame.desguazame_escritorio.model.User;
import static com.desguazame.desguazame_escritorio.util.FormUtils.leerXML;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de utilidades globales de la aplicación.
 * <p>
 * Esta clase contiene variables estáticas accesibles desde cualquier parte del
 * programa, que representan el estado actual del usuario, la conexión de
 * socket, las listas de piezas, y las configuraciones como las URLs del
 * servidor y de las imágenes.
 * </p>
 *
 * <p>
 * Los valores como la URL del socket y de las imágenes se obtienen desde un
 * archivo XML mediante la función {@code leerXML}.
 * </p>
 *
 * @author Charlie
 */
public class AppGlobals {

    /**
     * Usuario actualmente autenticado en la aplicación.
     */
    public static User user;

    /**
     * Token de autenticación del usuario actual.
     */
    public static String token;

    /**
     * URL del servidor de sockets, leída desde el archivo de configuración XML.
     */
    public static String socketURL = leerXML("socketurl");

    /**
     * Instancia de la clase {@code Sockets} usada para la comunicación en
     * tiempo real.
     */
    public static Sockets socket = new Sockets(socketURL);

    /**
     * Lista de todas las piezas disponibles cargadas desde el servidor.
     */
    public static List<CarPart> carList = new ArrayList<>();

    /**
     * Lista de piezas seleccionadas por el usuario para comprar (carrito de la
     * compra).
     */
    public static List<CarPart> cartList = new ArrayList<>();

    /**
     * URL base desde donde se cargan las imágenes de las piezas, obtenida desde
     * XML.
     */
    public static String imgURL = leerXML("imgurl");
}
