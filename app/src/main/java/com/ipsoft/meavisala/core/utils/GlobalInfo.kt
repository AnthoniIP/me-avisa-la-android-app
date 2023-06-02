package com.ipsoft.meavisala.core.utils

object GlobalInfo {

    object PermissionInfo {

        private val listeners = mutableListOf<OnPermissionListener>()

        var hasPermissions = false
            set(value) {
                field = value
                notifyListeners()
            }

        private fun notifyListeners() {
            listeners.forEach { it.onPermissionUpdated(hasPermissions) }
        }

        fun addListener(listener: OnPermissionListener) {
            listeners.add(listener)
        }

        fun removeListener(listener: OnPermissionListener) {
            listeners.remove(listener)
        }

        interface OnPermissionListener {
            fun onPermissionUpdated(hasPermissions: Boolean)
        }
    }

    object AdsInfo {
        private val listeners = mutableListOf<OnAdsListener>()

        var hasAds = true
            set(value) {
                field = value
                notifyListeners()
            }

        private fun notifyListeners() {
            listeners.forEach { it.onAdsUpdated(hasAds) }
        }

        fun addListener(listener: OnAdsListener) {
            listeners.add(listener)
        }

        fun removeListener(listener: OnAdsListener) {
            listeners.remove(listener)
        }

        interface OnAdsListener {
            fun onAdsUpdated(hasAds: Boolean)
        }
    }

    object AlarmInfo {
        private val listeners = mutableListOf<OnAlarmListener>()

        var alarmListUpdated = false
            set(value) {
                field = value
                notifyListeners()
            }

        private fun notifyListeners() {
            listeners.forEach { it.onAlarmUpdated(alarmListUpdated) }
        }

        fun addListener(listener: OnAlarmListener) {
            listeners.add(listener)
        }

        fun removeListener(listener: OnAlarmListener) {
            listeners.remove(listener)
        }

        interface OnAlarmListener {
            fun onAlarmUpdated(hasAlarm: Boolean)
        }
    }
}
