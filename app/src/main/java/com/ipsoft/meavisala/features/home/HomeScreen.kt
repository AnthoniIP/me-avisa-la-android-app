package com.ipsoft.meavisala.features.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.ipsoft.meavisala.R
import com.ipsoft.meavisala.core.model.AlarmEntity
import com.ipsoft.meavisala.core.utils.defaultIpsoftSize
import com.ipsoft.meavisala.core.utils.extensions.getVerCode
import com.ipsoft.meavisala.core.utils.mediumPadding
import com.ipsoft.meavisala.core.utils.smallPadding
import com.ipsoft.meavisala.features.ads.BannerAdView
import com.ipsoft.meavisala.features.map.MapPinOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onAllowPermissionClick: () -> Unit,
    onRemoveAdsClick: () -> Unit,
    onAddNewAlarmClick: () -> Unit
) {
    val hasPermissions = viewModel.hasPermissions.value
    val alarmState = viewModel.alarms.value
    val showAds = viewModel.showAds.value

    val showAboutDialog = remember { mutableStateOf(false) }
    val loadAlarm = remember { mutableStateOf(true) }

    LaunchedEffect(loadAlarm.value) {
        if (loadAlarm.value) {
            viewModel.getAlarms()
            loadAlarm.value = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(id = R.string.app_name),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = stringResource(id = R.string.back),
                            modifier = Modifier.clickable { showAboutDialog.value = true }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = if (hasPermissions) onAddNewAlarmClick else onAllowPermissionClick) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.add_new_alarm)
                )
            }
        }
    ) { paddingValues ->
        if (showAboutDialog.value) {
            AboutDialog(
                onDismiss = { showAboutDialog.value = false },
                onRemoveAdsClick = onRemoveAdsClick,
                showAds = showAds
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            if (showAds) {
                BannerAdView()
            }
            LazyColumn(
                Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!hasPermissions) {
                    item {
                        MissingPermissions(onAllowPermissionClick = onAllowPermissionClick)
                    }
                } else {
                    if (alarmState.alarms.isNotEmpty()) {
                        alarmState.alarms.sortedBy { it.id }.reversed().forEach { alarm ->
                            item {
                                AlarmItem(alarm = alarm, viewModel)
                            }
                        }
                    } else {
                        item { EmptyAlarmList() }
                    }
                }
            }
        }
    }
}

@Composable
fun AboutDialog(onDismiss: () -> Unit, onRemoveAdsClick: () -> Unit, showAds: Boolean) {
    Dialog(
        onDismissRequest = { onDismiss() }
    ) {
        Surface(
            modifier = Modifier.wrapContentSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                InfoFooter()
                if (showAds) {
                    Button(
                        onClick = { onRemoveAdsClick() },
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        elevation = null
                    ) {
                        Text(text = stringResource(id = R.string.remove_ads))
                    }
                }
            }
        }
    }
}

@Composable
fun AlarmItem(alarm: AlarmEntity, viewModel: HomeViewModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.height(300.dp)) {
                val mapUiSettings by remember {
                    mutableStateOf(
                        MapUiSettings(
                            mapToolbarEnabled = false,
                            scrollGesturesEnabledDuringRotateOrZoom = false,
                            scrollGesturesEnabled = false
                        )
                    )
                }
                val location = LatLng(alarm.latitude, alarm.longitude)
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(location, 15f)
                }
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = mapUiSettings
                )
                MapPinOverlay()
            }
            Text(
                text = alarm.notificationText,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(id = R.string.notify_in) + " " + alarm.minDistanceToNotifyText,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.enabled),
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(checked = alarm.isEnable, onCheckedChange = {
                    viewModel.updateAlarm(alarm.copy(isEnable = it))
                })
            }
        }
    }
}

@Composable
fun EmptyAlarmList() {
    Text(
        text = stringResource(id = R.string.empty_alarm_list),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun MissingPermissions(onAllowPermissionClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.missing_permissions),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = stringResource(id = R.string.missing_permissions_info),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Button(onClick = onAllowPermissionClick) {
                Text(text = stringResource(id = R.string.allow_permission))
            }
        }
    }
}

@Composable
fun InfoFooter() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(mediumPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(mediumPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.padding(smallPadding))
                Text(
                    text = stringResource(id = R.string.version).format(LocalContext.current.getVerCode()),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(mediumPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.developed_by),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.padding(smallPadding))
                ClickableLinkImage(
                    stringResource(id = R.string.linktree_url)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(mediumPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.all_rights_reserved),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun ClickableLinkImage(url: String) {
    val context = LocalContext.current

    Image(
        modifier = Modifier
            .width(defaultIpsoftSize)
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            },
        painter = painterResource(id = R.drawable.ipsoft_logo),
        contentDescription = stringResource(
            id = R.string.developer_name
        )
    )
}
