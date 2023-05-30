package com.ipsoft.meavisala.features.alarmedetails

import android.location.Location
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _currentLocation = MutableStateFlow(Location(""))
    private val _isMapEditable = mutableStateOf(true)

    val currentLocation: StateFlow<Location> = _currentLocation
    val isMapEditable = _isMapEditable

    init {
        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        viewModelScope.launch {
            locationClient.getLocationUpdates(1000L)
                .catch { e -> e.printStackTrace() }
                .onEach { location ->
                    _currentLocation.value = location
                }
                .launchIn(viewModelScope)
        }
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
//
//    fun getAddressFromLocation(context: Context): String {
//        val geocoder = Geocoder(context, Locale.getDefault())
//        var addresses: List<Address>? = null
//        var addressText = ""
//
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                geocoder.getFromLocation(
//                    location.value.latitude, location.value.longitude, 1
//                ) { addresses = it }
//            } else {
//                addresses = geocoder.getFromLocation(
//                    location.value.latitude, location.value.longitude, 1
//                )
//            }
//
//        } catch (ex: Exception) {
//            ex.printStackTrace()
//        }
//        if (!addresses.isNullOrEmpty()) {
//            addressText = addresses?.get(0)?.getAddressLine(0) ?: ""
//        }
//        return addressText
//    }
}
