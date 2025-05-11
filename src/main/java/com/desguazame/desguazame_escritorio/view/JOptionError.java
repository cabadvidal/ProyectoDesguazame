/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desguazame.desguazame_escritorio.view;

import javax.swing.JOptionPane;

/**
 * Clase utilitaria para mostrar mensajes de error mediante {@link JOptionPane}.
 * <p>
 * Esta clase proporciona métodos para mostrar mensajes de error
 * contextualizados basados en la clave y el valor pasados como parámetros. Se
 * utiliza principalmente para validar campos en formularios y proporcionar
 * retroalimentación inmediata al usuario.</p>
 *
 * <p>
 * Los métodos definidos en esta clase pueden ser utilizados para mostrar
 * mensajes de error específicos para cada tipo de entrada, como un correo
 * electrónico inválido, un teléfono incorrecto o un número de cuenta mal
 * formado, entre otros.</p>
 *
 * <p>
 * Ejemplo de uso:</p>
 * <pre>{@code
 * JOptionError.showError("TELEFONO", "123456789");
 * }</pre>
 *
 * @author Charlie
 */
public class JOptionError extends JOptionPane {

    /**
     * Muestra un mensaje de error contextualizado según la clave y el valor.
     *
     * <p>
     * Este método determina el tipo de error basado en la clave proporcionada
     * (por ejemplo, "NOMBRE", "DNI_CIF", "CODIGO_POSTAL") y muestra un mensaje
     * de error correspondiente. El mensaje puede incluir detalles adicionales
     * proporcionados en el valor.</p>
     *
     * @param clave Clave que representa el campo o tipo de error. Ejemplos
     * incluyen "NOMBRE", "TELEFONO", "DNI_CIF".
     * @param valor Valor asociado o detalle del error. Este parámetro puede ser
     * el valor ingresado por el usuario o un detalle específico del error.
     */
    public static void showError(String clave, String valor) {
        String message = "";
        String title = "";
        switch (clave) {
            case "NOMBRE":
            case "APELLIDO_A":
            case "APELLIDO_B":
                title = "Nombre o Apellido";
                message = "El campo " + clave.toLowerCase().replace("_", " ") + " no es válido: " + valor;
                break;
            case "MUNICIPIO":
            case "CIUDAD":
                title = "Localización";
                message = "Problema con la localización: " + valor;
                break;
            case "TELEFONO":
                title = clave;
                message = "El teléfono introducido deben ser números de 9 dígitos en total.";
                break;
            case "MOVIL":
                title = clave;
                message = "El movil introducido deben ser números de 9 dígitos en total." + valor;
                break;
            case "NUMERO_CUENTA":
                title = clave.replace("_", " ");
                message = "El número de cuenta introducido debe incluir el IBAN ES y los números.";
                break;
            case "DNI_CIF":
                title = "Documento de identidad";
                message = "El número de DNI introducido no es válido.";
                break;
            case "MAIL":
                title = "Correo eléctronico";
                message = "El correo electrónico no es válido: " + valor;
                break;
            case "CODIGO_POSTAL":
                title = "Código postal";
                message = "El código postal introducido debe incluir 5 dígitos entre 01000 al 52999.";
                break;
            default:
                title = clave;
                message = valor;
        }

        createError(title, message);
    }

    /**
     * Muestra un cuadro de diálogo de error con un título y mensaje
     * personalizados.
     *
     * @param title Título de la ventana del mensaje. Este parámetro define el
     * encabezado de la ventana de error.
     * @param message Mensaje de error que se desea mostrar. El mensaje describe
     * el problema que el usuario debe corregir.
     */
    private static void createError(String title, String message) {
        JOptionPane.showMessageDialog(
                null,
                message,
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }
}
