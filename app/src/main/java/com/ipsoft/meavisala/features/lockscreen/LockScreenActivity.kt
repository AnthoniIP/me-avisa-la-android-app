package com.ipsoft.meavisala.features.lockscreen

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.ipsoft.meavisala.R
import com.ipsoft.meavisala.core.utils.extensions.turnScreenOffAndKeyguardOn
import com.ipsoft.meavisala.core.utils.extensions.turnScreenOnAndKeyguardOff
import com.ipsoft.meavisala.features.soundalarm.ALARM_DESCRIPTION

class LockScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen)
        turnScreenOnAndKeyguardOff()
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

    override fun onDestroy() {
        super.onDestroy()
        turnScreenOffAndKeyguardOn()
    }
}
