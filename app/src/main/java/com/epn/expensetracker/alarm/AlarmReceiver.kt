package com.epn.expensetracker.alarm


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.epn.expensetracker.MainActivity
import com.epn.expensetracker.R

/**
 * BroadcastReceiver que se ejecuta cuando la alarma se dispara.
 * Funciona incluso con la app completamente cerrada.
 */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        mostrarNotificacion(context)

        // Reprogramar para el día siguiente
        val hora = ReminderPreferences.obtenerHora(context)
        val minuto = ReminderPreferences.obtenerMinuto(context)
        ReminderScheduler.programarRecordatorio(context, hora, minuto)
    }

    private fun mostrarNotificacion(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // IMPORTANTE: Cambia el ID del canal para que Android aplique los nuevos ajustes de sonido
        // Si usas el mismo ID que antes, Android recordará la configuración vieja sin sonido.
        val channelId = "expense_reminder_channel_v2"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Configurar atributos de audio para asegurar que suene
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val canal = NotificationChannel(
                channelId,
                "Recordatorios de Gastos",
                NotificationManager.IMPORTANCE_HIGH // Importancia ALTA para que haga ruido y vibre
            ).apply {
                description = "Recordatorios diarios para registrar gastos"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500) // Patrón: espera 0, vibra 500ms, espera 200ms, vibra 500ms
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes)
            }
            notificationManager.createNotificationChannel(canal)
        }

        // Intent para abrir la app al tocar la notificación
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificacion = NotificationCompat.Builder(context, channelId) // Usa el nuevo ID
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("¿Registraste tus gastos?")
            .setContentText("No olvides anotar lo que gastaste hoy")
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Prioridad alta para versiones viejas de Android
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL) // Activa luces, sonido y vibración por defecto
            .build()

        notificationManager.notify(NOTIFICATION_ID, notificacion)
    }

    companion object {
        const val CHANNEL_ID = "expense_reminder_channel"
        const val NOTIFICATION_ID = 1001
    }
}
