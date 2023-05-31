package com.ipsoft.meavisala.data.alarmdatabase.repository

import com.ipsoft.meavisala.core.model.AlarmEntity

interface AlarmRepository {
    suspend fun getAllAlarms(): List<AlarmEntity>
    suspend fun getAlarmById(id: Int): AlarmEntity
    suspend fun insertAlarm(alarmEntity: AlarmEntity)
    suspend fun deleteAlarm(alarmEntity: AlarmEntity)
    suspend fun getAllEnabledAlarms(): List<AlarmEntity>
    suspend fun updateAlarm(alarmEntity: AlarmEntity)
}
