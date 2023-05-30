package com.ipsoft.meavisala.features.alarmedetails

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.CountDownTimer
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AlarmDetailsViewModel @Inject constructor() : ViewModel() {

    val location = MutableStateFlow(getInitialLocation())
    val addressText = mutableStateOf("")
    var isMapEditable = mutableStateOf(true)
    private var timer: CountDownTimer? = null

    private fun getInitialLocation() : Location{
        val initialLocation = Location("")
        initialLocation.latitude = 51.506874
        initialLocation.longitude = -0.139800
        return initialLocation
    }

    fun updateLocation(latitude: Double, longitude: Double){
        if(latitude != location.value.latitude) {
            val location = Location("")
            location.latitude = latitude
            location.longitude = longitude
            setLocation(location)
        }
    }

    private fun setLocation(loc: Location) {
        location.value = loc
    }

    fun getAddressFromLocation(context: Context): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        var addresses: List<Address>? = null
        var addressText = ""

        try {
            addresses = geocoder.getFromLocation(location.value.latitude, location.value.longitude, 1)
        }catch(ex: Exception){
            ex.printStackTrace()
        }
        if(!addresses.isNullOrEmpty()){
            val address: Address = addresses[0]
            addressText = address.getAddressLine(0) ?: ""
        }





        return addressText
    }

    fun onTextChanged(context: Context, text: String){
        if(text == "")
            return
        timer?.cancel()
        timer = object : CountDownTimer(1000, 1500) {
            override fun onTick(millisUntilFinished: Long) { }
            override fun onFinish() {
                location.value = getLocationFromAddress(context, text)
            }
        }.start()
    }

    fun getLocationFromAddress(context: Context, strAddress: String): Location{
        val geocoder = Geocoder(context, Locale.getDefault())
        val address: Address?

        val addresses: List<Address>? = geocoder.getFromLocationName(strAddress, 1)

        if (addresses != null) {
            if (addresses.isNotEmpty()) {
                address = addresses[0]

                val loc = Location("")
                loc.latitude = address.latitude
                loc.longitude = address.longitude
                return loc
            }
        }

        return location.value
    }

}
