package com.example.desguazame.ui.cart;

import static com.example.desguazame.utils.AppGlobals.facturaURL;
import static com.example.desguazame.utils.AppGlobals.id_factura;
import static com.example.desguazame.utils.AppGlobals.token;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.desguazame.R;
import com.example.desguazame.access.CarPart;
import com.example.desguazame.utils.AppGlobals;
import com.example.desguazame.utils.CartData;
import com.example.desguazame.utils.Sockets;
import org.json.JSONObject;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Fragmento que representa la pantalla final del carrito de compra.
 * Permite al usuario revisar el total a pagar, seleccionar el método de pago
 * y procesar la compra. También se encarga de la descarga automática de la factura en PDF.
 *
 * Utiliza comunicación con el servidor mediante la clase {@code Sockets},
 * gestionando la actividad de usuario y el pago con hilos y sincronización.
 *
 * @author Charlie
 */
public class FinalCartFragment extends Fragment {

    private RadioGroup radioGroup;
    private RadioButton rbCreditCard, rbBank;
    private Button btnCheck;
    private TextView txtVPrice, txtVIVA, txtVTotal;
    private Sockets socket;

    /**
     * Infla la vista del fragmento, inicializa los elementos visuales y calcula los valores de precio, IVA y total.
     * Establece el evento para el botón de pago.
     *
     * @param inflater El LayoutInflater usado para inflar la vista.
     * @param container El contenedor padre del fragmento.
     * @param savedInstanceState Estado previamente guardado (si lo hubiera).
     * @return La vista raíz del fragmento.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finalcart, container, false);

        radioGroup = view.findViewById(R.id.radioGroup);
        rbCreditCard = view.findViewById(R.id.rbCreditCard);
        rbBank = view.findViewById(R.id.rbBank);
        btnCheck = view.findViewById(R.id.btnCheck);
        txtVPrice = view.findViewById(R.id.txtVPrice);
        txtVIVA = view.findViewById(R.id.txtVIVA);
        txtVTotal = view.findViewById(R.id.txtVTotal);

        socket = new Sockets(requireContext(), AppGlobals.socketURL);

        double total = 0;
        for (CarPart part : AppGlobals.cartList) {
            total += part.getPrice();
        }
        double iva = total * 0.21;
        double price = total - iva;
        txtVPrice.setText("Precio: " + String.format("%.2f", price) + " €");
        txtVTotal.setText("Total: " + String.format("%.2f", total) + " €");
        txtVIVA.setText("IVA: " + String.format("%.2f", iva) + " €");

        btnCheck.setOnClickListener(v -> realizarPago());

        return view;
    }

    /**
     * Lógica de pago al pulsar el botón.
     * Verifica el método de pago seleccionado, comunica con el servidor
     * para registrar la actividad y realizar el pago, y si es exitoso,
     * inicia la descarga de la factura.
     */
    private void realizarPago() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(getContext(), "Selecciona un método de pago", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadio = requireView().findViewById(selectedId);
        String metodoPago = selectedRadio.getText().toString();

        CartData datos = new CartData(metodoPago);
        JSONObject dataJSON = datos.getDataJSON();

        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean success = new AtomicBoolean(false);

        new Thread(() -> {
            socket.activity(token, "Realizar pago", latch, success);
            try {
                latch.await();
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        if (success.get()) {
                            CountDownLatch latchPago = new CountDownLatch(1);
                            AtomicBoolean pagoOK = new AtomicBoolean(false);
                            new Thread(() -> {
                                socket.makePayment(dataJSON, latchPago, pagoOK);
                                try {
                                    latchPago.await();
                                    if (isAdded()) {
                                        requireActivity().runOnUiThread(() -> {
                                            if (pagoOK.get()) {
                                                Toast.makeText(getContext(), "Pago realizado correctamente", Toast.LENGTH_LONG).show();
                                                AppGlobals.cartList.clear();
                                                descargarFactura();
                                                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                                                navController.navigate(R.id.nav_search);
                                            } else {
                                                Toast.makeText(getContext(), "Error al procesar el pago", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }).start();
                        } else {
                            Toast.makeText(getContext(), "Error al iniciar la actividad de pago", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Solicita al servidor la descarga de la factura correspondiente al pedido realizado
     * y lanza un intent de descarga mediante el {@code DownloadManager} de Android.
     */
    private void descargarFactura() {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean success = new AtomicBoolean(false);

        new Thread(() -> {
            socket.activity(token, "Realizar descarga fichero", latch, success);
            try {
                latch.await();
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        if (success.get()) {
                            String nombreFactura = "factura_" + id_factura + ".pdf";
                            if (nombreFactura == null || !nombreFactura.matches("^factura_\\d+\\.pdf$")) {
                                Toast.makeText(getContext(), "Nombre de factura no válido", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            facturaURL = facturaURL + nombreFactura;

                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(facturaURL));
                            request.setTitle("Descargando " + nombreFactura);
                            request.setDescription("Factura en proceso de descarga...");
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, nombreFactura);
                            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

                            DownloadManager downloadManager = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);
                            if (downloadManager != null) {
                                downloadManager.enqueue(request);
                                Toast.makeText(getContext(), "Descarga iniciada...", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Error al iniciar la descarga", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Error al descargar la factura", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}