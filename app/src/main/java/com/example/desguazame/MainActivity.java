package com.example.desguazame;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.Manifest;
import com.example.desguazame.utils.AppGlobals;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.desguazame.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Actividad principal de la aplicación que actúa como punto de entrada para
 * la navegación y la inicialización de componentes clave.
 * <p>
 * Configura el layout principal, la barra de herramientas, el menú lateral
 * (Navigation Drawer), el canal de notificaciones FCM y solicita permisos
 * necesarios en tiempo de ejecución.
 * </p>
 * También gestiona el token de Firebase Cloud Messaging para permitir la
 * recepción de notificaciones push.
 *
 * @author Charlie
 */
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    /**
     * Método llamado al crear la actividad. Se encarga de inflar la vista,
     * configurar el menú lateral, inicializar Firebase y registrar el canal
     * de notificaciones para FCM.
     *
     * @param savedInstanceState Datos guardados previamente, si los hubiera.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
//        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null)
//                        .setAnchorView(R.id.fab).show();
//            }
//        });
        obtenerTokenFcm();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "canal_fcm",
                    "Notificaciones FCM",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_login, R.id.nav_search, R.id.nav_cart)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    /**
     * Solicita el token de Firebase Cloud Messaging (FCM) y lo guarda en una
     * variable global para su uso en notificaciones push.
     */
    private void obtenerTokenFcm() {
        FirebaseApp.initializeApp(this);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Error al conseguir el token", task.getException());
                        return;
                    }

                    // Token FCM obtenido
                    AppGlobals.token_fcm = task.getResult();
                });
    }

    /**
     * Infla el menú superior de la aplicación si está presente.
     *
     * @param menu Menú donde se agregarán los elementos.
     * @return true si el menú se ha creado correctamente.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Permite el comportamiento de navegación superior (flecha atrás) en la
     * barra de herramientas.
     *
     * @return true si la navegación se ha realizado correctamente.
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Actualiza el ítem del menú lateral entre "Iniciar sesión" y "Cerrar sesión"
     * según el estado del usuario.
     *
     * @param isLogin true si el usuario ha iniciado sesión; false si ha cerrado sesión.
     */
    public void actualizarItemLoginLogout(boolean isLogin) {
        NavigationView navView = binding.navView;
        Menu menu = navView.getMenu();
        MenuItem loginItem = menu.findItem(R.id.nav_login);

        if (loginItem != null) {
            if (isLogin) {
                loginItem.setTitle(R.string.menu_logout);
                loginItem.setIcon(R.drawable.ic_logout);
                loginItem.setOnMenuItemClickListener(item -> {
                    AppGlobals.user = null;
                    AppGlobals.token = null;

                    // Restaurar el menú a "Iniciar sesión"
                    actualizarItemLoginLogout(false);

                    // Navegar al login
                    NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
                    NavOptions navOptions = new NavOptions.Builder()
                            .setPopUpTo(R.id.nav_search, true)
                            .build();
                    navController.navigate(R.id.nav_login, null, navOptions);
                    return true;
                });
            } else {
                loginItem.setTitle(R.string.menu_login);
                loginItem.setIcon(R.drawable.ic_login);
                loginItem.setOnMenuItemClickListener(item -> {
                    NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
                    navController.navigate(R.id.nav_login);
                    return true;
                });
            }
        }
    }
}