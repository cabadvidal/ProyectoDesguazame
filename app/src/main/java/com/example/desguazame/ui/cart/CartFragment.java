package com.example.desguazame.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.desguazame.R;
import com.example.desguazame.access.CarPart;
import com.example.desguazame.databinding.FragmentCartBinding;
import com.example.desguazame.utils.AppGlobals;

/**
 * Fragmento que representa la vista del carrito de compra dentro de la aplicación.
 * Permite visualizar las piezas añadidas, ver el total actualizado y proceder al pago.
 * Este fragmento se encarga de inicializar el RecyclerView con su adaptador,
 * así como de calcular el importe total de los productos seleccionados.
 *
 * @author Charlie
 */
public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    private CartAdapter adapter;

    /**
     * Método llamado al crear la vista del fragmento.
     * Infla el layout, configura el RecyclerView y el botón de finalizar compra.
     *
     * @param inflater           El LayoutInflater usado para inflar la vista.
     * @param container          El contenedor padre de la vista del fragmento.
     * @param savedInstanceState Estado previamente guardado del fragmento.
     * @return La vista raíz del fragmento.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        adapter = new CartAdapter(requireContext(), this::actualizarTotal);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        binding.btnCkeckout.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_cart_to_nav_finalcart);
        });

        root.post(this::actualizarTotal);

        return root;
    }

    /**
     * Recalcula el importe total del carrito sumando los precios de todas las piezas.
     * El resultado se muestra en el TextView correspondiente.
     */
    private void actualizarTotal() {
        double total = 0;
        for (CarPart part : AppGlobals.cartList) {
            total += part.getPrice();
        }
        if (binding != null && binding.textVTotal != null) {
            binding.textVTotal.setText("Total: " + total + " €");
        }
    }

    /**
     * Método llamado al destruir la vista del fragmento.
     * Se libera el binding para evitar fugas de memoria.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
