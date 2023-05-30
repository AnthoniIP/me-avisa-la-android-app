package com.ipsoft.meavisala.core.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.ipsoft.meavisala.R

sealed class Screen(
    val route: String,
    @Suppress("unused")
    @StringRes
    val resourceId: Int,
    val icon: ImageVector
) {
    object Home : Screen("home", R.string.home, Icons.Filled.Home)
    object AlarmDetails : Screen("alarm_details", R.string.cart, Icons.Filled.DateRange)
}
