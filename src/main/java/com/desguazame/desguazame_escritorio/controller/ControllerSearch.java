package com.desguazame.desguazame_escritorio.controller;

import com.desguazame.desguazame_escritorio.App;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.user;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class ControllerSearch implements Initializable{

    @FXML
    private Label welcome;

    @FXML
    private void changeLogin() throws IOException {
        App.setRoot("login");
    }

    private void onLoad() throws IOException {
        String welcomer = "Bienvenido/a, " + user.getfName() + " " + user.getsName() + ", " + user.getName();
        welcome.setText(welcomer);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            onLoad();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
