import express from "express";
import cors from "cors";
import path from "path";
import { fileURLToPath } from "url";
import { createServer } from "http";
import { Server } from "socket.io";
import { configServerIO } from "./src/sockets/sockets.js";
import { format, transports } from "winston";
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

export { app }
