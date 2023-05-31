package com.ipsoft.meavisala.features.map

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.ipsoft.meavisala.R
import com.ipsoft.meavisala.core.model.AlarmEntity
import com.ipsoft.meavisala.features.alarmedetails.AlarmDetailsViewModel

@Composable
fun MapPinOverlay() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                modifier = Modifier.size(50.dp),
                bitmap = ImageBitmap.imageResource(id = R.drawable.pin).asAndroidBitmap()
                    .asImageBitmap(),
                contentDescription = "Pin Image"
            )
        }
        Box(
            Modifier.weight(1f)
        ) {}
    }
}

@SuppressLint("MissingPermission")
@Composable
fun MapViewContainer(
    isEnabled: Boolean,
    mapView: MapView,
    currentZoom: Float,
    viewModel: AlarmDetailsViewModel = hiltViewModel()
) {
    AndroidView(
        factory = { mapView }
    ) {
        mapView.getMapAsync { map ->

            map.isMyLocationEnabled = true
            map.uiSettings.apply {
                setAllGesturesEnabled(isEnabled)
                isZoomControlsEnabled = true
                isMyLocationButtonEnabled = true
                isCompassEnabled = true
            }

            val currentLocation = viewModel.currentLocation.value
            val position = LatLng(currentLocation.latitude, currentLocation.longitude)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, currentZoom))

            map.setOnCameraIdleListener {
                map.cameraPosition.let {
                    viewModel.updateLocation(
                        it.target.latitude,
                        it.target.longitude
                    )
                    viewModel.updateZoom(it.zoom)
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun HomeMapViewContainer(
    mapView: MapView,
    currentZoom: Float,
    alarm: AlarmEntity
) {
    AndroidView(
        factory = { mapView }
    ) {
        mapView.getMapAsync { map ->

            map.isMyLocationEnabled = false
            map.uiSettings.apply {
                setAllGesturesEnabled(false)
                isZoomControlsEnabled = false
                isMyLocationButtonEnabled = false
                isCompassEnabled = false
            }

            val position = LatLng(alarm.latitude, alarm.longitude)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, currentZoom))
        }
    }
}
