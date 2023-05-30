package com.ipsoft.meavisala.core

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.ads.MobileAds
import com.ipsoft.meavisala.core.ui.Screen
import com.ipsoft.meavisala.core.ui.Screen.Companion.ALARM_DETAILS_ACTION
import com.ipsoft.meavisala.core.ui.theme.MeAvisaLaTheme
import com.ipsoft.meavisala.core.utils.PermissionInfo
import com.ipsoft.meavisala.data.datastore.PreferencesDataStore
import com.ipsoft.meavisala.features.alarmedetails.AlarmDetailsScreen
import com.ipsoft.meavisala.features.backgroundlocation.LocationService
import com.ipsoft.meavisala.features.home.HomeScreen
import com.ipsoft.meavisala.features.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val homeViewModel: HomeViewModel by viewModels()

    private val requiredPermissions = mutableListOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.INTERNET

    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }.toTypedArray()

    override fun onResume() {
        super.onResume()
        checkPermissions()
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        checkPermissions()
    }

    private fun checkPermissions() {
        var hasPermissions = true
        for (permission in requiredPermissions) {
            val permissionStatus = ContextCompat.checkSelfPermission(this, permission)
            if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                hasPermissions = false
            }
            PermissionInfo.hasPermissions = hasPermissions
        }
        homeViewModel.saveHasPermissions(hasPermissions)
        if (hasPermissions) {
            Intent(this, LocationService::class.java).apply {
                action = LocationService.ACTION_START
                startService(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions()
        setContent {
            MeAvisaLaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = Screen.Home.route) {
                        composable(
                            Screen.Home.route
                        ) {
                            HomeScreen(
                                onAllowPermissionClick = {
                                    requestPermissions()
                                }
                            ) {
                                navController.navigate(Screen.AlarmDetails.route)
                            }
                        }
                        composable(Screen.AlarmDetails.route) {
                            AlarmDetailsScreen {
                                navController.popBackStack()
                            }
                        }
                    }
                }
            }
        }
        MobileAds.initialize(this) {}
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            requiredPermissions,
            0
        )
    }
}
