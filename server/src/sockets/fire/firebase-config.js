import { getAuth, createUserWithEmailAndPassword, signInWithPhoneNumber, RecaptchaVerifier } from "firebase/auth";
import { initializeApp } from "firebase/app";
import admin from "firebase-admin";
import path from 'path';
import { cargarParametros } from "../../../xml/leer_xml.js";
//import { getAnalytics } from "firebase/analytics";

// Cargar parámetros de archivo XML
const datosFireBase = await cargarParametros('firebase');
// Cargar variables de configuración
const {apiKey, authDomain, projectId, storageBucket, messagingSenderId, appId, measurementId} = datosFireBase;

// Configuración de Firebase
const firebaseConfig = {
  apiKey: apiKey,
  authDomain: authDomain,
  projectId: projectId,
  storageBucket: storageBucket,
  messagingSenderId: messagingSenderId,
  appId: appId,
  measurementId: measurementId
};

// Inicializar Firebase
const appFirebase = initializeApp(firebaseConfig);
const auth = getAuth(appFirebase);

// Obtener la ruta del archivo actual
const __dirname = path.dirname(new URL(import.meta.url).pathname);

// Inicializa Firebase Admin SDK con las credenciales de la cuenta de servicio
admin.initializeApp({
  credential: admin.credential.cert(path.resolve(__dirname, 'service-account-file.json'))
});
// Exportar auth
export { auth, admin };