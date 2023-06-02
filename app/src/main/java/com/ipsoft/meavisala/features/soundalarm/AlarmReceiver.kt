package com.ipsoft.meavisala.features.soundalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import com.ipsoft.meavisala.R
import com.ipsoft.meavisala.core.utils.extensions.showNotificationWithFullScreenIntent

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound)
        mediaPlayer.start()
        if (intent.getBooleanExtra(LOCK_SCREEN_KEY, true)) {
            context.showNotificationWithFullScreenIntent(
                true
            )
        } else {
            context.showNotificationWithFullScreenIntent(
                description = intent.getStringExtra(
                    ALARM_DESCRIPTION
                ) ?: ""
            )
        }
    }

    companion object {
        fun build(context: Context, isLockScreen: Boolean): Intent {
            return Intent(context, AlarmReceiver::class.java).also {
                it.putExtra(LOCK_SCREEN_KEY, isLockScreen)
            }
        }
    }
}

private const val LOCK_SCREEN_KEY = "lockScreenKey"
const val ALARM_DESCRIPTION = "Alarm_description"
