package com.example.desguazame.ui.cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.desguazame.R;
import com.example.desguazame.access.CarPart;
import com.example.desguazame.utils.AppGlobals;

/**
 * Adaptador que gestiona los elementos visuales del carrito de compra en un RecyclerView.
 * Permite mostrar las piezas añadidas al carrito, así como eliminarlas y actualizar el total.
 *
 * @author Charlie
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    /**
     * Interfaz para notificar cambios en el carrito, como actualizaciones tras eliminar un producto.
     */
    public interface OnCartChangedListener {
        void onCartUpdated();
    }

    private final OnCartChangedListener listener;
    private final Context context;

    /**
     * Constructor del adaptador.
     *
     * @param context  Contexto de la aplicación.
     * @param listener Escuchador de cambios en el carrito.
     */
    public CartAdapter(Context context, OnCartChangedListener listener) {
        this.context = context;
        this.listener = listener;
    }

    /**
     * Crea una nueva instancia de ViewHolder para cada elemento del RecyclerView.
     *
     * @param parent   Vista padre del RecyclerView.
     * @param viewType Tipo de vista (no se utiliza en este caso).
     * @return ViewHolder creado.
     */
    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_cart_recycler, parent, false);
        return new CartViewHolder(view);
    }

    /**
     * Asocia los datos de una pieza de coche a una vista de lista.
     *
     * @param holder   ViewHolder que representa el ítem.
     * @param position Posición del elemento en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CarPart item = AppGlobals.cartList.get(position);

        holder.textReference.setText("Referencia: " + item.getReference());
        holder.textDescription.setText("Descripción: " + item.getDescription());
        holder.textPrice.setText("Precio: " + item.getPrice() + " €");

        holder.btnRemove.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                AppGlobals.cartList.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, AppGlobals.cartList.size());
                listener.onCartUpdated();
            }
        });
    }


    /**
     * Devuelve el número total de elementos en el carrito.
     *
     * @return número de ítems en la lista del carrito.
     */
    @Override
    public int getItemCount() {
        return AppGlobals.cartList.size();
    }

    /**
     * ViewHolder que representa cada ítem del carrito en el RecyclerView.
     */
    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView textReference, textDescription, textPrice;
        ImageButton btnRemove;

        /**
         * Constructor del ViewHolder.
         *
         * @param itemView Vista que representa un ítem del carrito.
         */
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            textReference = itemView.findViewById(R.id.textItemReference);
            textDescription = itemView.findViewById(R.id.textItemDescription);
            textPrice = itemView.findViewById(R.id.textItemPrice);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}

