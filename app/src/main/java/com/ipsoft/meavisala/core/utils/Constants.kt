package com.ipsoft.meavisala.core.utils

import androidx.compose.ui.unit.dp

const val NOTIFICATION_CHANNEL_ID = "yZkrd0FCmbMTyibj8lNT"
const val NOTIFICATION_CHANNEL_NAME = "Alarm notifications"

val smallPadding = 4.dp
val mediumPadding = 8.dp
val largePadding = 16.dp

val defaultImageSize = 180.dp
val defaultIpsoftSize = defaultImageSize * 0.5f

enum class Distance(val distance: Long, val stringName: String) {
    ON_LOCAL(500L, "No local"),
    KM1(1000L, "1 km"),
    KM5(5000L, "5 km"),
    KM10(10000L, "10 km"),
    KM20(20000L, "20 km"),
    KM50(50000L, "50 km")
}

val distances = listOf(
    Distance.ON_LOCAL,
    Distance.KM1,
    Distance.KM5,
    Distance.KM10,
    Distance.KM20,
    Distance.KM50
)
