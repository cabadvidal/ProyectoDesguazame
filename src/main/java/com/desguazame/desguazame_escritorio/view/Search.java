/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desguazame.desguazame_escritorio.view;

import com.desguazame.desguazame_escritorio.model.CarPart;
import com.desguazame.desguazame_escritorio.util.AppGlobals;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.carList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author Charlie
 */
public class Search {

    private String aImagen[];
    @FXML
    private HBox hBox;

    public Search(String[] aImagen, HBox hBox) {

        for (CarPart cp : carList) {
            VBox vb = new VBox();
            aImagen = cp.getImagen();
            for (String sb : aImagen) {
                Image image = new Image(sb, 150, 150, true, true);
            }

            Label lbCategorie = new Label(cp.getCategories());
            Label lbDescription = new Label(cp.getDescription());

        }
        this.aImagen = aImagen;
        this.hBox = hBox;
    }

}
