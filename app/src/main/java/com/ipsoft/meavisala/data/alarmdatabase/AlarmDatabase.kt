package com.ipsoft.meavisala.data.alarmdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ipsoft.meavisala.core.model.AlarmEntity
import com.ipsoft.meavisala.data.alarmdatabase.AlarmDatabase.Companion.DATABASE_VERSION

@Database(entities = [AlarmEntity::class], version = DATABASE_VERSION)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao

    companion object {
        const val DATABASE_NAME = "alarm_database"
        const val DATABASE_VERSION = 2
    }
}
