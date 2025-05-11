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

    public void RegisterUser(HashMap<String, String> campos, CountDownLatch latch, AtomicBoolean success) {
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

}
