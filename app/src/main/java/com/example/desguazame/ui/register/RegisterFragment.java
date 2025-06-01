package com.example.desguazame.ui.register;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.example.desguazame.R;
import com.example.desguazame.utils.AppGlobals;
import com.example.desguazame.utils.DataVerify;
import com.example.desguazame.utils.Sockets;
import com.google.android.material.textfield.TextInputEditText;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Fragmento encargado del registro de nuevos usuarios en la aplicación.
 * Recoge los datos introducidos en el formulario, los valida y, si son correctos,
 * los envía al servidor mediante una conexión Socket.IO para su registro.
 * En caso de éxito, redirige al fragmento de inicio de sesión.
 *
 * Utiliza la clase {@link Sockets} para la comunicación con el backend
 * y la clase {@link DataVerify} para la validación de datos.
 *
 * @author Charlie
 */
public class RegisterFragment extends Fragment {

    private TextInputEditText txtNombre, txtApellido_A, txtApellido_B, txtDireccion,
            txtCP, txtCiudad, txtMunicipio, txtTelefono, txtMovil, txtMail, txtDNI,
            txtBank, txtCreditCard, txtPass, txtPass2;
    private Button btnRegistrar;
    private Sockets socket;

    /**
     * Infla el layout del fragmento y enlaza los elementos del formulario.
     * Inicializa la instancia de {@link Sockets} y configura el botón de registro.
     *
     * @param inflater El LayoutInflater para inflar el layout.
     * @param container El contenedor padre del fragmento.
     * @param savedInstanceState Estado previamente guardado (si existe).
     * @return Vista raíz del fragmento.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        txtNombre = view.findViewById(R.id.txtNombre);
        txtApellido_A = view.findViewById(R.id.txtApellido_A);
        txtApellido_B = view.findViewById(R.id.txtApellido_B);
        txtDireccion = view.findViewById(R.id.txtDireccion);
        txtCP = view.findViewById(R.id.txtCP);
        txtCiudad = view.findViewById(R.id.txtCiudad);
        txtMunicipio = view.findViewById(R.id.txtMunicipio);
        txtTelefono = view.findViewById(R.id.txtTelefono);
        txtMovil = view.findViewById(R.id.txtMovil);
        txtMail = view.findViewById(R.id.txtMail);
        txtDNI = view.findViewById(R.id.txtDNI);
        txtBank = view.findViewById(R.id.txtBank);
        txtCreditCard = view.findViewById(R.id.txtCreditCard);
        txtPass = view.findViewById(R.id.txtPass);
        txtPass2 = view.findViewById(R.id.txtPass2);
        btnRegistrar = view.findViewById(R.id.btnRegistrar);

        socket = new Sockets(requireContext(), AppGlobals.socketURL);

        btnRegistrar.setOnClickListener(v -> registrarUsuario());

        return view;
    }

    /**
     * Valida los datos introducidos por el usuario y, si son correctos, realiza
     * el registro mediante una llamada al servidor con Socket.IO.
     * Verifica:
     * <ul>
     *     <li>Que las contraseñas coincidan.</li>
     *     <li>Que todos los campos sean válidos mediante {@link DataVerify}.</li>
     * </ul>
     * Si el registro es exitoso, muestra un mensaje de confirmación
     * y navega al fragmento de inicio de sesión.
     */
    private void registrarUsuario() {
        HashMap<String, String> campos = new HashMap<>();

        campos.put("NOMBRE", txtNombre.getText().toString().trim());
        campos.put("APELLIDO_A", txtApellido_A.getText().toString().trim());
        campos.put("APELLIDO_B", txtApellido_B.getText().toString().trim());
        campos.put("DIRECCION", txtDireccion.getText().toString().trim());
        campos.put("CODIGO_POSTAL", txtCP.getText().toString().trim());
        campos.put("CIUDAD", txtCiudad.getText().toString().trim());
        campos.put("MUNICIPIO", txtMunicipio.getText().toString().trim());
        campos.put("TELEFONO", txtTelefono.getText().toString().trim().replace(" ", ""));
        campos.put("MOVIL", txtMovil.getText().toString().trim().replace(" ", ""));
        campos.put("MAIL", txtMail.getText().toString().trim());
        campos.put("DNI", txtDNI.getText().toString().trim());
        campos.put("NUMERO_CUENTA", txtBank.getText().toString().trim().replace(" ", ""));
        campos.put("TARJETA_CREDITO", txtCreditCard.getText().toString().trim().replace(" ", ""));
        campos.put("CONTRASENA", txtPass.getText().toString());

        if (!campos.get("CONTRASENA").equals(txtPass2.getText().toString())) {
            Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Map.Entry<String, String> entry : campos.entrySet()) {
            String clave = entry.getKey();
            String valor = entry.getValue();
            if (!DataVerify.verificarDato(clave, valor)) {
                Toast.makeText(getContext(), "Dato no válido: " + clave + " → " + valor, Toast.LENGTH_LONG).show();
                return;
            }
        }

        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean success = new AtomicBoolean(false);

        new Thread(() -> {
            socket.registerUser(campos, latch, success);
            try {
                if (isAdded()) {
                    if (!latch.await(20, TimeUnit.SECONDS)) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Timeout: sin respuesta del servidor", Toast.LENGTH_LONG).show());
                        return;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (success.get()) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Usuario registrado correctamente", Toast.LENGTH_LONG).show();
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                    navController.navigate(R.id.nav_login);
                });
            }
        }).start();
    }
}