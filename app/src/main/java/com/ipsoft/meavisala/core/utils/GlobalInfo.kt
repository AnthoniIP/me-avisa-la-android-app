package com.ipsoft.meavisala.core.utils

object GlobalInfo {

    object PermissionInfo {

        private val listeners = mutableListOf<OnPermissionListener>()

        var hasPermissions = false
            set(value) {
                notifyListeners()
                field = value
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

        var hasAds = false
            set(value) {
                notifyListeners()
                field = value
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
}
