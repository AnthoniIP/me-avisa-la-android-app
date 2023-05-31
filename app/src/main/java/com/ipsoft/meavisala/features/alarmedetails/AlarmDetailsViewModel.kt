package com.ipsoft.meavisala.features.alarmedetails

import android.location.Location
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipsoft.meavisala.core.model.AlarmEntity
import com.ipsoft.meavisala.core.utils.Distance
import com.ipsoft.meavisala.data.alarmdatabase.repository.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmDetailsViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository
) : ViewModel() {

    private val _currentLocation = MutableStateFlow(getInitialLocation())
    private val _isMapEditable = mutableStateOf(true)
    private val _selectedDistance = mutableStateOf(Distance.ON_LOCAL)
    private val _currentZoom = mutableStateOf(3f)
    private val _notificationText = mutableStateOf("")
    private val _creationSuccess = mutableStateOf(false)

    val currentLocation: StateFlow<Location> = _currentLocation
    val isMapEditable: State<Boolean> = _isMapEditable
    val selectedDistance: State<Distance> = _selectedDistance
    val currentZoom: State<Float> = _currentZoom
    val notificationText: State<String> = _notificationText
    val creationSuccess: State<Boolean> = _creationSuccess

    fun updateZoom(zoom: Float) {
        _currentZoom.value = zoom
    }

    fun onDistanceSelected(distance: Distance) {
        _selectedDistance.value = distance
    }

    fun updateLocation(latitude: Double, longitude: Double) {
        val location = Location("")
        location.latitude = latitude
        location.longitude = longitude
        setLocation(location)
    }

    fun saveAlarm() {
        viewModelScope.launch(Dispatchers.IO) {
            alarmRepository.insertAlarm(
                alarmEntity = AlarmEntity(
                    latitude = _currentLocation.value.latitude,
                    longitude = _currentLocation.value.longitude,
                    minDistanceToNotify = _selectedDistance.value.distance,
                    notificationText = _notificationText.value,
                    isEnable = true,
                    minDistanceToNotifyText = _selectedDistance.value.stringName
                )
            )
            _creationSuccess.value = true
        }
    }

    private fun getInitialLocation(): Location {
        // middle of brazil
        val location = Location("")
        location.latitude = -14.2400732
        location.longitude = -53.1805017
        return location
    }

    private fun setLocation(loc: Location) {
        _currentLocation.value = loc
    }

    fun updateNotificationText(newText: String) {
        _notificationText.value = newText
    }
}
