import express from "express";
import cors from "cors";
import path from "path";
import multer from 'multer';
import fs from 'fs';
import { fileURLToPath } from "url";
import { createServer } from "http";
import { Server } from "socket.io";
import { configServerIO } from "./src/sockets/sockets.js";
import { logger } from "./src/log/log.js";


const app = express();
const server = createServer(app);
const puerto = 10010;

// Obtener la ruta del directorio actual en ESM
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// Habilita CORS en todas las rutas HTTP
app.use(cors());

// Habilita leer JSON en el cuerpo de la petición
app.use(express.json()); 

// Sirve archivos estáticos desde el directorio 'public' 'web'
app.use(express.static(path.join(__dirname, "public", "web")));

// Configurar socket.io
const io = new Server(server, {
    cors: {
        origin: "*",  // Permite conexiones de cualquier origen
        methods: ["GET", "POST"] // Define los métodos HTTP permitidos en CORS
    }
});

configServerIO(io);

// Asegura compatibilidad con __dirname
const carpetaDestino = path.join(__dirname, 'public', 'web', 'img', 'piezas');

// Crear carpeta si no existe
if (!fs.existsSync(carpetaDestino)) {
    fs.mkdirSync(carpetaDestino, { recursive: true });
}

// Configuración de almacenamiento con nombre único
const storage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, carpetaDestino);
    },
    filename: function (req, file, cb) {
        const ext = path.extname(file.originalname);
        const nombreUnico = `${Date.now()}-${Math.round(Math.random() * 1e9)}${ext}`;
        cb(null, nombreUnico);
    }
});

// Exportar el middleware upload para usarlo en rutas
const upload = multer({ storage });

// Iniciar el servidor
server.listen(puerto, '0.0.0.0', () => {
    logger.info(`Servidor UP, UP, UP puerto escucha: ${puerto}`);

    // Importar 'web_crud.js' después de que el servidor esté levantado
    import("./src/api/web_crud.js").then(() => {
        logger.info('Rutas cargadas correctamente');
    }).catch(error => {
        logger.error("Error al cargar las rutas", error);
    });
});

export { app, upload }
