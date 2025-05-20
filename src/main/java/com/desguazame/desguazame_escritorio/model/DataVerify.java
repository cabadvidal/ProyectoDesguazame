/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desguazame.desguazame_escritorio.model;

import java.util.*;
import java.util.regex.*;

/**
 * Clase encargada de verificar la validez de distintos datos según su tipo y
 * contexto.
 * <p>
 * Esta clase proporciona una serie de métodos estáticos para validar distintos
 * tipos de datos, como nombres, apellidos, números de teléfono, direcciones de
 * correo electrónico, entre otros. Se usa principalmente para realizar
 * validaciones en formularios o entradas de datos.</p>
 *
 * <p>
 * Los métodos de esta clase permiten verificar si un dato cumple con los
 * formatos esperados para varios campos comunes en aplicaciones de registro o
 * gestión de información de usuarios.</p>
 *
 * <p>
 * Ejemplo de uso:</p>
 * <pre>{@code
 * boolean esValido = DataVerify.verificarDato("TELEFONO", "123456789");
 * }</pre>
 *
 * @author Charlie
 */
public class DataVerify {

    /**
     * Verifica el valor de un dato según su tipo y la tabla de origen.
     * <p>
     * Este método evalúa el tipo de dato proporcionado (como "NOMBRE",
     * "TELEFONO", "MAIL") y llama al método correspondiente para realizar la
     * validación del valor proporcionado.</p>
     *
     * @param tipo Tipo de dato (clave). Ejemplos: "NOMBRE", "APELLIDO_A",
     * "DNI".
     * @param valor Valor del dato a verificar. Este parámetro debe ser un valor
     * que corresponda al tipo indicado.
     * @return {@code true} si el dato es válido según su tipo, {@code false} en
     * caso contrario.
     */
    public static boolean verificarDato(String tipo, String valor) {
        tipo = tipo.trim().toUpperCase();
        switch (tipo) {
            case "NOMBRE":
            case "APELLIDO_A":
            case "APELLIDO_B":
            case "MUNICIPIO":
            case "CIUDAD":
                return verificarString(valor);
            case "TELEFONO":
                return verificarTelefono(valor.trim());
            case "MOVIL":
                return verificarMovil(valor.trim());
            case "NUMERO_CUENTA":
                return verificarNumeroCuenta(valor.trim());
            case "DNI":
                return verificarDNI(valor.trim());
            case "MAIL":
                return verificarEmail(valor.trim());
            case "CODIGO_POSTAL":
                return verificarCodigoPostal(valor.trim());
            default:
                return true;
        }
    }

    /**
     * Verifica si un texto contiene solo letras (mayúsculas, minúsculas,
     * acentuadas o ñ) y espacios.
     * <p>
     * Este método comprueba que el texto ingresado esté formado únicamente por
     * caracteres alfabéticos y espacios, permitiendo así validar nombres,
     * apellidos y otros campos similares.</p>
     *
     * @param dato El texto a verificar. Se espera que este texto sea una cadena
     * que contenga solo letras y espacios.
     * @return {@code true} si el texto es válido (solo letras y espacios),
     * {@code false} en caso contrario.
     */
    private static boolean verificarString(String dato) {
        return dato.trim().matches("^[A-Za-zÁÉÍÓÚÜáéíóúüÑñ\\s]+$");
    }

    /**
     * Verifica si un número corresponde a un teléfono fijo nacional válido de 9
     * cifras y con prefijo válido.
     * <p>
     * Este método valida si el número de teléfono corresponde a un formato
     * correcto, considerando los prefijos válidos para teléfonos fijos en
     * España.</p>
     *
     * @param dato El número de teléfono a verificar. Se espera que este número
     * sea una cadena numérica de 9 cifras.
     * @return {@code true} si el número de teléfono es válido, {@code false} en
     * caso contrario.
     */
    private static boolean verificarTelefono(String dato) {
        if (!dato.matches("^\\d{9}$")) {
            return false;
        }
        String prefijo3 = dato.substring(0, 3);
        String prefijo2 = dato.substring(0, 2);
        Set<String> prefijos3 = Set.of("945", "967", "965", "966", "950", "984", "985", "920", "924", "971", "947", "927", "956", "942", "964", "926", "957", "981", "969", "972", "958", "949", "943", "959", "974", "953", "987", "973", "982", "951", "952", "968", "948", "988", "979", "928", "986", "941", "923", "921", "954", "955", "975", "977", "922", "978", "925", "960", "961", "962", "963", "983", "944", "946", "980", "976");
        Set<String> prefijos2 = Set.of("91", "93");
        return prefijos3.contains(prefijo3) || prefijos2.contains(prefijo2);
    }

