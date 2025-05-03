package com.desguazame.desguazame_escritorio.controller;

import com.desguazame.desguazame_escritorio.App;
import java.io.IOException;
import javafx.fxml.FXML;

public class ControllerRegister {

    @FXML
    private void changeLogin() throws IOException {
        App.setRoot("login");
    }
}