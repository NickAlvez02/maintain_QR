package com.techapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.techapp.ui.MainActivity
import com.techapp.R

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Verificamos si el mensaje trae notificación
        remoteMessage.notification?.let {
            showNotification(it.title, it.body)
        }
        
        // También puede traer datos adicionales
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("FCM", "Datos recibidos: ${remoteMessage.data}")
            // Si el título/body vienen en los datos en lugar de la notificación
            if (remoteMessage.notification == null) {
                showNotification(remoteMessage.data["title"], remoteMessage.data["body"])
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Nuevo token generado: $token")
        // El LoginViewModel se encarga de enviarlo al servidor al iniciar sesión
    }

    private fun showNotification(title: String?, message: String?) {
        val channelId = "default_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal para Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Notificaciones de Órdenes",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para avisos de nuevas órdenes y actualizaciones"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intento para abrir la app (MainActivity)
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construir la notificación visual
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_orders) // Usamos el icono de órdenes que ya tienes
            .setContentTitle(title ?: "MaintQR Update")
            .setContentText(message ?: "Tienes una nueva actualización en tus órdenes.")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}
