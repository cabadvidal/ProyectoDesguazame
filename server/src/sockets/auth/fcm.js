import { admin } from "../fire/firebase-config.js";

/**
 * Envía una notificación push a un cliente móvil mediante Firebase Cloud Messaging (FCM).
 *
 * @async
 * @function enviarMensaje
 * @param {string} titulo - Título de la notificación que se mostrará en el dispositivo.
 * @param {string} mensaje - Cuerpo de la notificación (contenido del mensaje).
 * @param {string} token - Token FCM del dispositivo al que se desea enviar el mensaje.
 * 
 * @returns {Promise<void>} Promesa que se resuelve cuando el mensaje ha sido enviado o muestra un error.
 * 
 * @example
 * await enviarMensaje("Compra confirmada", "Tu pago ha sido procesado con éxito", fcmToken);
 */
export async function enviarMensaje(titulo, mensaje, token) {
    admin.messaging().send({
        token: token,
        notification: {
            title: titulo,
            body: mensaje
        }
    }).then(res => {
        console.log("✅ Notificación enviada:", res);
    }).catch(err => {
        console.error("❌ Error al enviar notificación:", err);
    });
}