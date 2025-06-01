package com.example.desguazame.utils;

import static com.example.desguazame.utils.AppGlobals.cartList;
import static com.example.desguazame.utils.AppGlobals.token;
import com.example.desguazame.access.CarPart;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Clase que representa los datos del carrito de compra necesarios para
 * generar una factura y realizar un pago.
 * <p>
 * Al instanciar esta clase, se construye automáticamente un objeto
 * {@link JSONObject} que incluye el tipo de pago, el token del usuario,
 * la base imponible, el IVA y la lista de piezas seleccionadas.
 * </p>
 *
 * La estructura generada es compatible con el backend del sistema.
 *
 * {@code
 * {
 *   "BASE": double,
 *   "IVA": double,
 *   "TIPO_PAGO": String,
 *   "TOKEN": String,
 *   "PIEZAS": [ { "ID_PIEZA": int }, ... ]
 * }
 * }
 *
 * @author Charlie
 */
public class CartData {

    private String tipoPago;
    private JSONObject dataJSON;

    /**
     * Crea una instancia de CartData con el tipo de pago especificado. Al crear
     * la instancia, se genera automáticamente el objeto JSON con los datos de
     * la factura.
     *
     * @param tipoPago Tipo de pago seleccionado (por ejemplo: "Tarjeta",
     *                 "Efectivo").
     */
    public CartData(String tipoPago) {
        this.tipoPago = tipoPago;
        dataJSON = createJSONAPay();
    }

    /**
     * Genera el objeto JSON que representa los datos de la factura y las piezas
     * en el carrito. Calcula la base imponible y el IVA, y añade todas las
     * piezas con su ID.
     *
     * @return JSONObject con la siguiente estructura: { "BASE": double, "IVA":
     * double, "TIPO_PAGO": String, "TOKEN": String, "PIEZAS": [ { "ID_PIEZA":
     * int }, ... ] }
     */
    private JSONObject createJSONAPay() {
        JSONObject data = new JSONObject();
        double base = cartList.stream().mapToDouble(CarPart::getPrice).sum();
        double iva = base * 0.21;

        try {
            data.put("BASE", base);
            data.put("IVA", iva);
            data.put("TIPO_PAGO", tipoPago);
            data.put("TOKEN", token);

            JSONArray piezasArray = new JSONArray();
            for (CarPart part : cartList) {
                JSONObject piezaObj = new JSONObject();
                piezaObj.put("ID_PIEZA", part.getId());
                piezasArray.put(piezaObj);
            }
            data.put("PIEZAS", piezasArray);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    /**
     * Devuelve el objeto JSON generado con los datos del carrito listos para
     * enviar al servidor.
     *
     * @return JSONObject con los datos del pago.
     */
    public JSONObject getDataJSON() {
        return dataJSON;
    }
}
