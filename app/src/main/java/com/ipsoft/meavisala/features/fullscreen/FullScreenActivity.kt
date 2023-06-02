package com.ipsoft.meavisala.features.fullscreen

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.ipsoft.meavisala.R
import com.ipsoft.meavisala.features.soundalarm.ALARM_DESCRIPTION

class FullScreenActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen)
    }

    override fun onResume() {
        super.onResume()
        intent.getStringExtra(ALARM_DESCRIPTION)?.let {
            findViewById<TextView>(R.id.alarm_description)?.apply {
                text = it
            }
        }
        findViewById<Button>(R.id.ok_button)?.setOnClickListener {
            finish()
        }
    }
}
