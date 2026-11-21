package com.kenai.atlasexpoalert

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object ReminderScheduler {
    const val CHANNEL_ID = "atlas_expo_alerts"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Avisos ATLAS EXPO", NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Avisos antes y al comenzar las actividades"
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    fun scheduleAll(context: Context) {
        createChannel(context)
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val now = System.currentTimeMillis()
        EventData.events.forEachIndexed { index, event ->
            val start = event.timeMillis()
            listOf(15, 10, 5, 0).forEach { minutes ->
                val trigger = start - minutes * 60_000L
                if (trigger <= now) return@forEach
                val broadcast = Intent(context, ReminderReceiver::class.java).apply {
                    putExtra("eventIndex", index)
                    putExtra("minutes", minutes)
                }
                val requestCode = index * 10 + minutes
                val pending = PendingIntent.getBroadcast(
                    context, requestCode, broadcast,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarm.canScheduleExactAlarms()) {
                        alarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pending)
                    } else {
                        alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pending)
                    }
                } catch (_: SecurityException) {
                    alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pending)
                }
            }
        }
    }
}

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val index = intent.getIntExtra("eventIndex", -1)
        val minutes = intent.getIntExtra("minutes", 0)
        val event = EventData.events.getOrNull(index) ?: return
        ReminderScheduler.createChannel(context)
        val headline = when (minutes) {
            0 -> "¡Empieza ahora!"
            5 -> "Empieza en 5 minutos"
            10 -> "Empieza en 10 minutos"
            else -> "Empieza en 15 minutos"
        }
        val details = buildString {
            append(event.title)
            append(" · ")
            append(event.place)
            if (event.presenter.isNotBlank()) {
                append("\n")
                append(event.presenter)
            }
        }
        val notification = NotificationCompat.Builder(context, ReminderScheduler.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("$headline: ATLAS EXPO")
            .setContentText(event.title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(details))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    context, 0, Intent(context, MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()
        try {
            NotificationManagerCompat.from(context).notify(index * 10 + minutes, notification)
        } catch (_: SecurityException) { }
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) ReminderScheduler.scheduleAll(context)
    }
}
