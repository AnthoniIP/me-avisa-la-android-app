package com.ipsoft.meavisala.core.utils.extensions

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.ipsoft.meavisala.R
import com.ipsoft.meavisala.core.utils.NOTIFICATION_CHANNEL_ID
import com.ipsoft.meavisala.core.utils.NOTIFICATION_CHANNEL_NAME
import com.ipsoft.meavisala.features.fullscreen.FullScreenActivity
import com.ipsoft.meavisala.features.lockscreen.LockScreenActivity

fun Context.getVerCode(): String {
    return try {
        val pInfo = this.packageManager.getPackageInfo(this.packageName, 0)
        pInfo.versionName
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

fun Context.showNotificationWithFullScreenIntent(
    isLockScreen: Boolean = false,
    channelId: String = NOTIFICATION_CHANNEL_ID,
    title: String = "Você chegou em um destino!",
    description: String = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."

) {
    val builder = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(R.drawable.ic_location)
        .setContentTitle(title)
        .setContentText(description)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setFullScreenIntent(getFullScreenIntent(isLockScreen), true)

    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    with(notificationManager) {
        buildChannel()

        val notification = builder.build()

        notify(0, notification)
    }
}

private fun NotificationManager.buildChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = NOTIFICATION_CHANNEL_NAME
        val descriptionText = "Location arrived Notification"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        createNotificationChannel(channel)
    }
}

private fun Context.getFullScreenIntent(isLockScreen: Boolean): PendingIntent {
    val destination = if (isLockScreen) {
        LockScreenActivity::class.java
    } else {
        FullScreenActivity::class.java
    }
    val intent = Intent(this, destination)

    // flags and request code are 0 for the purpose of demonstration
    return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
}

fun Context.hasLocationPermission(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
