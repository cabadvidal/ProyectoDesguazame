import { auth, admin } from '../fire/firebase-config.js';
import { createUserWithEmailAndPassword } from "firebase/auth";

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
