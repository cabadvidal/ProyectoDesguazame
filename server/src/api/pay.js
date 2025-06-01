import { logger } from "../log/log.js";
import { realizarConsulta } from "../../mysql/consultas_mysql.js";
import { generarFactura } from "../factura/factura.js";
import { verificarToken, obtenerFCM } from "../sockets/auth/credenciales.js";
import { enviarMensaje } from "../sockets/auth/fcm.js";

/**
 * Realiza el proceso completo de pago de una factura:
 * 1. Verifica el token del cliente.
 * 2. Inserta una nueva factura en la base de datos.
 * 3. Inserta líneas de factura para cada pieza comprada.
 * 4. Actualiza el precio real de cada línea de factura mediante un procedimiento almacenado.
 * 5. Marca las piezas como vendidas.
 * 6. Genera la factura final y emite un mensaje por socket al cliente.
 * 
 * @async
 * @function realizarPago
 * @param {Object} datos - Objeto que contiene los datos del pago.
 * @param {number} datos.BASE - Base imponible de la factura.
 * @param {number} datos.IVA - IVA aplicado a la factura.
 * @param {string} datos.TIPO_PAGO - Tipo de pago (por ejemplo: "Tarjeta", "Efectivo").
 * @param {string} datos.TOKEN - Token del cliente autenticado.
 * @param {Array<Object>} datos.PIEZAS - Array de objetos que representan las piezas compradas.
 * @param {number} datos.PIEZAS[].ID_PIEZA - ID de la pieza comprada.
 * @param {Socket} socket - Instancia del socket que representa al cliente.
 * 
 * @returns {void}
 * 
 * @emits socket.emit("pago", {valido: true | false}) - Cuando el pago se realiza correctamente true sino false.
 * 
 * @throws Error - Si hay fallos en la base de datos, en el token o en la lógica de inserción.
 */

export async function realizarPago(datos, socket) {
    const SQLFactura = "INSERT INTO FACTURA (BASE, IVA, TIPO_PAGO, FECHA, CLIENTE_FK) VALUES(?, ?, ?, NOW(), ?)";
    const SQLLineaFactura = "INSERT INTO LINEA_FACTURA (PIEZAS_FK, FACTURA_FK, PRECIO) VALUES (?, ?, 1.0)";
    const procedimientoActualizarLinea = "CALL actualizar_precio_linea_factura(?)";
    const SQLActualizarVendido = "UPDATE PIEZAS SET VENDIDO = TRUE WHERE ID_PIEZAS = ?";

    const { BASE, IVA, TIPO_PAGO, TOKEN } = datos;
    if (!verificarToken(TOKEN)) {
        logger.error("❌ Token no válido: " + TOKEN)
        socket.emit("pago", { valido: false });
        return;
    }

    try {
        let CLIENTE_FK = null;
        global.usuariosConectados.forEach(usr => {
            if (usr.token === TOKEN) CLIENTE_FK = usr.id;
        });

        if (CLIENTE_FK === null) {
            socket.emit("pago", { valido: false });
            return;
        }
        // FUNCIONA HASTA AQUÍ
        const resultadoFactura = await realizarConsulta(SQLFactura, [BASE, IVA, TIPO_PAGO, CLIENTE_FK]);

        if (resultadoFactura && resultadoFactura.insertId) {
            const ID_FACTURA = resultadoFactura.insertId;
            const PIEZAS = datos.PIEZAS;
            for (const pieza of PIEZAS) {
                const resultadoLinea = await realizarConsulta(SQLLineaFactura, [pieza.ID_PIEZA, ID_FACTURA]);

                if (resultadoLinea) {
                    const ID_LINEA = resultadoLinea.insertId;
                    await realizarConsulta(procedimientoActualizarLinea, [ID_LINEA]);
                    await realizarConsulta(SQLActualizarVendido, [pieza.ID_PIEZA]);
                }
            }
            generarFactura(ID_FACTURA);
            socket.emit("pago", { valido: true, ID_FACTURA: ID_FACTURA });
        } else {
            socket.emit("pago", { valido: false });
        }
        console.log(`Compra realizada`);
        const tokenFcm = obtenerFCM();
        if (tokenFcm && tokenFcm !== null) {
            setTimeout(() => {
                enviarMensaje("Has realizado una compra", `Pedido tramitado. Muchas gracisa por su compra`, tokenFcm);
            }, 1200000); // 20 min
        }

    } catch (error) {
        console.log("error al realizar pago src/api/pay.js " + error);
        socket.emit("pago", { valido: false });
    }
}