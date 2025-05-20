/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desguazame.desguazame_escritorio.view;

import com.desguazame.desguazame_escritorio.model.CarPart;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.carList;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.cartList;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.imgURL;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

/**
 * Clase que representa la vista de búsqueda y listado de piezas de automóvil.
 * <p>
 * Muestra una cuadrícula con tarjetas que representan cada pieza, incluyendo un
 * slider de imágenes, información descriptiva y un botón para agregar o
 * eliminar la pieza del carrito. También actualiza el contador de elementos en
 * el carrito.
 * </p>
 *
 * @author Charlie
 */
public class SearchView {

    private VBox vBox;

    private Label lblCartCount;

    private static int lblCount;

    /**
     * Constructor que inicializa la vista de búsqueda con el contenedor y la
     * etiqueta del carrito, además de crear la interfaz con los datos actuales.
     *
     * @param vBox Contenedor principal para añadir las tarjetas.
     * @param lblCartCount Etiqueta que muestra el número de elementos en el
     * carrito.
     */
    public SearchView(VBox vBox, Label lblCartCount) {
        this.vBox = vBox;
        this.lblCartCount = lblCartCount;
        lblCount = cartList.size();
        createSellerPart();
    }

    /**
     * Construye la vista con las tarjetas de cada pieza disponible.
     * <p>
     * Cada tarjeta incluye un slider de imágenes, etiquetas con la información
     * de la pieza, y un botón para agregar o eliminar la pieza del carrito. Las
     * tarjetas se agrupan en filas de hasta tres columnas.
     * </p>
     */
    private void createSellerPart() {
        int index = 0;
        HBox hBoxLine = new HBox(20); // espacio entre tarjetas
        hBoxLine.setAlignment(Pos.CENTER);

        for (CarPart cp : carList) {
            VBox vb = new VBox(10);
            vb.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: #f5f5f5;");
            vb.setPrefWidth(250);
            vb.setAlignment(Pos.TOP_CENTER);

            String[] aImagen = cp.getImagen();

            // Slider con ImageView
            ImageView imageView = new ImageView();
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);

            int[] currentIndex = {0};

            if (aImagen != null && aImagen.length > 0) {
                String imageFullUrl = imgURL + aImagen[currentIndex[0]];
                imageView.setImage(new Image(imageFullUrl, 150, 150, true, true));
            }

            Button btnPrev = new Button("<");
            Button btnNext = new Button(">");

            btnPrev.setOnAction(e -> {
                currentIndex[0] = (currentIndex[0] - 1 + aImagen.length) % aImagen.length;
                String imageFullUrl = imgURL + aImagen[currentIndex[0]];
                imageView.setImage(new Image(imageFullUrl, 150, 150, true, true));
            });

            btnNext.setOnAction(e -> {
                currentIndex[0] = (currentIndex[0] + 1) % aImagen.length;
                String imageFullUrl = imgURL + aImagen[currentIndex[0]];
                imageView.setImage(new Image(imageFullUrl, 180, 180, true, true));
            });

            HBox sliderControls = new HBox(10, btnPrev, imageView, btnNext);
            sliderControls.setAlignment(Pos.CENTER);

            // Info de la pieza
            Label lbCategorie = new Label("Categoría: " + cp.getCategories());
            lbCategorie.setWrapText(true);
            lbCategorie.setMaxWidth(230);
            lbCategorie.setPrefWidth(230);

            Label lbDescription = new Label("Descripción: " + cp.getDescription());
            lbDescription.setWrapText(true);
            lbDescription.setMaxWidth(230);
            lbDescription.setPrefWidth(230);
            lbDescription.setPrefHeight(Region.USE_COMPUTED_SIZE); // <- CLAVE
            lbDescription.setMinHeight(Region.USE_PREF_SIZE);

            Label lbReferencia = new Label("Referencia: " + cp.getReference());
            lbReferencia.setWrapText(true);
            lbReferencia.setMaxWidth(230);
            lbReferencia.setPrefWidth(230);

            Label lbMB = new Label("Marca: " + cp.getBrand() + " Modelo: " + cp.getModel());
            lbMB.setWrapText(true);
            lbMB.setMaxWidth(230);
            lbMB.setPrefWidth(230);

            Label lbCompany = new Label("Empresa: " + cp.getCompany());
            lbCompany.setWrapText(true);
            lbCompany.setMaxWidth(230);
            lbCompany.setPrefWidth(230);

            Label lbPrecio = new Label("Precio: " + cp.getPrice() + " €");
            lbPrecio.setWrapText(true);
            lbPrecio.setMaxWidth(230);
            lbPrecio.setPrefWidth(230);

            Label lbYear = new Label("Año: " + cp.getYear());
            lbYear.setWrapText(true);
            lbYear.setMaxWidth(230);
            lbYear.setPrefWidth(230);

            boolean isInCart = verifyCartList(cp);
            boolean[] isCart = {isInCart};
            Button btnAddToCart = new Button("");
            btnAddToCart.setText(isCart[0] ? "Borrar del carrito" : "Agregar al carrito");

            if (isInCart) {
                lblCartCount.setText(lblCount + "");
                lblCartCount.setVisible(true);
            }

            btnAddToCart.setOnAction(e -> {
                if (!isCart[0]) {
                    btnAddToCart.setText("Borrar del carrito");
                    lblCount++;
                    lblCartCount.setText(lblCount + "");
                    lblCartCount.setVisible(true);
                    cartList.add(cp);
                } else {
                    btnAddToCart.setText("Agregar al carrito");
                    lblCount--;
                    if (lblCount >= 1) {
                        lblCartCount.setText(lblCount + "");
                        cartList.remove(cp);
                    } else {
                        lblCartCount.setText("");
                    }

                    cartList.removeIf(part -> part.getId() == cp.getId());
                }
                isCart[0] = !isCart[0];
            });

            vb.getChildren().addAll(sliderControls, lbCategorie, lbReferencia, lbDescription, lbYear, lbMB, lbCompany, lbPrecio, btnAddToCart);
            hBoxLine.getChildren().add(vb);
            index++;

            // Si es la tercera columna o última pieza, añadir fila
            if (index % 3 == 0 || index == carList.size()) {
                VBox.setMargin(hBoxLine, new Insets(10));
                vBox.getChildren().add(hBoxLine);
                hBoxLine = new HBox(20);
                hBoxLine.setAlignment(Pos.CENTER);
            }
        }
    }

    /**
     * Verifica si una pieza ya se encuentra en el carrito.
     *
     * @param cp La pieza que se desea verificar.
     * @return {@code true} si la pieza está en el carrito, {@code false} en
     * caso contrario.
     */
    private boolean verifyCartList(CarPart cp) {
        for (CarPart cart : cartList) {
            if (cart.getId() == cp.getId()) {
                return true;
            }
        }
        return false;
    }
}
