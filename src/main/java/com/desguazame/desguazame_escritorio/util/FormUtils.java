/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desguazame.desguazame_escritorio.util;

import java.util.HashMap;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Clase de utilidades para leer campos de formularios JavaFX contenidos en estructuras VBox y HBox.
 */
public class FormUtils {

    /**
     * Extrae los valores de todos los {@link TextField}, {@link PasswordField}, {@link ComboBox} y {@link CheckBox}
     * contenidos directa o recursivamente dentro de un contenedor {@link HBox}, asumiendo que contiene {@link VBox}.
     *
     * @param hBox El contenedor HBox raíz desde el cual leer los campos.
     * @return Un {@link HashMap} que mapea los IDs de los nodos a sus valores de texto.
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
     * Extrae los valores de todos los {@link TextField}, {@link PasswordField}, {@link ComboBox} y {@link CheckBox}
     * contenidos directa o recursivamente dentro de un contenedor {@link VBox} o anidados en otros contenedores VBox/HBox.
     *
     * @param container El contenedor VBox raíz desde el cual leer los campos.
     * @return Un {@link HashMap} que mapea los IDs de los nodos a sus valores de texto.
     */
    public static HashMap<String, String> readNodeFields(VBox container) {
        HashMap<String, String> campos = new HashMap<>();

        for (Node node : container.getChildren()) {
            if (node instanceof TextField) {
                TextField textField = (TextField) node;
                putIfValid(campos, textField.getId(), textField.getText());
            } else if (node instanceof PasswordField) {
                PasswordField passwordField = (PasswordField) node;
                putIfValid(campos, passwordField.getId(), passwordField.getText());
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
     *
     * @param map   Mapa donde insertar la entrada.
     * @param id    ID del nodo.
     * @param value Valor del nodo.
     */
    private static void putIfValid(HashMap<String, String> map, String id, String value) {
        if (id != null && !id.isEmpty()) {
            map.put(id, value);
        }
    }
}