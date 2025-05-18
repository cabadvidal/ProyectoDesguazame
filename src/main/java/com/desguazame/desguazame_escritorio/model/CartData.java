/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desguazame.desguazame_escritorio.model;

import static com.desguazame.desguazame_escritorio.util.AppGlobals.cartList;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.token;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Clase que encapsula los datos necesarios para realizar un pago a partir del
 * carrito de piezas. Calcula automáticamente la base imponible, el IVA y
 * prepara un objeto JSON con toda la información necesaria para enviar al
 * servidor.
 *
 * Este JSON incluye: BASE, IVA, TIPO_PAGO, TOKEN y un array de piezas con sus
 * respectivos ID.
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
     * "Efectivo").
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
