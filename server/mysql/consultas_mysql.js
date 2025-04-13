import { crearConexionMysql } from "./conexion_mysql.js";

/**
 * Función que ejecuta consulta a la base de datos.
 * 
 * @param {string} sql String que contiene la consulta a ejecutar.
 * @param {Array} parametros Contiene un array con los parámetros de la consulta.
 * @returns Devuelve un array con la información de la consulta.
 */
export async function realizarConsulta(sql, parametros) {
    const conexion = await crearConexionMysql(); // Crear la conexión

    try {
        // Ejecutar consulta desestructurar el resultado y no enviar la ColumnDefinition
        const [resultado, fields] = await conexion.query(sql, parametros);
        console.log("Resultados de la consulta:", resultado);
        return resultado;
    } catch (error) {
        console.error("Error al realizar la consulta:", error);
    } finally {
        // Cerrar la conexión
        await conexion.end();
        console.log("Conexión a MySQL cerrada");
    }
}
