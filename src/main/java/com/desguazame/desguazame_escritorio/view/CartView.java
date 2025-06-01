/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desguazame.desguazame_escritorio.view;

import com.desguazame.desguazame_escritorio.model.CarPart;
import static com.desguazame.desguazame_escritorio.util.AppGlobals.cartList;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

/**
 * Clase que gestiona la vista del carrito de compra en la interfaz gráfica.
 * <p>
 * Construye y actualiza la lista de productos añadidos al carrito, mostrando
 * referencia, descripción, precio y un botón para eliminar cada producto.
 * También crea y actualiza la sección de checkout con subtotal, IVA y precio
 * total.
 * </p>
 *
 * @author Charlie
 */
public class CartView {

    private VBox vBox;
    private VBox vBoxCheckOut;
    private boolean isCreateCheckout;

    private Text tfSubTotalValue;
    private Text tfIVAValue;
    private Text tfTotalValue;

    /**
     * Constructor que inicializa la vista del carrito y crea su contenido.
     *
     * @param vBox Contenedor para listar los productos en el carrito.
     * @param vBoxCheckOut Contenedor para mostrar el resumen de la compra.
     */
    public CartView(VBox vBox, VBox vBoxCheckOut) {
        this.vBox = vBox;
        this.vBoxCheckOut = vBoxCheckOut;
        isCreateCheckout = false;
        createCartView();
    }

    /**
     * Construye y refresca la vista del carrito con los productos actuales.
     * <p>
     * Añade para cada producto un HBox con su información y un botón para
     * eliminarlo. Actualiza también la vista de checkout con los totales
     * correspondientes.
     * </p>
     */
    private void createCartView() {
        double totalPrice = 0;
        vBox.getChildren().clear();

        for (CarPart cp : cartList) {
            totalPrice += cp.getPrice();
            HBox hb = new HBox(10);
            VBox.setMargin(hb, new Insets(10, 10, 10, 10));
            hb.setStyle("-fx-padding: 25; -fx-border-color: #ccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: #f5f5f5;");
            hb.setPrefWidth(150);
            hb.setAlignment(Pos.CENTER_LEFT);

            Button btnDelete = new Button("X");

            btnDelete.setOnAction(e -> {
                cartList.removeIf(part -> part.getId() == cp.getId());
                createCartView();
            });

            TextFlow tfReferencia = createStyledLabel("Referencia: ", cp.getReference(), 300);
            TextFlow tfDescripcion = createStyledLabel("Descripción: ", cp.getDescription(), 400);
            TextFlow tfPrecio = createStyledLabel("Precio: ", String.format("%.2f €", cp.getPrice()), 250);

            hb.getChildren().addAll(tfReferencia, tfDescripcion, tfPrecio, btnDelete);
            vBox.getChildren().add(hb);
        }
        createCheckout(totalPrice);
    }

    /**
     * Crea un TextFlow con dos textos: uno en negrita y otro en estilo normal.
     *
     * @param boldText Texto que se mostrará en negrita.
     * @param normalText Texto que se mostrará en estilo normal.
     * @return Un TextFlow que contiene ambos textos estilizados.
     */
    private TextFlow createStyledLabel(String boldText, String normalText, int value) {
        Text bold = new Text(boldText);
        bold.setFont(Font.font("System", FontWeight.BOLD, 18));

        Text normal = new Text(normalText);
        normal.setFont(Font.font("System", FontWeight.NORMAL, 18));
        TextFlow textFlow = new TextFlow(bold, normal);
        textFlow.setMaxWidth(value); 
        textFlow.setPrefWidth(value);


        textFlow.setPrefHeight(Region.USE_COMPUTED_SIZE);
        textFlow.setMinHeight(Region.USE_PREF_SIZE);

        textFlow.setLineSpacing(2); 
        return textFlow;
    }

    /**
     * Crea o actualiza la sección de checkout con subtotal, IVA y precio total.
     * <p>
     * La primera vez crea los controles y los añade al contenedor
     * correspondiente, posteriormente solo actualiza los valores mostrados.
     * </p>
     *
     * @param totalPrice El precio total antes de aplicar el IVA.
     */
    private void createCheckout(double totalPrice) {
        double iva = totalPrice * 0.21;

        if (!isCreateCheckout) {
            HBox hb = new HBox(20);
            hb.setAlignment(Pos.CENTER_RIGHT);
            // Crear los valores de texto por separado y guardarlos
            tfSubTotalValue = new Text(String.format("%.2f€", totalPrice));
            tfSubTotalValue.setFont(Font.font("System", FontWeight.NORMAL, 18));
            Text tfSubTotalLabel = new Text("Precio: ");
            tfSubTotalLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
            TextFlow tfSubTotal = new TextFlow(tfSubTotalLabel, tfSubTotalValue);

            tfIVAValue = new Text(String.format("%.2f€", iva));
            tfIVAValue.setFont(Font.font("System", FontWeight.NORMAL, 18));
            Text tfIVALabel = new Text("IVA: ");
            tfIVALabel.setFont(Font.font("System", FontWeight.BOLD, 18));
            TextFlow tfIVA = new TextFlow(tfIVALabel, tfIVAValue);

            tfTotalValue = new Text(String.format("%.2f€", totalPrice + iva));
            tfTotalValue.setFont(Font.font("System", FontWeight.NORMAL, 18));
            Text tfTotalLabel = new Text("Precio Total: ");
            tfTotalLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
            TextFlow tfTotal = new TextFlow(tfTotalLabel, tfTotalValue);

            hb.getChildren().addAll(tfSubTotal, tfIVA, tfTotal);
            vBoxCheckOut.getChildren().add(hb);
            isCreateCheckout = true;
        } else {
            // Solo actualizamos los valores
            tfSubTotalValue.setText(String.format("%.2f€", totalPrice));
            tfIVAValue.setText(String.format("%.2f€", iva));
            tfTotalValue.setText(String.format("%.2f€", totalPrice + iva));
        }
    }
}
