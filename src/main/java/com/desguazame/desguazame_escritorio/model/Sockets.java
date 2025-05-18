/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desguazame.desguazame_escritorio.model;

import static com.desguazame.desguazame_escritorio.util.AppGlobals.token;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.user;
import com.desguazame.desguazame_escritorio.view.JOptionError;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import org.json.JSONObject;

/**
 *
 * @author Charlie
 */
public class Sockets {

    /**
     * URI del servidor WebSocket.
     */
    private final String uri;

    /**
     * Objeto {@link Socket} que representa la conexión WebSocket.
     */
    private Socket socket;

    /**
     * Constructor de la clase WebSocket.
     *
     * @param uri La URI del servidor WebSocket al que se desea conectar.
     */
    public Sockets(String uri) {
        this.uri = uri;
        try {
            socket = IO.socket(new URI(uri));
            socket.connect();
        } catch (URISyntaxException ex) {
            Logger.getLogger(Sockets.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Realiza la autenticación del usuario a través del socket.
     * <p>
     * Envía las credenciales al servidor mediante el evento "comprobar
     * credenciales" y espera la respuesta a través del evento "token_cliente".
     * Si las credenciales son válidas, se actualiza la instancia global del
     * usuario y se establece el token. En caso de error, se muestra una ventana
     * con el mensaje correspondiente.
     * </p>
     *
     * @param campos HashMap que contiene los campos del formulario de login,
     * típicamente "usuario" y "password".
     * @param latch CountDownLatch utilizado para sincronizar la espera del hilo
     * que invoca este método.
     * @param success Variable atómica que indica si la autenticación fue
     * exitosa.
     */
    public void authUser(HashMap<String, String> campos, CountDownLatch latch, AtomicBoolean success) {

        // Emitir las credenciales
        JSONObject data = new JSONObject();
        String[] key = {"usuario", "password"};
        for (String k : key) {
            data.put(k, campos.get(k));
        }
        if (!socket.connected()) {
            socket.connect();
        }
        System.out.println("com.desguazame.desguazame_escritorio.model.Sockets.authUser()" + data.toString());
        socket.emit("comprobar credenciales", data);

        // Evento que maneja la respuesta del servidor con el token del cliente
        socket.on("token_cliente", args -> {
            JSONObject json = (JSONObject) args[0];
            if (user == null) {
                token = json.getString("token");
                user = new User(json.getString("nombre"), json.getString("apellido_a"), json.getString("apellido_b"), campos.get("password"), json.getString("direccion"), json.getInt("codigo_postal"), json.getString("ciudad"), json.getString("municipio"), json.getLong("telefono"), json.getLong("movil"), json.getString("dni"), json.getString("numero_cuenta"), json.getString("mail"), Long.parseLong(json.getString("tarjeta_credito")));
            } else {
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
            }
            success.set(true);
            latch.countDown();
        });

        socket.on("error_registro", args -> {
            Platform.runLater(() -> JOptionError.showError("Error Autenticación", "Usuario o contraseña erroneos"));
            success.set(false);
            latch.countDown();
        });

    }

    /**
     * Registra un nuevo usuario a través del socket.
     * <p>
     * Envía los datos de registro al servidor mediante el evento
     * "registrar_usuario" y espera la respuesta a través de los eventos
     * "usr_existe", "usr_registrado" o "error_registro". Según la respuesta,
     * actualiza el valor de éxito y desbloquea el hilo llamador.
     * </p>
     *
     * @param campos HashMap con los datos del formulario de registro, como
     * "MAIL", "CONTRASENA", "NOMBRE", etc.
     * @param latch CountDownLatch utilizado para sincronizar la espera del hilo
     * que invoca este método.
     * @param success Variable atómica que indica si el registro fue exitoso.
     */
    public void registerUser(HashMap<String, String> campos, CountDownLatch latch, AtomicBoolean success) {
        System.out.println("Se llama al registro de usuario");
        JSONObject data = new JSONObject();
        String[] key = {"MAIL", "CONTRASENA", "NOMBRE", "APELLIDO_A", "APELLIDO_B",
            "DIRECCION", "CODIGO_POSTAL", "CIUDAD", "MUNICIPIO", "TELEFONO", "MOVIL", "DNI", "TARJETA_CREDITO", "NUMERO_CUENTA"};
        for (String k : key) {
            data.put(k, campos.get(k));
        }

        if (!socket.connected()) {
            socket.connect();
        }

        socket.emit("registrar_usuario", data);

        socket.on("usr_existe", args -> {
            Platform.runLater(() -> JOptionError.showError("Usuario existente", "El correo ya está registrado."));
            success.set(false);
            latch.countDown();
        });

        socket.on("usr_registrado", args -> {
            System.out.println("✅ Usuario registrado correctamente");
            success.set(true);
            latch.countDown();
        });

        socket.on("error_registro", args -> {
            Platform.runLater(() -> JOptionError.showError("Registro fallido", args[0].toString()));
            success.set(false);
            latch.countDown();
        });
    }

    /**
     * Envía una actividad al servidor a través de un socket y espera una
     * respuesta para validar si el usuario está autenticado.
     * <p>
     * Este método se conecta al servidor si aún no está conectado, envía un
     * mensaje con el tipo de actividad y el token de autenticación, y luego
     * espera una respuesta para confirmar si el usuario es válido. Utiliza un
     * {@link CountDownLatch} para sincronización y un {@link AtomicBoolean}
     * para indicar el éxito.</p>
     *
     * @param type El tipo de actividad que realiza el usuario (por ejemplo:
     * "mover", "leer", etc.).
     * @param latch Mecanismo de sincronización que se libera una vez recibida
     * la respuesta.
     * @param success Variable atómica que se establece en true si la actividad
     * fue válida (usuario autenticado).
     */
    public void activity(String type, CountDownLatch latch, AtomicBoolean success) {
        if (!socket.connected()) {
            socket.connect();
        }

        JSONObject data = new JSONObject();
        data.put("token", token);
        data.put("actividad", type);

        socket.emit("actividad", data);

        socket.on("respuesta actividad", args -> {
            JSONObject json = (JSONObject) args[0];
            boolean isActivity = json.getBoolean("valido");

            if (!isActivity) {
                Platform.runLater(()
                        -> JOptionError.showError("Usuario no autentificado", args[0].toString())
                );
            }

            success.set(isActivity);
            latch.countDown();
        });
    }
    
    public void makePayment(JSONObject data) {
        socket.emit("realizar pago", data);
    }
}
