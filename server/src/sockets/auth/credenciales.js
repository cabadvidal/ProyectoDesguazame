import { authUsuarioFire, generarToken } from "./auth_user.js";
import { registrarUsuario } from "./reg_user.js";
import { realizarConsulta } from "../../../mysql/consultas_mysql.js";
import { usuariosConectados } from "../sockets.js";
import { logger } from "../../log/log.js";

// Clase para almacenar los datos de los usuarios que se conectan
class Usuario {
    constructor(user, pass, FIREBASE_UID, token, socket, tipo_cuenta, id) {
        this.user = typeof user === 'string' ? user : "Desconocido";
        this.pass = typeof user === 'string' ? pass : "Desconocido";
        this.FIREBASE_UID = FIREBASE_UID;
        this.token = token;
        this.socket = socket;
        this.tipo_cuenta = tipo_cuenta;
        this.id = id;
        this.timeout = this.iniciarTimeout();
    }

    setSocket(socket) {
        this.socket = socket;
    }

    /**
     * Inicia un temporizador para desconectar al usuario después de 15 minutos de inactividad.
     */
    iniciarTimeout() {
        return setTimeout(() => {
            deslogueUsuario(this.token);
        }, 900000); // 15 minutos
    }

    /**
     * Resetea el temporizador cuando el usuario realiza una acción.
     */
    resetearTimeout() {
        clearTimeout(this.timeout); // Elimina el timeout anterior
        this.timeout = this.iniciarTimeout(); // Reinicia el timeout
        console.log(`⏳ Temporizador reiniciado para ${this.user}`);
    }
}

/**
 * Función para comprobar las credenciales del usuario.
 * @param {object} socket - El socket de conexión.
 * @param {object} datos - Los datos de usuario y contraseña.
 */
export async function comprobarCredenciales(socket, datos) {
    try {
        const { usuario, password } = datos;

        logger.debug(`src/sockets/auth/credenciales.js comprobarCredenciales() || Usuario: ${usuario} | Contraseña: ${password}`);

        // Consulta que obtiene el número de mesas
        const sql = 'SELECT * FROM `DATOS_COMUNES` WHERE MAIL = ? AND CONTRASENA = SHA2(?, 256)';

        // Realiza la consulta a 'MySQL' 
        const resultado = await realizarConsulta(sql, [usuario, password]);

        if (!resultado) {
            socket.emit('token', null);
            return;
        }

        const { ID_DATOS_COMUNES, NOMBRE, APELLIDO_A, APELLIDO_B, FIREBASE_UID } = resultado[0];
        logger.debug(`src/sockets/auth/credenciales.js comprobarCredenciales() || FIREBASE_UID en MySQL: ${resultado[0].FIREBASE_UID}`);

        const sqlTipoUsuario = `SELECT 'EMPLEADO' AS TIPO_USUARIO, ID_EMPLEADOS AS ID, NUMERO_SS AS CAMPO1, TIPO_CUENTA AS CAMPO2 
                                    FROM EMPLEADOS 
                                    WHERE DATOS_COMUNES_FK = ? 
                                UNION 
                                SELECT 'CLIENTE' AS TIPO_USUARIO, ID_CLIENTE AS ID, DATOS_TARJETA AS CAMPO1, NULL AS CAMPO2 
                                    FROM CLIENTE 
                                    WHERE DATOS_COMUNES_FK = ?
                                UNION 
                                SELECT 'VENDEDOR' AS TIPO_USUARIO, ID_VENDEDOR AS ID, NULL AS CAMPO1, NULL AS CAMPO2 
                                    FROM VENDEDOR 
                                    WHERE DATOS_COMUNES_FK = ?;`;

        const resultado_tipo_usuario = await realizarConsulta(sqlTipoUsuario, [ID_DATOS_COMUNES, ID_DATOS_COMUNES, ID_DATOS_COMUNES]);

        const { TIPO_USUARIO, ID, CAMPO1, CAMPO2 } = resultado_tipo_usuario[0];

        const uidFirebase = await authUsuarioFire(usuario, password);

        if (uidFirebase === FIREBASE_UID) {
            // Crear el nuevo usuario y agregarlo al array
            let token = generarToken();
            while (verificarToken(token)) {
                token = generarToken();
            }

            logger.debug(`src/sockets/auth/credenciales.js comprobarCredenciales() || El token es: ${token} para el Usuario; ${usuario}`);
            const apellidos = (APELLIDO_A + " " + APELLIDO_B).trim(); // Elimina espacios extra si un apellido es vacío
            let user = null;
            switch (TIPO_USUARIO) {
                case 'EMPLEADO': {
                    user = new Usuario(usuario, password, uidFirebase, token, socket, CAMPO2, ID);
                    logger.debug(`src/sockets/auth/credenciales.js comprobarCredenciales() || TIPO DE CUENTA en MySQL: ${CAMPO2}`);
                    socket.emit('token_empleado', { 'token': token, 'nombre': NOMBRE, 'apellidos': apellidos, 'tipo_cuenta': CAMPO2 });
                    break;
                }
                case 'CLIENTE': {
                    user = new Usuario(usuario, password, uidFirebase, token, socket, null, ID);
                    socket.emit('token_cliente', { 'token': token, 'nombre': NOMBRE, 'apellidos': apellidos });
                    break;
                }
            }
            usuariosConectados.push(user);
            console.log(`✅ Usuario: ${usuario} logueado correctamente. Tipo de cuenta: ${TIPO_USUARIO}.`);
        } else {
            // Enviar null si no se encuentra
            socket.emit('token', null);
        }


    } catch (error) {
        console.error("❌ Error al comprobar el código:", error);
        socket.emit('token', null);
    }
}

