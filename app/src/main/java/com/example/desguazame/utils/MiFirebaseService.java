package com.example.desguazame.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.desguazame.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Servicio de Firebase encargado de recibir y mostrar notificaciones push
 * enviadas mediante Firebase Cloud Messaging (FCM).
 * <p>
 * Esta clase extiende {@link FirebaseMessagingService} y sobrescribe el método
 * {@code onMessageReceived} para gestionar la visualización de notificaciones
 * en dispositivos Android.
 * </p>
 *
 * <p>
 * Requiere que el canal de notificaciones {@code canal_fcm} esté previamente
 * creado desde el {@code NotificationManager} para Android 8.0 (API 26) en adelante.
 * </p>
 *
 * <p>
 * Se deben gestionar los permisos de notificación en tiempo de ejecución en
 * versiones recientes de Android (API 33+).
 * </p>
 *
 * @author Charlie
 */
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MiFirebaseService extends FirebaseMessagingService {

    /**
     * Método que se invoca automáticamente cuando se recibe un mensaje push
     * desde Firebase.
     *
     * <p>
     * Extrae el título y cuerpo del mensaje, construye una notificación local
     * y la muestra al usuario si se tiene el permiso necesario.
     * </p>
     *
     * @param remoteMessage El mensaje recibido desde Firebase, que puede
     *                      contener datos o notificaciones.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String titulo = "";
        String mensaje = "";

        if (remoteMessage.getNotification() != null) {
            titulo = remoteMessage.getNotification().getTitle();
            mensaje = remoteMessage.getNotification().getBody();
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "canal_fcm")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }
}
