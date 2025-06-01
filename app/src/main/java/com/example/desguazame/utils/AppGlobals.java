package com.example.desguazame.utils;

import com.example.desguazame.access.User;
import com.example.desguazame.access.CarPart;

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
     * Token de autenticación en FCM
     */
    public static String token_fcm;

    /**
     * URL del servidor de sockets, leída desde el archivo de configuración XML.
     */
    public static String socketURL = "http://83.37.180.236:10010";

    /**
     * URL del servidor de para obtener las imagenes.
     */
    public static String imgURL = "http://83.37.180.236:10010/img/piezas/";

    /**
     * URL del servidor de para obtener las facturas.
     */
    public static String facturaURL = "http://83.37.180.236:10010/descargar/factura/";

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
     * Contiene el id de la factura generada
     */
    public static int id_factura = 0;
}