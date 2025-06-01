/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desguazame.desguazame_escritorio.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
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
 * boolean esValido = DataVerifyProcess.verificarDato("TELEFONO", "123456789");
 * }</pre>
 *
 * @author Charlie
 */
public class DataVerifyProcess {

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
        try {

            ProcessBuilder builder = new ProcessBuilder("java", "-jar", "process/DesguazameProceso.jar", tipo, valor);

            Process proceso = builder.start();

             // Leer la salida estándar del proceso
            BufferedReader leerSalida = new BufferedReader(
                    new InputStreamReader(proceso.getInputStream()));

            StringBuilder capturaSalida = new StringBuilder();
            String linea;

            // Leer la salida estándar
            while ((linea = leerSalida.readLine()) != null) {
                capturaSalida.append(linea).append("\n");
            }
            
            System.out.println("" + capturaSalida);
            // Comprobamos la salida del programa
            int exitCode = proceso.waitFor();

            if (exitCode == 1) {
                // Dato erroneo
                return false;
            } else if (exitCode == 0) {
                // Dato correcto
                return true;
            }
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        return false;
    }
}
