package com.ipsoft.meavisala.core.utils

const val NOTIFICATION_CHANNEL_ID = "yZkrd0FCmbMTyibj8lNT"
const val NOTIFICATION_CHANNEL_NAME = "Alarm notifications"

enum class Distance(val distance: Int, val stringName: String) {
    ON_LOCAL(0, "No local"),
    KM1(1000, "1 km"),
    KM5(5000, "5 km"),
    KM10(10000, "10 km"),
    KM20(20000, "20 km"),
    KM50(50000, "50 km")
}

val distances = listOf(
    Distance.ON_LOCAL,
    Distance.KM1,
    Distance.KM5,
    Distance.KM10,
    Distance.KM20,
    Distance.KM50
)
