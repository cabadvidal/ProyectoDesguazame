import { comprobarCredenciales, deslogueUsuario, eliminarUsuario, verificarToken } from "./auth/credenciales.js";
import { logger } from "../log/log.js";

// Array que almacena los usuarios conectados
var usuariosConectados = [];

/**
 * Configura los eventos de Socket.io
 * 
 * @param {Server} io - Instancia del servidor de Socket.io
 */
export function configServerIO(io) {
    io.on('connection', (socket) => {
        console.log('✅ Cliente conectado con ID:', socket.id);

        // Evento para autentificar al usuario
        socket.on('comprobar credenciales', async (datos) => {
            logger.debug(`src/sockets/sockets.js los datos recibidos ${datos}`);
            await comprobarCredenciales(socket, datos);
        });

        // Evento para reconectar con token
        socket.on('reconexion', async (token) => {
            if (verificarToken(token)) {
                const index = usuariosConectados.findIndex((user) => user.token === token);
                const user = usuariosConectados[index];
                user.socket = socket;
                user.resetearTimeout();
                user.setSocket(socket);
                console.warn(`⚠️ El usuario: ${user.user} se ha reconectado con el token: ${token}.`);
                socket.emit("respuesta verificación", { valido: true });
            } else {
                console.warn(`⚠️ El usuario con el token: ${token} ha intentado conectarse al servidor token no válido.`);
                socket.emit("respuesta verificación", { valido: false });
            }
        });

        socket.on('actividad', async (datos) => {
            const {token, actividad} = datos;
            if (verificarToken(token)) {
                const index = usuariosConectados.findIndex((user) => user.token === token);
                if (index !== -1) {
                    const user = usuariosConectados[index];
                    user.resetearTimeout();
                    user.setSocket(socket);
                    console.log(`El usuario: ${user.user}, realiza la actividad ${actividad} con el token: ${token}`);
                    socket.emit('respuesta actividad', { valido: true });
                } else {
                    console.error(`No hay ningún usuario conectado con el token: ${token}`);
                    socket.emit('respuesta actividad', { valido: false });
                }
            }
        });

        socket.on('desconectar', async(token) => {
            const index = usuariosConectados.findIndex((user) => user.socket === socket);
            if (index !== -1) {
                const user = usuariosConectados[index];
                console.log(`🔴 Usuario ${user.user} desconectado. Eliminando...`);
                eliminarUsuario(user.token);
            } else {
                console.warn("⚠️ Usuario desconectado sin registro.");
            }
        });
    });


}

export { usuariosConectados };