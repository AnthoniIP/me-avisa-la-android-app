package com.ipsoft.meavisala.features.lockscreen

import android.app.Activity
import android.os.Bundle
import com.ipsoft.meavisala.R
import com.ipsoft.meavisala.core.utils.extensions.turnScreenOffAndKeyguardOn
import com.ipsoft.meavisala.core.utils.extensions.turnScreenOnAndKeyguardOff

class LockScreenActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen)
        turnScreenOnAndKeyguardOff()
    }

    override fun onDestroy() {
        super.onDestroy()
        turnScreenOffAndKeyguardOn()
    }
}
