package com.ipsoft.meavisala.core.model

import android.location.Location
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val minDistanceToNotify: Long,
    val notificationText: String,
    val isEnabled: Boolean,
    val minDistanceToNotifyText: String
) {
    fun getDistanceInMeters(location: Location): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            this.latitude,
            this.longitude,
            location.latitude,
            location.longitude,
            results
        )
        return results[0]
    }
}
