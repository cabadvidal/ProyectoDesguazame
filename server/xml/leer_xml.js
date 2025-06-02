import fs from 'fs';
import path from 'path';
import { fileURLToPath } from "url";
import { parseStringPromise } from 'xml2js';

// Obtener la ruta absoluta al archivo XML
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const rutaArchivo = path.resolve(__dirname, 'conf.xml');

/**
 * Lee los parámetros de configuración del fichero 'XML' y devuelve los datos según la petición.
 * @param {string} opcion Contiene la opción de los parámetros a devolver ('mysql', 'mongo').
 * @returns {object} Datos correspondientes a la opción solicitada.
 */
export async function cargarParametros(opcion) {
    try {
        // Leer el archivo XML
        const xmlData = fs.readFileSync(rutaArchivo, 'utf-8');

        // Convertir XML a objeto JavaScript
        const resultado = await parseStringPromise(xmlData);

        // Acceder a los valores
        const mysql = {
            direccion: resultado.conf.mysql[0].direccion[0],
            usuario: resultado.conf.mysql[0].usuario[0],
            clave: resultado.conf.mysql[0].clave[0],
            bbdd: resultado.conf.mysql[0].bbdd[0],
            puerto: resultado.conf.mysql[0].puerto[0],
        };

        const firebase = {
            apiKey: resultado.conf.firebase[0].apiKey[0],
            authDomain: resultado.conf.firebase[0].authDomain[0],
            projectId: resultado.conf.firebase[0].projectId[0],
            storageBucket: resultado.conf.firebase[0].storageBucket[0],
            messagingSenderId: resultado.conf.firebase[0].messagingSenderId[0],
            appId: resultado.conf.firebase[0].appId[0],
            measurementId: resultado.conf.firebase[0].measurementId[0]
        };

        // Usar las variables según sea necesario
        switch(opcion) {
            case 'mysql':
                console.log('MySQL Configuración: cargada');
                return mysql;

            case 'firebase':
                console.log('Firebase Configuración: cargada');
                return firebase;
        }  
    } catch (error) {
        console.error('Error al cargar el archivo XML:', error);
    }
}

