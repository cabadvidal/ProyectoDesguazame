package com.desguazame.desguazame_escritorio.controller;

import com.desguazame.desguazame_escritorio.App;
import com.desguazame.desguazame_escritorio.threads.WebSocket;
import java.io.IOException;
import javafx.fxml.FXML;

public class ControllerLogin {

    /**
     * Método para cambiar a la ventana de registro.
     * @throws IOException 
     */
    @FXML
    private void changeRegister() throws IOException {
        App.setRoot("register");
    }

    /**
     * Método para verificar login en el servidor y cambiar a la ventana de usuario.
     * @throws IOException 
     */
    @FXML
    private void changeSearch() throws IOException {
        // Inicia el WebSocket en un nuevo hilo
        Thread webSocketThread = new Thread(new WebSocket("http://192.168.1.122:10010"));
        webSocketThread.setDaemon(true); // No bloqueará el cierre del programa
        webSocketThread.start();

        // Espera un poco para asegurar que el WebSocket se haya conectado antes de cambiar la vista
        try {
            webSocketThread.join();  // Espera que el hilo WebSocket termine de conectarse
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Cambia la vista a "search"
        App.setRoot("search");
    }
}
