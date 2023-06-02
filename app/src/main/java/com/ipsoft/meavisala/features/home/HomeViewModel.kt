package com.ipsoft.meavisala.features.home

import android.location.Location
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipsoft.meavisala.core.model.AlarmEntity
import com.ipsoft.meavisala.core.utils.GlobalInfo.AdsInfo
import com.ipsoft.meavisala.core.utils.GlobalInfo.AlarmInfo.alarmListUpdated
import com.ipsoft.meavisala.core.utils.GlobalInfo.PermissionInfo
import com.ipsoft.meavisala.core.utils.GlobalInfo.PermissionInfo.OnPermissionListener
import com.ipsoft.meavisala.data.alarmdatabase.repository.AlarmRepository
import com.ipsoft.meavisala.data.datastore.PreferencesDataStore
import com.ipsoft.meavisala.features.backgroundlocation.LocationClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferencesDataStore: PreferencesDataStore,
    private val alarmRepository: AlarmRepository,
    private val locationClient: LocationClient
) : ViewModel(), OnPermissionListener, AdsInfo.OnAdsListener {

    private val _currentLocation = mutableStateOf(Location(""))
    private val _alarms = mutableStateOf(AlarmState())
    private val _hasPermissions = mutableStateOf(false)
    private val _showAds = mutableStateOf(true)

    val hasPermissions: State<Boolean> = _hasPermissions
    val alarms: State<AlarmState> = _alarms
    val showAds: State<Boolean> = _showAds
    val currentLocation: State<Location> = _currentLocation

    init {
        PermissionInfo.addListener(this)
        AdsInfo.addListener(this)
        loadHasPermissions()
        loadShowAds()
        getCurrentLocation()
        getIsAlarmsEnabled()
    }

    private fun getIsAlarmsEnabled() {
        viewModelScope.launch {
            _alarms.value =
                _alarms.value.copy(alarmsEnabled = preferencesDataStore.readIsAlarmsEnabled())
        }
    }

    private fun getCurrentLocation() {
        locationClient.getLocationUpdates(1000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                _currentLocation.value = location
            }.launchIn(viewModelScope)
    }

    fun getAlarms() {
        viewModelScope.launch(Dispatchers.IO) {
            _alarms.value = _alarms.value.copy(alarms = alarmRepository.getAllAlarms())
            alarmListUpdated = _alarms.value.alarms.isNotEmpty()
        }
    }

    private fun loadHasPermissions() {
        viewModelScope.launch {
            _hasPermissions.value = preferencesDataStore.readHasPermissions()
        }
    }

    private fun loadShowAds() {
        viewModelScope.launch {
            _showAds.value = preferencesDataStore.readShowAds()
        }
    }

    fun saveHasPermissions(hasPermissions: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.storeHasPermissions(hasPermissions)
        }
    }

    fun saveIsAlarmsEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.storeIsAlarmsEnabled(isEnabled)
            _alarms.value = _alarms.value.copy(alarmsEnabled = isEnabled)
        }
    }

    override fun onCleared() {
        super.onCleared()
        PermissionInfo.removeListener(this)
        AdsInfo.removeListener(this)
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

    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            alarmRepository.deleteAlarm(alarm)
            getAlarms()
        }
    }

    override fun onAdsUpdated(hasAds: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.storeShowAds(hasAds)
            _showAds.value = hasAds
        }
    }

    data class AlarmState(
        val alarms: List<AlarmEntity> = emptyList(),
        val alarmsEnabled: Boolean = false
    )
}
