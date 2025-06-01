package com.example.desguazame.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.example.desguazame.R;
import com.example.desguazame.databinding.FragmentSearchBinding;
import com.example.desguazame.utils.AppGlobals;
import com.example.desguazame.utils.FormUtils;
import com.example.desguazame.utils.Sockets;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Fragmento encargado de realizar la búsqueda de piezas desde la app móvil.
 * Permite al usuario aplicar filtros como categoría, marca, modelo y texto libre.
 * Recupera datos desde el servidor y, tras realizar la búsqueda, redirige a un
 * fragmento de resultados.
 *
 * Utiliza un backend en Node.js mediante peticiones REST y actividad por socket.
 * Las opciones de los desplegables se obtienen dinámicamente desde el servidor.
 *
 * @author Charlie
 */
public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private Spinner spCategory, spBrand, spModel;
    private EditText eTextSearch;
    private Button btnSearch;
    private final String searchMenuURL = "http://83.37.180.236:10010/ObtenerSearch?TABLA=";
    private final String searchData = "http://83.37.180.236:10010/Search?";
    private Sockets socket;

    /**
     * Infla el layout del fragmento de búsqueda, inicializa los componentes
     * visuales y configura eventos como el cambio de marca y el botón de búsqueda.
     *
     * @param inflater El LayoutInflater.
     * @param parent Contenedor padre.
     * @param savedInstanceState Estado previo guardado (si aplica).
     * @return Vista principal del fragmento.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, parent, false);
        View root = binding.getRoot();

        spCategory = binding.spCategory;
        spBrand = binding.spBrand;
        spModel = binding.spModel;
        eTextSearch = binding.eTextSearch;
        btnSearch = binding.btnSearch;
        socket = new Sockets(requireContext(), AppGlobals.socketURL);

        cargarCategorias();
        cargarMarcas();
        cargarModelos("Toyota");
        spBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String marca = spBrand.getSelectedItem().toString();
                if (!marca.equals("Marcas")) {
                    cargarModelos(marca);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnSearch.setOnClickListener(v -> realizarBusqueda());

        return root;
    }

    /**
     * Carga las categorías disponibles desde el backend y las inserta en el Spinner.
     */
    private void cargarCategorias() {
        new Thread(() -> {
            List<String> lista = new ArrayList<>();
            lista.add("Categorías");
            try {
                JSONArray data = FormUtils.readJsonDeUrl(searchMenuURL + "CATEGORIAS");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    lista.add(obj.getString("NOMBRE_CATEGORIA"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            requireActivity().runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, lista);
                spCategory.setAdapter(adapter);
            });
        }).start();
    }

    /**
     * Carga las marcas de coches disponibles desde el backend y actualiza el Spinner.
     */
    private void cargarMarcas() {
        new Thread(() -> {
            List<String> lista = new ArrayList<>();
            lista.add("Marcas");
            try {
                JSONArray data = FormUtils.readJsonDeUrl(searchMenuURL + "MARCAS");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    lista.add(obj.getString("NOMBRE_MARCA"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            requireActivity().runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, lista);
                spBrand.setAdapter(adapter);
            });
        }).start();
    }

    /**
     * Carga los modelos de una marca específica desde el backend y actualiza el Spinner.
     *
     * @param marca Nombre de la marca seleccionada.
     */
    private void cargarModelos(String marca) {
        new Thread(() -> {
            List<String> lista = new ArrayList<>();
            lista.add("Modelos");
            try {
                String url = searchMenuURL + "MODELOS&MARCA=" + marca.toUpperCase();
                JSONArray data = FormUtils.readJsonDeUrl(url);
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    lista.add(obj.getString("NOMBRE_MODELO"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            requireActivity().runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, lista);
                spModel.setAdapter(adapter);
            });
        }).start();
    }

    /**
     * Realiza la búsqueda de piezas según los filtros seleccionados por el usuario.
     * Genera una URL con los parámetros y lanza una petición al servidor.
     * Si hay éxito, navega al fragmento de resultados.
     */
    private void realizarBusqueda() {
        String texto = eTextSearch.getText().toString().trim();
        String categoria = spCategory.getSelectedItem().toString();
        String marca = spBrand.getSelectedItem().toString();
        String modelo = spModel.getSelectedItem().toString();

        StringBuilder url = new StringBuilder(searchData);

        if ((!texto.isEmpty()) && !texto.equals("Palabra a buscar")) {
            url.append("TEXTO=").append(texto);
        }
        if (!categoria.equals("Categorías")) {
            url.append(url.charAt(url.length() - 1) == '?' ? "" : "&").append("CATEGORIAS=").append(categoria);
        }
        if (!marca.equals("Marcas")) {
            url.append(url.charAt(url.length() - 1) == '?' ? "" : "&").append("MARCAS=").append(marca);
        }
        if (!modelo.equals("Modelos")) {
            url.append(url.charAt(url.length() - 1) == '?' ? "" : "&").append("MODELOS=").append(modelo);
        }

        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean success = new AtomicBoolean(false);

        new Thread(() -> {
            socket.activity(AppGlobals.token, "Realizar búsqueda", latch, success);
            try {
                if (!latch.await(10, TimeUnit.SECONDS)) {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Timeout: sin respuesta del servidor", Toast.LENGTH_LONG).show()
                        );
                    }
                    return;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }

            if (success.get()) {
                JSONArray data = FormUtils.readJsonDeUrl(url.toString());
                if (data != null) {
                    if (isAdded()) {
                        Bundle bundle = new Bundle();
                        bundle.putString("jsonData", data.toString());

                        requireActivity().runOnUiThread(() -> {
                            if (!isAdded()) return;
                            NavController navController = NavHostFragment.findNavController(SearchFragment.this);
                            navController.navigate(R.id.action_searchFragment_to_searchResultFragment, bundle);
                        });
                    }
                } else {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            }
        }).start();
    }

    /**
     * Libera el binding para evitar fugas de memoria.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}