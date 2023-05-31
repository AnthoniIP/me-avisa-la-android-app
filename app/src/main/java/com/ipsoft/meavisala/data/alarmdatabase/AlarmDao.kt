package com.ipsoft.meavisala.data.alarmdatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ipsoft.meavisala.core.model.AlarmEntity

@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarm")
    fun getAll(): List<AlarmEntity>

    @Query("SELECT * FROM alarm WHERE id = :id")
    fun getAlarmById(id: Int): AlarmEntity

    @Insert
    fun insert(alarmEntity: AlarmEntity)

    @Delete
    fun delete(alarmEntity: AlarmEntity)

    @Query("SELECT * FROM alarm WHERE isEnable = 1")
    fun getAllEnabledAlarm(): List<AlarmEntity>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(alarmEntity: AlarmEntity)
}