    /**
     * Verifica si un número corresponde a un móvil español válido (empieza por
     * 6 o 7 y tiene 9 cifras).
     * <p>
     * Este método valida los números de teléfono móvil, asegurándose de que
     * empiecen por los dígitos 6 o 7 y contengan exactamente 9 cifras.</p>
     *
     * @param dato El número de móvil a verificar. Se espera que este número sea
     * una cadena numérica de 9 cifras.
     * @return {@code true} si el número de móvil es válido, {@code false} en
     * caso contrario.
     */
    private static boolean verificarMovil(String dato) {
        dato = dato.trim().replaceAll("\\s+", "");
        return dato.matches("^\\d{9}$") && (dato.startsWith("6") || dato.startsWith("7"));
    }

    /**
     * Verifica si un número de cuenta bancaria (IBAN español) es válido.
     * <p>
     * Este método valida el formato del número de cuenta bancaria, asegurándose
     * de que sea un IBAN español con el formato correcto y los cálculos de
     * verificación adecuados.</p>
     *
     * @param dato Número IBAN a verificar. Se espera que el IBAN esté compuesto
     * por 24 caracteres.
     * @return {@code true} si el número de cuenta es válido, {@code false} en
     * caso contrario.
     */
    private static boolean verificarNumeroCuenta(String dato) {
        dato = dato.replace(" ", "");
        dato = dato.trim();
        if (dato.length() != 24) {
            return false;
        }

        String iban = dato.substring(0, 2);
        String numeroIban = dato.substring(2, 4);
        String numeroCuenta = dato.substring(4);

        StringBuilder valorIban = new StringBuilder();
        for (char c : iban.toCharArray()) {
            if (Character.isLetter(c)) {
                valorIban.append((int) c - 55); // A=10, B=11...
            } else {
                return false;
            }
        }

        String numeroParaCalcular = numeroCuenta + valorIban + numeroIban;
        try {
            return new java.math.BigInteger(numeroParaCalcular).mod(java.math.BigInteger.valueOf(97)).intValue() == 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Verifica si un DNI español es válido.
     * <p>
     * Este método comprueba que el formato del DNI sea válido, asegurándose de
     * que esté compuesto por 7 u 8 cifras seguidas de una letra, y que la letra
     * coincida con el cálculo basado en el número.</p>
     *
     * @param dato DNI a verificar (7 u 8 cifras seguidas de una letra).
     * @return {@code true} si el DNI es correcto, {@code false} en caso
     * contrario.
     */
    private static boolean verificarDNI(String dato) {
        dato = dato.trim().toUpperCase();
        if (!dato.matches("^\\d{7,8}[A-Z]$")) {
            return false;
        }
        // Extraer número y letra
        int numero;
        try {
            numero = Integer.parseInt(dato.substring(0, dato.length() - 1));
        } catch (NumberFormatException e) {
            return false;
        }

        char letra = dato.charAt(dato.length() - 1);
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";

        // Calcular letra esperada
        char letraEsperada = letras.charAt(numero % 23);

        return letra == letraEsperada;
    }

    /**
     * Verifica si una dirección de correo electrónico tiene un formato válido.
     * <p>
     * Este método valida si la dirección de correo electrónico cumple con un
     * formato estándar de correo electrónico.</p>
     *
     * @param dato Dirección de correo electrónico.
     * @return {@code true} si el formato del correo electrónico es válido,
     * {@code false} en caso contrario.
     */
    private static boolean verificarEmail(String dato) {
        return Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", dato.trim());
    }

    /**
     * Verifica si un código postal español es válido (del 01000 al 52999).
     * <p>
     * Este método valida que el código postal esté dentro del rango válido para
     * códigos postales españoles.</p>
     *
     * @param dato Código postal a verificar.
     * @return {@code true} si el código postal es válido, {@code false} en caso
     * contrario.
     */
    private static boolean verificarCodigoPostal(String dato) {
        return Pattern.matches("^(?:0?[1-9]|[1-4]\\d|5[0-2])\\d{3}$", dato.trim());
    }

}
