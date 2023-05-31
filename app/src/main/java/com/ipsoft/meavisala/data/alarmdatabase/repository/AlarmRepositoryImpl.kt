package com.ipsoft.meavisala.data.alarmdatabase.repository

import com.ipsoft.meavisala.core.model.AlarmEntity
import com.ipsoft.meavisala.data.alarmdatabase.AlarmDatabase
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    alarmRepository: AlarmDatabase
) : AlarmRepository {

    private val alarmDao = alarmRepository.alarmDao()

    override suspend fun getAllAlarms(): List<AlarmEntity> = alarmDao.getAll()
    override suspend fun getAlarmById(id: Int): AlarmEntity = alarmDao.getAlarmById(id)
    override suspend fun insertAlarm(alarmEntity: AlarmEntity) = alarmDao.insert(alarmEntity)
    override suspend fun deleteAlarm(alarmEntity: AlarmEntity) = alarmDao.delete(alarmEntity)
    override suspend fun getAllEnabledAlarms(): List<AlarmEntity> = alarmDao.getAllEnabledAlarm()
    override suspend fun updateAlarm(alarmEntity: AlarmEntity) = alarmDao.update(alarmEntity)
}
