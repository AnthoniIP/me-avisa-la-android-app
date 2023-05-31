package com.ipsoft.meavisala.features.alarmedetails

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ipsoft.meavisala.R
import com.ipsoft.meavisala.core.utils.Distance
import com.ipsoft.meavisala.core.utils.distances
import com.ipsoft.meavisala.features.ads.BannerAdView
import com.ipsoft.meavisala.features.ads.showInterstitial
import com.ipsoft.meavisala.features.map.MapPinOverlay
import com.ipsoft.meavisala.features.map.MapViewContainer
import com.ipsoft.meavisala.features.map.rememberMapViewWithLifecycle
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
    val showAds = viewModel.showAds.value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.add_new_alarm),
                        style = MaterialTheme.typography.titleLarge
                    )
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
        val creationComplete = viewModel.creationSuccess.value
        if (creationComplete) {
            if (showAds) {
                showInterstitial(LocalContext.current) {
                    onBackClick()
                }
            } else {
                onBackClick()
            }
        }
        val mapView = rememberMapViewWithLifecycle()
        VerticalScrollLayout(
            modifier = Modifier.padding(paddingValues),
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
            ),
            ChildLayout(
                content = {
                    OutlinedTextField(
                        value = viewModel.notificationText.value,
                        onValueChange = { text: String ->
                            viewModel.updateNotificationText(text)
                        },
                        label = { Text(text = stringResource(id = R.string.notification_text)) },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text
                        ),
                        singleLine = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp, 8.dp)
                    )
                }
            ),
            ChildLayout(
                content = {
                    Button(onClick = {
                        viewModel.saveAlarm()
                    }) {
                        Text(text = stringResource(id = R.string.save))
                    }
                }
            ),
            ChildLayout(
                content = {
                    BannerAdView()
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
