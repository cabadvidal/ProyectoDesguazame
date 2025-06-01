package com.example.desguazame.ui.search;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.desguazame.R;
import com.example.desguazame.access.CarPart;
import java.util.List;
import static com.example.desguazame.utils.AppGlobals.cartList;
import static com.example.desguazame.utils.AppGlobals.imgURL;
import androidx.core.content.ContextCompat;

/**
 * Clase encargada de generar dinámicamente las tarjetas visuales de resultados de búsqueda
 * dentro de un {@link HorizontalScrollView} usando los datos de una lista de objetos {@link CarPart}.
 * <p>
 * Ofrece navegación de imágenes por pieza, interacción con el carrito de compra y muestra
 * actualizada del contador de artículos añadidos.
 * </p>
 *
 * @author Charlie
 */
public class SearchView {

    private final Context context;
    private final HorizontalScrollView scrollView;
    private final LinearLayout container;
    private final TextView lblCartCount;
    private final List<CarPart> carList;

    /**
     * Constructor que inicializa la vista de resultados con las piezas proporcionadas.
     *
     * @param context       Contexto de la aplicación.
     * @param scrollView    Scroll horizontal donde se inserta la vista.
     * @param container     Contenedor lineal donde se colocan las tarjetas.
     * @param carList       Lista de piezas a mostrar.
     * @param lblCartCount  Etiqueta donde se muestra el número total de piezas en el carrito.
     */
    public SearchView(Context context, HorizontalScrollView scrollView, LinearLayout container, List<CarPart> carList, TextView lblCartCount) {
        this.context = context;
        this.scrollView = scrollView;
        this.container = container;
        this.carList = carList;
        this.lblCartCount = lblCartCount;

        cargarTarjetas();
    }


    /**
     * Crea y añade dinámicamente las tarjetas de las piezas al contenedor visual.
     * Cada tarjeta incluye datos como descripción, precio, imagen y botón para añadir o quitar del carrito.
     */
    private void cargarTarjetas() {
        container.removeAllViews();

        for (CarPart cp : carList) {
            LinearLayout card = new LinearLayout(context);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(32, 32, 32, 32);
            card.setBackgroundResource(R.drawable.card_background);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    700, // ancho fijo más elegante
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(24, 24, 24, 24);
            card.setLayoutParams(cardParams);

            // Contenedor imagen y botones
            LinearLayout imgNav = new LinearLayout(context);
            imgNav.setOrientation(LinearLayout.HORIZONTAL);
            imgNav.setGravity(Gravity.CENTER);
            imgNav.setPadding(0, 0, 0, 16);

            Button btnPrev = new Button(context);
            btnPrev.setText("<");
            btnPrev.setWidth(20);
            btnPrev.setHeight(20);
            GradientDrawable background = new GradientDrawable();
            background.setShape(GradientDrawable.RECTANGLE);
            background.setColor(ContextCompat.getColor(context, R.color.purple_700)); // Color de fondo
            background.setCornerRadius(32f); // Radio del borde en píxeles (usa dp convertidos si quieres)
            btnPrev.setBackground(background);

            Button btnNext = new Button(context);
            btnNext.setText(">");
            btnNext.setWidth(20);
            btnNext.setHeight(20);
            btnNext.setBackground(background);


            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(300, 300);
            imageView.setLayoutParams(imgParams);

            String[] imagenes = cp.getImagen();
            final int[] currentImage = {0};

            if (imagenes != null && imagenes.length > 0) {
                Glide.with(context)
                        .load(imgURL + imagenes[0])
                        .placeholder(R.drawable.placeholder)
                        .into(imageView);
            }

            btnPrev.setOnClickListener(v -> {
                if (imagenes.length > 0) {
                    currentImage[0] = (currentImage[0] - 1 + imagenes.length) % imagenes.length;
                    Glide.with(context).load(imgURL + imagenes[currentImage[0]]).into(imageView);
                }
            });

            btnNext.setOnClickListener(v -> {
                if (imagenes.length > 0) {
                    currentImage[0] = (currentImage[0] + 1) % imagenes.length;
                    Glide.with(context).load(imgURL + imagenes[currentImage[0]]).into(imageView);
                }
            });

            imgNav.addView(btnPrev);
            imgNav.addView(imageView);
            imgNav.addView(btnNext);

            card.addView(imgNav);
            card.addView(buildText("Categoría: " + cp.getCategories()));
            card.addView(buildText("Referencia: " + cp.getReference()));
            card.addView(buildText("Descripción: " + cp.getDescription()));
            card.addView(buildText("Año: " + cp.getYear()));
            card.addView(buildText("Marca: " + cp.getBrand() + " Modelo: " + cp.getModel()));
            card.addView(buildText("Empresa: " + cp.getCompany()));
            card.addView(buildText("Precio: " + cp.getPrice() + " €"));

            Button btnCart = new Button(context);
            boolean enCarrito = verificarCarrito(cp);
            btnCart.setText(enCarrito ? "Borrar del carrito" : "Agregar al carrito");

            btnCart.setBackgroundColor(Color.parseColor("#DDDDDD"));
            btnCart.setTextColor(Color.BLACK);
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            btnParams.setMargins(0, 16, 0, 0);
            btnCart.setLayoutParams(btnParams);

            btnCart.setOnClickListener(v -> {
                if (verificarCarrito(cp)) {
                    cartList.removeIf(item -> item.getId() == cp.getId());
                    btnCart.setText("Agregar al carrito");
                } else {
                    cartList.add(cp);
                    btnCart.setText("Borrar del carrito");
                }
                actualizarContador();
            });

            card.addView(btnCart);
            container.addView(card);
        }

        actualizarContador();
    }

    /**
     * Construye un {@link TextView} personalizado con estilo homogéneo para mostrar atributos de cada pieza.
     *
     * @param text Texto a mostrar.
     * @return Objeto TextView listo para ser añadido a la tarjeta.
     */
    private TextView buildText(String text) {
        TextView tv = new TextView(context);
        tv.setText(text);
        tv.setPadding(8, 8, 8, 8);
        tv.setTextSize(15f);
        tv.setTextColor(Color.DKGRAY);
        return tv;
    }

    /**
     * Verifica si una pieza ya se encuentra en el carrito de compra.
     *
     * @param cp Pieza a comprobar.
     * @return {@code true} si está en el carrito, {@code false} en caso contrario.
     */
    private boolean verificarCarrito(CarPart cp) {
        for (CarPart item : cartList) {
            if (item.getId() == cp.getId()) return true;
        }
        return false;
    }

    /**
     * Actualiza el contador visual de piezas en el carrito, mostrando u ocultando
     * el {@link TextView} según corresponda.
     */
    private void actualizarContador() {
        int count = cartList.size();
        if (count > 0) {
            lblCartCount.setText(String.valueOf(count));
            lblCartCount.setVisibility(View.VISIBLE);
        } else {
            lblCartCount.setText("");
            lblCartCount.setVisibility(View.INVISIBLE);
        }
    }
}

