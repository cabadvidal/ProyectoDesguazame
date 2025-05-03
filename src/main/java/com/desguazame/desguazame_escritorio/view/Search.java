/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desguazame.desguazame_escritorio.view;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

/**
 *
 * @author Charlie
 */
public class Search {
    
    private String aImagen[];
    @FXML
    private HBox hBox;

    public Search(String[] aImagen, HBox hBox) {
        this.aImagen = aImagen;
        this.hBox = hBox;
    }
    
    
}
