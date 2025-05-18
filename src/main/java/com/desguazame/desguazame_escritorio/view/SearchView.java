/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desguazame.desguazame_escritorio.view;

import com.desguazame.desguazame_escritorio.model.CarPart;
import com.desguazame.desguazame_escritorio.util.AppGlobals;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.carList;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.cartList;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.imgURL;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
            vb.setAlignment(Pos.CENTER);

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
                imageView.setImage(new Image(imageFullUrl, 180, 180, true, true));
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
            Label lbDescription = new Label("Descripción: " + cp.getDescription());
            Label lbPrecio = new Label("Precio: " + cp.getPrice() + " €");
            Label lbReferencia = new Label("Referencia: " + cp.getReference());
            Label lbYear = new Label("Año: " + cp.getYear());
            Label lbMB = new Label("Marca: " + cp.getBrand() + " Modelo: " + cp.getModel());
            Label lbCompany = new Label("Empresa: " + cp.getCompany());

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