/**
 * Gestión del registro de usuarios.
 * @param {object} socket - El socket de conexión.
 * @param {object} datos - Los datos de registro.
 */
export async function registroUsuario(socket, datos) {
    try {
        const { mail, pass, nombre, apellido_a, apellido_b, direccion, codigo_postal,
            ciudad, municipio, movil, dni } = datos;
        const sqlMail = `SELECT NOMBRE FROM DATOS_COMUNES WHERE MAIL = ?`;
        const resultadoMail = await realizarConsulta(sqlMail, [mail]);
        if (resultadoMail[0].NOMBRE) {
            logger.debug(`src/sockets/auth/credenciales.js registroUsuario() || Usuario con mail: ${mail} ya está registrado.`);
            console.warn(`⚠️ El usuario con mail ${mail} ya está registrado en el sistema.`)
            socket.emit('usr_existe');
            return;
        }
        const sqlRegistro = `INSERT INTO DATOS_COMUNES VALUES (?, ?, ?, SHA2(?, 256), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`;
        const resultado = await realizarConsulta(sqlRegistro, [nombre, apellido_a, apellido_b, pass, direccion, codigo_postal, ciudad, municipio, null, movil, mail, dni, null, null, null])
        if (resultado.insertId) {
            const ID = resultado.insertId;
            logger.debug(`src/sockets/auth/credenciales.js registroUsuario() || Usuario con mail: ${mail} registrado en MySQL.`);
            const FIREBASE_UID = registrarUsuario(mail, pass);
            const sql_UID = `UPDATE DATOS_COMUNES SET FIREBASE_UID = ? WHERE ID_DATOS_COMUNES = ?`;
            await realizarConsulta(sql_UID, [FIREBASE_UID, ID]);

            logger.debug(`src/sockets/auth/credenciales.js registroUsuario() || Usuario con mail: ${mail} actualizado con Firebase UID: ${FIREBASE_UID}.`);
            socket.emit('usr_registrado');
        }
    } catch (error) {
        console.error(`❌ Error en registroUsuario: ${error.message}`);
        socket.emit('error_registro', 'Error en el servidor');
    }
}

/**
 * Desloguea al usuario por inactividad.
 */
export async function deslogueUsuario(token) {
    const index = usuariosConectados.findIndex((user) => user.token === token);
    if (index !== -1) {
        const user = usuariosConectados[index];
        user.socket.emit("deslogueado", "Sesión expirada por inactividad.");
        clearTimeout(user.timeout);
        if (user.socket) {
            user.socket.disconnect(true);
        }
        usuariosConectados.splice(index, 1);
        console.warn(`⚠️ Usuario ${user.user} deslogueado por inactividad.`);
    }
}

/**
 * Elimina un usuario cuando se desconecta manualmente.
 */
export async function eliminarUsuario(token) {
    const index = usuariosConectados.findIndex((user) => user.token === token);
    if (index !== -1) {
        const user = usuariosConectados[index];
        clearTimeout(user.timeout);
        usuariosConectados.splice(index, 1);
        if (user.socket) {
            user.socket.disconnect(true);
        }
        console.log("🔴 Usuario desconectado manualmente.");
    }
}

/**
 * Verifica si el token generado ya existe.
 * @param {String} token Contiene el token generado para el usuario que se acaba de conectar.
 * @returns Devuelve True si el token ya existe sino false
 */
export function verificarToken(token) {
    return usuariosConectados.some(user => user.token === token);
}