import express from "express";
import path from "path";
import { fileURLToPath } from "url";
import { createServer } from "https";
import { createServer as createHttpServer } from "http";
/*import fs from "fs";  // Necesario para leer los archivos del certificado
import {
    guardarFacturas, guardarPedido, ejecutarConsultas,
    modificarPedido, eliminarTodoDeColeccion, buscarDatosRecaudacion
} from "./mongodb/consultas_mongodb.js"; */
import { realizarConsulta } from "./src/api/mysql/consultas_mysql.js";
//import { ObjectId } from 'mongodb';

// Obtener la ruta del directorio actual en ESM
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();
const puertoHTTPS = 10010;
const puertoHTTP = 10009; 

// Crear servidor HTTP
const serverHTTP = createHttpServer(app);

// Sirve archivos estáticos desde el directorio 'public' 'fichar'
app.use(express.static(path.join(__dirname, "public")));

// Iniciar el servidor HTTP y redirigir a HTTPS
serverHTTP.listen(puertoHTTP, () => {
    console.log(`Servidor HTTP escuchando en el puerto ${puertoHTTP}`);
});

//Recibe el json y lo interpreta correctamente
app.use(express.json());

// Obtiene el número de mesas
app.get("mysql/obtenerDatos", async (req, res) => {
    try {
        // Consulta que obtiene el número de mesas
        const sql = 'SELECT * FROM `USUARIOS`';

        // Realiza la consulta a 'MySQL' 
        const resultado = await realizarConsulta(sql);
        console.log('Número de mesas: ' + resultado);
        // Devuelve el resultado en formato json
        res.status(200).json({ data: resultado });
    } catch (error) {
        res.status(200).json({ data: error });
        console.log('Error al consultar comidas: ' + error);
    }
});