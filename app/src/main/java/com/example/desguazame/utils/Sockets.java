package com.example.desguazame.utils;

import static com.example.desguazame.utils.AppGlobals.id_factura;
import static com.example.desguazame.utils.AppGlobals.token;
import static com.example.desguazame.utils.AppGlobals.token_fcm;
import static com.example.desguazame.utils.AppGlobals.user;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.desguazame.access.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Clase que gestiona la comunicación WebSocket con el servidor a través de Socket.IO.
 * <p>
 * Permite realizar operaciones como autenticación, registro de usuario,
 * validación de actividad y pagos, todo mediante eventos WebSocket.
 * </p>
 * <p>
 * Requiere que en el archivo AndroidManifest.xml se tenga el permiso de Internet:
 * <pre>{@code <uses-permission android:name="android.permission.INTERNET" />}</pre>
 * </p>
 *
 * @author Charlie
 */
public class Sockets {

    private Socket socket;
    private final String uri;
    private final Context context;

    /**
     * Constructor que inicializa el socket con la URI del servidor.
     *
     * @param context Contexto Android donde se ejecuta la aplicación.
     * @param uri     URI del servidor WebSocket (ej: "http://192.168.1.78:3000").
     */
    public Sockets(Context context, String uri) {
        this.context = context;
        this.uri = uri;
        try {
            socket = IO.socket(new URI(uri));
        } catch (URISyntaxException e) {
            Log.e("Sockets", "URI inválida", e);
        }
    }

    /**
     * Establece la conexión con el servidor WebSocket si aún no está conectada.
     */
    public void connect() {
        if (socket != null && !socket.connected()) {
            socket.connect();
        }
    }

