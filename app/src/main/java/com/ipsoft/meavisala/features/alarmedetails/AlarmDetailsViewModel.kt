package com.ipsoft.meavisala.features.alarmedetails

import android.location.Location
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipsoft.meavisala.core.utils.Distance
import com.ipsoft.meavisala.features.backgroundlocation.LocationClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmDetailsViewModel @Inject constructor(private val locationClient: LocationClient) :
    ViewModel() {

    private val _currentLocation = MutableStateFlow(getInitialLocation())
    private val _isMapEditable = mutableStateOf(true)
    private val _selectedDistance = mutableStateOf(Distance.ON_LOCAL)
    private val _currentZoom = mutableStateOf(3f)

    val currentLocation: StateFlow<Location> = _currentLocation
    val isMapEditable: State<Boolean> = _isMapEditable
    val selectedDistance: State<Distance> = _selectedDistance
    val currentZoom: State<Float> = _currentZoom

    init {
        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        viewModelScope.launch {
            locationClient.getLocationUpdates(1000L)
                .catch { e -> e.printStackTrace() }
                .onEach { location ->
                    setLocation(location)
                }
                .launchIn(viewModelScope)
        }
    }

    private fun getInitialLocation(): Location {
        val location = Location("")
        location.latitude = -14.2400732
        location.longitude = -53.1805017
        return location
    }

    fun updateZoom(zoom: Float) {
        _currentZoom.value = zoom
    }

    fun updateLocation(latitude: Double, longitude: Double) {
        val location = Location("")
        location.latitude = latitude
        location.longitude = longitude
        setLocation(location)
    }

    private fun setLocation(loc: Location) {
        _currentLocation.value = loc
    }

    fun onDistanceSelected(distance: Distance) {
        _selectedDistance.value = distance
    }
}
