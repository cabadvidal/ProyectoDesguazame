package com.example.desguazame.ui.login;

import static com.example.desguazame.utils.AppGlobals.socketURL;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.desguazame.MainActivity;
import com.example.desguazame.databinding.FragmentLoginBinding;
import android.widget.Toast;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.desguazame.R;
import com.example.desguazame.utils.Sockets;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Fragmento encargado de gestionar el inicio de sesión del usuario en la aplicación.
 * Permite introducir las credenciales, autenticar al usuario mediante el servidor
 * y, en caso de éxito, redirigir al fragmento principal de búsqueda.
 * Utiliza una instancia de {@link Sockets} para realizar la autenticación remota.
 *
 * @author Charlie
 */
public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private EditText texMail;
    private EditText textPass;
    private Button btnLogin;

    private Sockets socket;

    /**
     * Crea e infla la vista del fragmento, enlaza los elementos de la interfaz
     * y configura el botón de inicio de sesión.
     *
     * @param inflater El LayoutInflater usado para inflar el layout del fragmento.
     * @param container El contenedor padre del fragmento.
     * @param savedInstanceState Estado previamente guardado (si existe).
     * @return La vista raíz del fragmento.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        texMail = binding.texMail;
        textPass = binding.textPass;
        btnLogin = binding.button;

        socket = new Sockets(requireContext(), socketURL);

        btnLogin.setOnClickListener(v -> autenticarUsuario());

        return root;
    }

    /**
     * Realiza el proceso de autenticación del usuario.
     * Verifica que los campos no estén vacíos y lanza una petición al servidor mediante Socket.IO.
     * Si la autenticación es exitosa, navega al fragmento principal y actualiza la interfaz principal.
     * En caso contrario, muestra un mensaje de error.
     */
    private void autenticarUsuario() {
        String email = texMail.getText().toString().trim();
        String pass = textPass.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(getContext(), "Introduce usuario y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, String> campos = new HashMap<>();
        campos.put("usuario", email);
        campos.put("password", pass);

        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean success = new AtomicBoolean(false);

        new Thread(() -> {
            socket.authUser(campos, latch, success);
            if (isAdded()) {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                requireActivity().runOnUiThread(() -> {
                    if (success.get()) {
                        NavController navController = Navigation.findNavController(requireView());
                        navController.navigate(R.id.nav_search);
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).actualizarItemLoginLogout(true);
                        }
                    } else {
                        Toast.makeText(getContext(), "Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).start();
    }

    /**
     * Libera el binding al destruir la vista del fragmento para evitar pérdidas de memoria.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