    /**
     * Autentica al usuario enviando su usuario y contraseña al servidor.
     *
     * @param campos  Mapa con las claves "usuario" y "password".
     * @param latch   CountDownLatch usado para sincronizar la respuesta.
     * @param success AtomicBoolean que se establecerá en true si la autenticación es exitosa.
     */
    public void authUser(HashMap<String, String> campos, CountDownLatch latch, AtomicBoolean success) {
        connect();

        JSONObject data = new JSONObject();
        try {
            data.put("usuario", campos.get("usuario"));
            data.put("password", campos.get("password"));
            data.put("tokenFcm", token_fcm);
            Log.e("FCM", "token_fcm: " + token_fcm);
        } catch (Exception e) {
            Log.e("Sockets", "Error creando JSON de auth", e);
        }

        socket.emit("comprobar credenciales", data);

        socket.once("token_cliente", args -> {
            JSONObject json = (JSONObject) args[0];
            token = json.optString("token");
            if (user == null) {
                try {
                    user = new User(json.getString("nombre"), json.getString("apellido_a"), json.getString("apellido_b"), campos.get("password"), json.getString("direccion"), json.getInt("codigo_postal"), json.getString("ciudad"), json.getString("municipio"), json.getLong("telefono"), json.getLong("movil"), json.getString("dni"), json.getString("numero_cuenta"), json.getString("mail"), Long.parseLong(json.getString("tarjeta_credito")));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    token = json.getString("token");
                    user.setName(json.getString("nombre"));
                    user.setfName(json.getString("apellido_a"));
                    user.setsName(json.getString("apellido_b"));
                    user.setPassword(campos.get("password"));
                    user.setAddress(json.getString("direccion"));
                    user.setCodePostal(json.getInt("codigo_postal"));
                    user.setCity(json.getString("ciudad"));
                    user.setMunicipality(json.getString("municipio"));
                    user.setPhone(json.getLong("telefono"));
                    user.setCell(json.getLong("movil"));
                    user.setDni(json.getString("dni"));
                    user.setBank(json.getString("numero_cuenta"));
                    user.setMail(json.getString("mail"));
                    user.setCreditCard(Long.parseLong(json.getString("tarjeta_credito")));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            success.set(true);
            latch.countDown();
        });

        socket.once("token", args -> {
            Toast.makeText(context, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            success.set(false);
            latch.countDown();
        });
    }

    /**
     * Registra un nuevo usuario en el sistema mediante WebSocket.
     *
     * @param campos  Mapa con los datos del formulario de registro. Claves esperadas:
     *                "MAIL", "CONTRASENA", "NOMBRE", "APELLIDO_A", "APELLIDO_B",
     *                "DIRECCION", "CODIGO_POSTAL", "CIUDAD", "MUNICIPIO", "TELEFONO",
     *                "MOVIL", "DNI", "TARJETA_CREDITO", "NUMERO_CUENTA".
     * @param latch   CountDownLatch para bloquear hasta recibir respuesta.
     * @param success AtomicBoolean que refleja si el registro fue exitoso.
     */
    public void registerUser(HashMap<String, String> campos, CountDownLatch latch, AtomicBoolean success) {
        connect();

        JSONObject data = new JSONObject();
        String[] keys = {"MAIL", "CONTRASENA", "NOMBRE", "APELLIDO_A", "APELLIDO_B",
                "DIRECCION", "CODIGO_POSTAL", "CIUDAD", "MUNICIPIO", "TELEFONO", "MOVIL",
                "DNI", "TARJETA_CREDITO", "NUMERO_CUENTA"};

        try {
            for (String k : keys) {
                data.put(k, campos.get(k));
            }
        } catch (Exception e) {
            Log.e("Sockets", "Error creando JSON de registro", e);
        }

        socket.emit("registrar_usuario", data);

        socket.once("usr_existe", args -> {
            Toast.makeText(context, "Este correo ya está registrado", Toast.LENGTH_SHORT).show();
            success.set(false);
            latch.countDown();
        });

        socket.once("usr_registrado", args -> {
            Log.d("Sockets", "Usuario registrado correctamente");
            success.set(true);
            latch.countDown();
        });

        socket.once("error_registro", args -> {
            Toast.makeText(context, "Error en el registro: " + args[0], Toast.LENGTH_SHORT).show();
            success.set(false);
            latch.countDown();
        });
    }

    /**
     * Envía una actividad realizada por el usuario para su validación mediante el token.
     *
     * @param token   Token JWT recibido previamente.
     * @param tipo    Tipo de actividad (por ejemplo: "login", "consulta", etc.).
     * @param latch   CountDownLatch para espera sincronizada.
     * @param success AtomicBoolean que indica si la actividad fue validada correctamente.
     */
    public void activity(String token, String tipo, CountDownLatch latch, AtomicBoolean success) {
        connect();

        JSONObject data = new JSONObject();
        try {
            data.put("token", token);
            data.put("actividad", tipo);
        } catch (Exception e) {
            Log.e("Sockets", "Error en actividad", e);
        }

        // ⚠️ Asegúrate de eliminar antes cualquier listener viejo
        socket.off("respuesta actividad");

        socket.on("respuesta actividad", args -> {
            JSONObject json = (JSONObject) args[0];
            boolean valido = json.optBoolean("valido", false);

            if (!valido) {
                Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            }

            success.set(valido);
            latch.countDown();

            // Eliminamos el listener para evitar fugas
            socket.off("respuesta actividad");
        });

        socket.emit("actividad", data);
    }

    /**
     * Solicita al servidor que realice un pago con los datos especificados.
     *
     * @param datosPago JSONObject con los datos necesarios para el pago.
     * @param latch     CountDownLatch para esperar respuesta del servidor.
     * @param success   AtomicBoolean que se establece en true si el pago fue exitoso.
     */
    public void makePayment(JSONObject datosPago, CountDownLatch latch, AtomicBoolean success) {
        connect();

        socket.emit("realizar pago", datosPago);

        socket.once("pago", args -> {
            JSONObject json = (JSONObject) args[0];
            boolean valido = json.optBoolean("valido", false);

            if (!valido) {
                Toast.makeText(context, "Error al realizar el pago", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    id_factura = json.getInt("ID_FACTURA");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            success.set(valido);
            latch.countDown();
        });
    }

    /**
     * Cierra la conexión WebSocket si está activa.
     */
    public void disconnect() {
        if (socket != null && socket.connected()) {
            socket.disconnect();
        }
    }
}
