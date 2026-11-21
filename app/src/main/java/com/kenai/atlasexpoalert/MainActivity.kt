package com.kenai.atlasexpoalert

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : Activity() {
    private val tz = TimeZone.getTimeZone("Europe/Madrid")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ReminderScheduler.createChannel(this)

        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 28, 24, 24)
            setBackgroundColor(0xFF101827.toInt())
        }
        val title = TextView(this).apply {
            text = "ATLAS EXPO ALERT"
            textSize = 27f
            setTextColor(0xFFFFFFFF.toInt())
            gravity = Gravity.CENTER
        }
        val subtitle = TextView(this).apply {
            text = "21–22 NOVIEMBRE 2026 · HORA DE ESPAÑA"
            textSize = 13f
            setTextColor(0xFF9DB4D0.toInt())
            gravity = Gravity.CENTER
            setPadding(0, 10, 0, 20)
        }
        val activate = Button(this).apply {
            text = "ACTIVAR TODOS LOS AVISOS"
            setOnClickListener { requestPermissionsAndSchedule() }
        }
        val info = TextView(this).apply {
            text = "Recibirás avisos 15, 10 y 5 minutos antes, y al comenzar cada actividad. Permite las notificaciones y las alarmas exactas si Android lo solicita."
            textSize = 14f
            setTextColor(0xFFE2E8F0.toInt())
            setPadding(4, 12, 4, 20)
        }
        content.addView(title)
        content.addView(subtitle)
        content.addView(activate)
        content.addView(info)

        val format = SimpleDateFormat("EEE d MMM · HH:mm", Locale("es", "ES")).apply { timeZone = tz }
        var lastDay = -1
        EventData.events.forEach { event ->
            if (event.day != lastDay) {
                lastDay = event.day
                val dayTitle = TextView(this).apply {
                    text = when (event.day) {
                        21 -> "SÁBADO 21 NOVIEMBRE"
                        22 -> "DOMINGO 22 NOVIEMBRE"
                        else -> "LUNES 23 NOVIEMBRE"
                    }
                    textSize = 17f
                    setTextColor(0xFF60A5FA.toInt())
                    setPadding(0, 18, 0, 8)
                }
                content.addView(dayTitle)
            }
            val card = TextView(this).apply {
                text = "${format.format(Date(event.timeMillis()))}\n${event.title}\n${event.place}" +
                    if (event.presenter.isNotBlank()) "\n${event.presenter}" else ""
                textSize = 14f
                setTextColor(0xFFFFFFFF.toInt())
                setBackgroundColor(0xFF1E293B.toInt())
                setPadding(14, 12, 14, 12)
            }
            content.addView(card, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = 8 })
        }
        scroll.addView(content)
        setContentView(scroll)

        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
        }
    }

    private fun requestPermissionsAndSchedule() {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarm = getSystemService(ALARM_SERVICE) as AlarmManager
            if (!alarm.canScheduleExactAlarms()) {
                try {
                    startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:$packageName")))
                } catch (_: Exception) { }
            }
        }
        ReminderScheduler.scheduleAll(this)
    }

    override fun onResume() {
        super.onResume()
        ReminderScheduler.scheduleAll(this)
    }
}
