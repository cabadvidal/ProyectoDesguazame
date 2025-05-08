/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desguazame.desguazame_escritorio.model;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Clase encargada de verificar la validez de distintos datos según su tipo y
 * contexto.
 */
public class DataVerify {

    /**
     * Verifica el valor de un dato según su tipo y la tabla de origen.
     *
     * @param tipo Tipo de dato (clave).
     * @param valor Valor del dato a verificar.
     * @return {@code true} si el dato es válido, {@code false} en caso
     * contrario.
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
            case "DNI_CIF":
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
     *
     * @param dato El texto a verificar.
     * @return {@code true} si el texto es válido, {@code false} en caso
     * contrario.
     */
    private static boolean verificarString(String dato) {
        return dato.trim().matches("^[A-Za-zÁÉÍÓÚÜáéíóúüÑñ\\s]+$");
    }

    /**
     * Verifica si un número corresponde a un teléfono fijo nacional válido de 9
     * cifras y con prefijo válido.
     *
     * @param dato El número de teléfono a verificar.
     * @return {@code true} si el número es válido, {@code false} en caso
     * contrario.
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
     *
     * @param dato El número de móvil a verificar.
     * @return {@code true} si el número es válido, {@code false} en caso
     * contrario.
     */
    private static boolean verificarMovil(String dato) {
        return dato.matches("^\\d{9}$") && (dato.startsWith("6") || dato.startsWith("7"));
    }

    /**
     * Verifica si un número de cuenta bancaria (IBAN español) es válido.
     *
     * @param dato Número IBAN a verificar.
     * @return {@code true} si el número es válido, {@code false} en caso
     * contrario.
     */
    private static boolean verificarNumeroCuenta(String dato) {
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
     *
     * @param dato Dirección de correo electrónico.
     * @return {@code true} si el formato es correcto, {@code false} en caso
     * contrario.
     */
    private static boolean verificarEmail(String dato) {
        return Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", dato.trim());
    }

    /**
     * Verifica si un código postal español es válido (del 01000 al 52999).
     *
     * @param dato Código postal a verificar.
     * @return {@code true} si el código postal es válido, {@code false} en caso
     * contrario.
     */
    private static boolean verificarCodigoPostal(String dato) {
        return Pattern.matches("^(?:0?[1-9]|[1-4]\\d|5[0-2])\\d{3}$", dato.trim());
    }

}
