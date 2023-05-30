package com.ipsoft.meavisala.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ipsoft.meavisala.R
import com.ipsoft.meavisala.features.ads.BannerAdView

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onAllowPermissionClick: () -> Unit,
    onAddNewAlarmClick: () -> Unit
) {
    val hasPermissions = viewModel.hasPermissions.value

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = if (hasPermissions) onAddNewAlarmClick else onAllowPermissionClick) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.add_new_alarm)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            item { BannerAdView() }
            item { Spacer(modifier = Modifier.padding(8.dp)) }
            if (!hasPermissions) {
                item {
                    MissingPermissions(onAllowPermissionClick = onAllowPermissionClick)
                }
            } else {
                item { EmptyAlarmList() }
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
