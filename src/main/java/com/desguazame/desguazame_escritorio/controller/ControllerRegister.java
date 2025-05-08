package com.desguazame.desguazame_escritorio.controller;

import com.desguazame.desguazame_escritorio.App;
import com.desguazame.desguazame_escritorio.model.DataVerify;
import com.desguazame.desguazame_escritorio.util.FormUtils;
import com.desguazame.desguazame_escritorio.view.JOptionError;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ControllerRegister {

    @FXML
    private HBox hBoxRegister;

    @FXML
    private void changeLogin() throws IOException {
        App.setRoot("login");
    }

    @FXML
    private void registerUser() throws IOException {
        HashMap<String, String> campos = FormUtils.readNodeFields(hBoxRegister);
        System.out.println("com.desguazame.desguazame_escritorio.controller.ControllerRegister.registerUser()" + campos.toString());
        for (Map.Entry<String, String> entry : campos.entrySet()) {
            String clave = entry.getKey();
            String valor = entry.getValue();
            if (!DataVerify.verificarDato(clave, valor)){
                JOptionError.showError(clave, valor);
                return;
            }
        }
    }

}
