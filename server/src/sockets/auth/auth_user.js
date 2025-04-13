import crypto from 'crypto';
import { auth } from '../fire/firebase-config.js'; // Asegúrate de importar la configuración de Firebase
import { signInWithEmailAndPassword } from "firebase/auth";
/**
 * Función que ejecuta consulta a la base de datos.
 * 
 * @param {string} sql - String que contiene la consulta a ejecutar.
 * @param {Array} parametros - Array con los parámetros de la consulta.
 * @returns {Array|null} Devuelve un array con la información de la consulta o `null` si hay error.
 */
export async function authUsuarioFire(email, password) {
    try {
        const userCredential = await signInWithEmailAndPassword(auth, email, password);
        return userCredential.user.uid;
    } catch (error) {
        console.error("Error autenticando con Firebase:", error.code, error.message);
        return null;
    }
}

/**
 * Función para generar un token único seguro.
 * @returns {string} Token generado.
 */
export function generarToken() {
    return crypto.randomBytes(32).toString('hex'); // Genera un token seguro de 32 bytes
}