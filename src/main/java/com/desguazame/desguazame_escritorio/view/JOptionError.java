/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desguazame.desguazame_escritorio.view;

import javax.swing.JOptionPane;

/**
 * Clase utilitaria para mostrar mensajes de error mediante JOptionPane.
 */
public class JOptionError extends JOptionPane {

     /**
     * Muestra un mensaje de error contextualizado según la clave y el valor.
     *
     * @param clave Clave que representa el campo o tipo de error.
     * @param valor Valor asociado o detalle del error.
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
     * @param title Título de la ventana del mensaje.
     * @param message Mensaje de error que se desea mostrar.
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
