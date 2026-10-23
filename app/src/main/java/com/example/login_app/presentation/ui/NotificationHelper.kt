package com.example.login_app.presentation.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.login_app.MainActivity
import com.example.login_app.R

object NotificationHelper {

    private const val TAG = "LOCAL_NOTI"
    private const val CHANNEL_ID = "notificaciones_generales"

    fun mostrarNotificacionBienvenida(context: Context, nombreUsuario: String) {
        crearCanalNotificaciones(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val titulo = "¡Bienvenido a la aplicación!"
        val cuerpo = "Hola $nombreUsuario, nos alegra tenerte de vuelta."

        val notificacion = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setStyle(NotificationCompat.BigTextStyle().bigText(cuerpo))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), notificacion)
            Log.d(TAG, "Notificación de bienvenida mostrada con éxito")
        } catch (error: SecurityException) {
            Log.e(TAG, "No hay permiso para mostrar notificaciones", error)
        }
    }

    private fun crearCanalNotificaciones(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CHANNEL_ID,
                "Notificaciones generales",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Avisos de Mi Aplicación"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(canal)
        }
    }
}