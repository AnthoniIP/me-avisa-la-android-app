package com.ipsoft.meavisala.features.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipsoft.meavisala.core.model.AlarmEntity
import com.ipsoft.meavisala.core.utils.PermissionInfo
import com.ipsoft.meavisala.data.alarmdatabase.repository.AlarmRepository
import com.ipsoft.meavisala.data.datastore.PreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferencesDataStore: PreferencesDataStore,
    private val alarmRepository: AlarmRepository
) : ViewModel(), PermissionInfo.OnPermissionListener {

    private val _alarms = mutableStateOf(AlarmState())
    private val _hasPermissions = mutableStateOf(false)

    val hasPermissions: State<Boolean> = _hasPermissions
    val alarms: State<AlarmState> = _alarms

    init {
        PermissionInfo.addListener(this)
        loadHasPermissions()
    }

    fun getAlarms() {
        viewModelScope.launch(Dispatchers.IO) {
            _alarms.value = AlarmState(alarmRepository.getAllAlarms())
        }
    }

    private fun loadHasPermissions() {
        viewModelScope.launch {
            _hasPermissions.value = preferencesDataStore.readHasPermissions()
        }
    }

    fun saveHasPermissions(hasPermissions: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.storeHasPermissions(hasPermissions)
        }
    }

    override fun onCleared() {
        super.onCleared()
        PermissionInfo.removeListener(this)
    }

    override fun onPermissionUpdated(hasPermissions: Boolean) {
        _hasPermissions.value = hasPermissions
    }

    fun updateAlarm(alarmEntity: AlarmEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            alarmRepository.updateAlarm(alarmEntity)
            getAlarms()
        }
    }

    data class AlarmState(
        val alarms: List<AlarmEntity> = emptyList()
    )
}
