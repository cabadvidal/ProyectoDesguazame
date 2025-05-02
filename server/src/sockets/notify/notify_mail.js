import nodemailer from 'nodemailer';

// Configuración del transporter para Nodemailer (Correo de salida)
const transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: 'cabadvidal@gmail.com', // Correo desde el que se envía el email.
        pass: 'fjsn ctxg djtq ijlq'  // Contraseña de la aplicación de gmail
    }
});

/**
 * Función que envía un mail de notificación.
 * @param {string} email - Contiene el email al que enviar el mensaje.
 * @param {string} message - Contiene el mensaje a enviar por mail.
 */
export function enviarMail(email, message) {
    if (email === '') {
        email = 'carlos.abad.vidal@colexio-karbo.com';
    }
    // Configurar el correo de notificación
    const mailOptions = {
        from: 'desguazame-noreply@gmail.com',
        to: email,
        subject: 'Notificación de cambio de contraseña',
        text: message
    };

    // Enviar el correo de notificación
    transporter.sendMail(mailOptions, (error, info) => {
        if (error) {
            console.error('Error al enviar el correo:', error);
            return false;
        } else {
            console.log(' src/sockets/notify Correo enviado:', info.response);
            return true;
        }
    });
}