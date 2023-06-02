package com.ipsoft.meavisala.features.backgroundlocation

import android.app.AlarmManager
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
import com.ipsoft.meavisala.features.soundalarm.ALARM_DESCRIPTION
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
            ACTION_UPDATE -> updateAlarms()
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
            .setContentText(getString(R.string.we_will_notify_you_when_you_are_near))
            .setSmallIcon(R.drawable.ic_location)
            .setOngoing(true)

        locationClient.getLocationUpdates(UPDATE_INTERVAL)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                Timber.d("Location: $location")
                saveAccessAlarms.forEach { alarm ->
                    val distance = alarm.getDistanceInMeters(location)
                    Timber.d("Actual Distance: $distance - Min Distance: ${alarm.minDistanceToNotify}")
                    if (distance <= alarm.minDistanceToNotify) {
                        ringAlarm(alarm)
                        alarmRepository.updateAlarm(alarm.copy(isEnabled = false))
                    }
                }
            }
            .launchIn(serviceScope)

        startForeground(1, notification.build())
    }

    private fun ringAlarm(alarm: AlarmEntity) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra(ALARM_DESCRIPTION, alarm.notificationText)
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
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_UPDATE = "ACTION_UPDATE"
        const val UPDATE_INTERVAL = 5000L
    }
}
