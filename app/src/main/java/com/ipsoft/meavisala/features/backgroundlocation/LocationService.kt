package com.ipsoft.meavisala.features.backgroundlocation

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.ipsoft.meavisala.R
import com.ipsoft.meavisala.core.model.AlarmEntity
import com.ipsoft.meavisala.core.utils.NOTIFICATION_CHANNEL_ID
import com.ipsoft.meavisala.data.alarmdatabase.repository.AlarmRepository
import com.ipsoft.meavisala.features.soundalarm.AlarmReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Inject
    lateinit var locationClient: LocationClient

    @Inject
    lateinit var alarmRepository: AlarmRepository

    private val alarms = mutableListOf<AlarmEntity>()
    private val saveAccessAlarms: List<AlarmEntity>
        get() = alarms.toList()

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun updateAlarms() {
        serviceScope.launch {
            val newAlarms = alarmRepository.getAllEnabledAlarms()
            alarms.clear()
            alarms.addAll(newAlarms)
        }
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.background_location_notification_title))
            .setSmallIcon(R.drawable.ic_location)
            .setOngoing(true)

        val notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        locationClient.getLocationUpdates(1000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val updatedNotification = notification.setContentText(
                    getString(R.string.we_will_notify_you_when_you_are_near)
                )
                updateAlarms()
                Timber.d("Location: $location")
                saveAccessAlarms.forEach { alarm ->
                    val distance = alarm.getDistanceInMeters(location)
                    Timber.d("Actual Distance: $distance - Min Distance: ${alarm.minDistanceToNotify}")
                    if (distance <= alarm.minDistanceToNotify) {
                        ringAlarm()
                        updatedNotification.setContentTitle(
                            getString(
                                R.string.background_location_reached_notification_title
                            )
                        )
                        updatedNotification.setContentText(
                            alarm.notificationText
                        )
                        updatedNotification.setOngoing(false)
                        stop()
                    }
                }
                notificationManager.notify(1, updatedNotification.build())
            }
            .launchIn(serviceScope)

        startForeground(1, notification.build())
    }

    private fun ringAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val currentTime = System.currentTimeMillis()

        alarmManager.set(AlarmManager.RTC_WAKEUP, currentTime, pendingIntent)
    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}
