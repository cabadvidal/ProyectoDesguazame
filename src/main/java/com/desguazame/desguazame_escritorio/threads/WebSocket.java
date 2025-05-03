/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desguazame.desguazame_escritorio.threads;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONObject;
import java.util.concurrent.CountDownLatch;
import java.net.URI;

/**
 * Clase que maneja la conexión WebSocket con un servidor y permite la emisión de eventos.
 * Esta clase implementa la interfaz {@link Runnable} para ejecutar el WebSocket en un hilo separado.
 */
public class WebSocket implements Runnable {

    /**
     * URI del servidor WebSocket.
     */
    private final String uri;

    /**
     * Objeto {@link Socket} que representa la conexión WebSocket.
     */
    private Socket socket;

    /**
     * Latch utilizado para esperar a que la conexión WebSocket se establezca antes de continuar.
     */
    private CountDownLatch latch;

    /**
     * Constructor de la clase WebSocket.
     *
     * @param uri La URI del servidor WebSocket al que se desea conectar.
     */
    public WebSocket(String uri) {
        this.uri = uri;
        this.latch = new CountDownLatch(1); // Espera solo una vez por la conexión
    }

    /**
     * Método que se ejecuta al iniciar el hilo WebSocket. Este método se encarga de conectar
     * al servidor WebSocket, emitir credenciales y manejar los eventos de conexión, recepción de 
     * tokens y desconexión.
     */
    @Override
    public void run() {
        try {
            socket = IO.socket(new URI(uri));

            // Evento que se ejecuta cuando el WebSocket se conecta exitosamente al servidor
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("🔗 Conectado al servidor");

                    // Emitir las credenciales una vez conectado
                    JSONObject data = new JSONObject();
                    data.put("usuario", "cliente@desga.com");
                    data.put("password", "Cliente123@");

                    socket.emit("comprobar credenciales", data);

                    // Decrementamos el latch para permitir que el hilo principal continúe
                    latch.countDown();
                }
            });

            // Evento que maneja la respuesta del servidor con el token del cliente
            socket.on("token_cliente", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (args.length > 0 && args[0] instanceof JSONObject) {
                        JSONObject data = (JSONObject) args[0];
                        String token = data.optString("token");
                        String nombre = data.optString("nombre");
                        String apellidos = data.optString("apellidos");

                        System.out.println("✅ Token recibido: " + token);
                        System.out.println("👤 Nombre: " + nombre + " " + apellidos);
                    }
                }
            });

            // Evento que se ejecuta cuando se pierde la conexión
            socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("🔌 Conexión cerrada");
                }
            });

            // Intentamos conectar al servidor
            socket.connect();

            // Esperamos hasta que la conexión esté establecida y las credenciales hayan sido emitidas
            latch.await();  // Bloquea hasta que countDown() sea llamado en el evento de conexión

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Envía un mensaje al servidor a través del WebSocket si la conexión está activa.
     *
     * @param event El nombre del evento que se desea emitir.
     * @param data  Los datos que se desean enviar al servidor.
     */
    public void sendMessage(String event, JSONObject data) {
        if (socket != null && socket.connected()) {
            socket.emit(event, data);
        } else {
            System.out.println("⚠️ Socket no conectado, no se puede emitir el mensaje.");
        }
    }
}