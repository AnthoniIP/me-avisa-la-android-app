package com.ipsoft.meavisala.features.soundalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import com.ipsoft.meavisala.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val mediaPlayer = MediaPlayer.create(
            context,
            R.raw.alarm_sound
        ) // Substitua "alarm_sound" pelo nome do seu arquivo de áudio na pasta "res/raw"
        mediaPlayer.start()
    }
}
