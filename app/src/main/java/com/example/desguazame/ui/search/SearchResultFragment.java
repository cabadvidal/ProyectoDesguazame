package com.example.desguazame.ui.search;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.desguazame.R;
import com.example.desguazame.access.CarPart;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragmento que muestra los resultados de la búsqueda de piezas realizadas desde {@link SearchFragment}.
 *
 * Este fragmento recibe un {@code JSONArray} con los resultados de búsqueda y construye
 * objetos {@link CarPart} que se visualizan dinámicamente en la interfaz mediante la clase
 * auxiliar {@link SearchView}. También muestra un contador del carrito.
 *
 * Utiliza un {@link HorizontalScrollView} para facilitar la navegación visual de los resultados.
 *
 * @author Charlie
 */
public class SearchResultFragment extends Fragment {

    private LinearLayout container;
    private HorizontalScrollView scrollView;
    private TextView lblCartCount;

    /**
     * Infla el layout del fragmento y carga las piezas si hay datos disponibles en el {@code Bundle}.
     *
     * @param inflater El inflador del layout.
     * @param containerLayout Contenedor padre.
     * @param savedInstanceState Estado guardado (si aplica).
     * @return La vista raíz del fragmento.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup containerLayout, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_result, containerLayout, false);

        container = view.findViewById(R.id.linearContainer);
        scrollView = view.findViewById(R.id.horizontalElementSearch);
        lblCartCount = new TextView(requireContext());

        if (getArguments() != null) {
            String jsonData = getArguments().getString("jsonData");
            try {
                JSONArray dataArray = new JSONArray(jsonData);
                cargarPiezas(dataArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    /**
     * Procesa los datos del {@code JSONArray} recibido del fragmento anterior.
     * Por cada objeto JSON del array se construye una instancia de {@link CarPart}
     * que se añade a una lista temporal.
     *
     * Finalmente, se instancia {@link SearchView} para renderizar la interfaz visual
     * de los resultados con scroll horizontal.
     *
     * @param dataArray Array de resultados en formato JSON.
     */
    private void cargarPiezas(JSONArray dataArray) {
        List<CarPart> parts = new ArrayList<>();
        for (int i = 0; i < dataArray.length(); i++) {
            try {
                JSONObject obj = dataArray.getJSONObject(i);
                CarPart cp = new CarPart(
                        obj.getInt("ID_PIEZAS"),
                        obj.optString("DESCRIPCION", ""),
                        (int) Double.parseDouble(obj.optString("PESO", "0")),
                        Double.parseDouble(obj.optString("PRECIO", "0")),
                        obj.optString("REFERENCIA", ""),
                        obj.optString("FECHA_YEAR", ""),
                        obj.optString("IMAGENES", "").split(";"),
                        obj.optString("CATEGORIAS", ""),
                        obj.optString("RAZON_SOCIAL", ""),
                        obj.optString("NOMBRE_MODELO", ""),
                        obj.optString("NOMBRE_MARCA", ""),
                        obj.optString("RAZON_SOCIAL", "")
                );
                parts.add(cp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        new SearchView(requireContext(), scrollView, container, parts, lblCartCount);
    }
}
