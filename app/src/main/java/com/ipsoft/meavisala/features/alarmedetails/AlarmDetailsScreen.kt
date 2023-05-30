package com.ipsoft.meavisala.features.alarmedetails

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
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
import com.ipsoft.meavisala.core.utils.Distance
import com.ipsoft.meavisala.core.utils.distances
import com.ipsoft.meavisala.features.ads.BannerAdView
import com.ipsoft.meavisala.features.verticalscrolllayout.ChildLayout
import com.ipsoft.meavisala.features.verticalscrolllayout.VerticalScrollLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmDetailsScreen(
    viewModel: AlarmDetailsViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val selectedDistance = viewModel.selectedDistance.value
    val currentZoom = viewModel.currentZoom.value

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
        val mapView = rememberMapViewWithLifecycle()
        VerticalScrollLayout(
            modifier = Modifier.padding(paddingValues),
            ChildLayout(
                content = {
                    BannerAdView()
                }
            ),
            ChildLayout(
                content = {
                    Text(
                        text = stringResource(id = R.string.select_place_on_map),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            ),
            ChildLayout(
                content = {
                    Box(modifier = Modifier.height(300.dp)) {
                        MapViewContainer(
                            viewModel.isMapEditable.value,
                            mapView,
                            currentZoom,
                            viewModel
                        )
                        MapPinOverlay()
                    }
                }
            ),
            ChildLayout(
                content = {
                    DistanceSelector(
                        onDistanceSelected = { distance ->
                            viewModel.onDistanceSelected(distance)
                        },
                        selectedDistance = selectedDistance
                    )
                }
            )
        )
    }
}

@Composable
fun DistanceSelector(
    onDistanceSelected: (Distance) -> Unit,
    selectedDistance: Distance
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.select_distance),
            style = MaterialTheme.typography.titleSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        distances.forEach { distance ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (distance == selectedDistance),
                        onClick = {
                            onDistanceSelected(distance)
                        }
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (distance == selectedDistance),
                    onClick = {
                        onDistanceSelected(distance)
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary,
                        unselectedColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = distance.stringName,
                    style = MaterialTheme.typography.bodyMedium
                )
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

@SuppressLint("MissingPermission")
@Composable
private fun MapViewContainer(
    isEnabled: Boolean,
    mapView: MapView,
    currentZoom: Float,
    viewModel: AlarmDetailsViewModel
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
                viewModel.updateZoom(map.cameraPosition.zoom)
                val cameraPosition = map.cameraPosition
                viewModel.updateLocation(
                    cameraPosition.target.latitude,
                    cameraPosition.target.longitude
                )
            }
        }
    }
}
