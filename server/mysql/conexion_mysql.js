import {cargarParametros} from "../xml/leer_xml.js";
import mysql from "mysql2/promise";

const datosMysql = await cargarParametros('mysql');

console.log('Parámetros de mysql:' + datosMysql);

/**
 * Crea una conexión a la base de datos MySQL utilizando los parámetros de configuración.
 * 
 * @returns {Promise<mysql.Connection>} Objeto de conexión a MySQL.
 */
export async function crearConexionMysql() {
    try {
         // Extraer parámetros
         const { direccion, usuario, clave, bbdd, puerto } = datosMysql;

         // Crear conexión con mysql2
         const conexion = await mysql.createConnection({
             host: direccion,
             user: usuario,
             password: clave,
             database: bbdd,
             port: parseInt(puerto, 10)
         });

         console.log("Conexión exitosa a MySQL");
         return conexion;
    } catch (error) {
        console.log('Error de conexion a MySQL: ' + error);
    }
}