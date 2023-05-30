package com.ipsoft.meavisala.features.alarmedetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.ipsoft.meavisala.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmDetailsScreen(
    alarmAction: Int,
    viewModel: AlarmDetailsViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.add_new_alarm))
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back),
                        modifier = Modifier.clickable { onBackClick() }
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            MapAddressPickerView(viewModel = viewModel)
        }
    }
}

@Composable
fun MapAddressPickerView(viewModel: AlarmDetailsViewModel) {
    Surface(color = MaterialTheme.colorScheme.background) {
        val mapView = rememberMapViewWithLifecycle()

        Column(Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.select_place_on_map),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(10.dp)
            )
            Box(modifier = Modifier.height(300.dp)) {
                MapViewContainer(viewModel.isMapEditable.value, mapView, viewModel)

                MapPinOverlay()
            }
        }
    }
}

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

@Composable
private fun MapViewContainer(
    isEnabled: Boolean,
    mapView: MapView,
    viewModel: AlarmDetailsViewModel
) {
    AndroidView(
        factory = { mapView }
    ) {
        mapView.getMapAsync { map ->

            map.uiSettings.setAllGesturesEnabled(isEnabled)

            val currentLocation = viewModel.currentLocation.value
            val position = LatLng(currentLocation.latitude, currentLocation.longitude)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 5f))

            map.setOnCameraIdleListener {
                val cameraPosition = map.cameraPosition
                viewModel.updateLocation(
                    cameraPosition.target.latitude,
                    cameraPosition.target.longitude
                )
            }
        }
    }
}

enum class AlarmDetailsScreenActions(i: Int) {
    CREATE(0), EDIT(1)
}
