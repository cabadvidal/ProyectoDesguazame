import { auth, admin } from '../fire/firebase-config.js';
import { createUserWithEmailAndPassword } from "firebase/auth";

/**
 * Registra un nuevo usuario en Firebase Authentication usando el cliente (auth).
 * @param {string} email - Correo electrónico del nuevo usuario.
 * @param {string} password - Contraseña del nuevo usuario.
 * @returns {Promise<string|null>} UID del nuevo usuario si se registra correctamente, o `null` si ocurre un error.
 */
export async function registrarUsuario(email, password) {
  try {
    const userCredential = await createUserWithEmailAndPassword(auth, email, password);
    const user = userCredential.user;
    console.log("User registered successfully:", user.uid);
    return user.uid;
  } catch (error) {
    console.error("Error registering user:", error.code, error.message);
    return null; // Devuelve null si ocurre un error
  }
}

/**
 * Elimina un usuario de Firebase Authentication usando el Admin SDK.
 * @param {string} FIREBASE_UID - UID del usuario que se desea eliminar.
 * @returns {Promise<boolean>} `true` si se elimina correctamente, `false` si ocurre un error.
 */
export async function eliminarUsuario(FIREBASE_UID) {
  try {
    // Obtener el usuario por su UID usando Admin SDK
    const userRecord = await admin.auth().getUser(FIREBASE_UID);

    // Eliminar el usuario de Firebase Authentication
    await admin.auth().deleteUser(FIREBASE_UID);

    console.log(`✅ Usuario con UID ${userRecord.uid} eliminado correctamente de Firebase.`);
    return true;
  } catch (error) {
    console.error(`Error al eliminar el usuario: ${error.code} - ${error.message}`);
    return false;
  }
}

/**
 * Modifica la contraseña de un usuario en Firebase Authentication usando el Admin SDK.
 * @param {string} FIREBASE_UID - UID del usuario cuya contraseña se va a modificar.
 * @param {string} nuevaContrasena - Nueva contraseña que se desea asignar.
 * @returns {Promise<boolean>} `true` si se actualiza correctamente, `false` si ocurre un error.
 */
export async function modificarContrasena(FIREBASE_UID, nuevaContrasena) {
  try {
    await admin.auth().updateUser(FIREBASE_UID, {
      password: nuevaContrasena
    });

    console.log(`✅ Contraseña actualizada correctamente para el UID: ${FIREBASE_UID}`);
    return true;
  } catch (error) {
    console.error(`❌ Error al actualizar la contraseña: ${error.code} - ${error.message}`);
    return false;
  }
}

