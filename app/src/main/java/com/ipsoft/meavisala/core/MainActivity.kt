package com.ipsoft.meavisala.core

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.google.android.gms.ads.MobileAds
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList
import com.ipsoft.meavisala.core.ui.Screen
import com.ipsoft.meavisala.core.ui.theme.MeAvisaLaTheme
import com.ipsoft.meavisala.core.utils.PermissionInfo
import com.ipsoft.meavisala.features.ads.loadInterstitial
import com.ipsoft.meavisala.features.ads.removeInterstitial
import com.ipsoft.meavisala.features.alarmedetails.AlarmDetailsScreen
import com.ipsoft.meavisala.features.backgroundlocation.LocationService
import com.ipsoft.meavisala.features.home.HomeScreen
import com.ipsoft.meavisala.features.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var handler: Handler

    private lateinit var billingClient: BillingClient

    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var signatureProduct: ProductDetails

    private val requiredPermissions = mutableListOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.INTERNET,
        Manifest.permission.SET_ALARM

    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }.toTypedArray()

    override fun onResume() {
        super.onResume()
        checkPermissions()

        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP)
                .build()
        ) { billingResult: BillingResult, list: List<Purchase> ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (purchase in list) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
                        handlePurchase(purchase)
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
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
                                },
                                onRemoveAdsClick = {
                                    launchPurchaseFlow(signatureProduct)
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
        loadInterstitial(this)

        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener { billingResult: BillingResult, list: List<Purchase?>? ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && list != null) {
                    for (purchase in list) {
                        if (purchase != null) {
                            handlePurchase(purchase)
                        }
                    }
                }
            }.build()

        // start the connection after initializing the billing client
        establishConnection()

        // restore purchases
        restorePurchases()

        handler = Handler(this.mainLooper)
    }

    fun establishConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    showProducts()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                establishConnection()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    fun showProducts() {
        val productList = ImmutableList.of( // Product 1
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("lifetime_signature")
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()
        billingClient.queryProductDetailsAsync(
            params
        ) { billingResult: BillingResult?, prodDetailsList: List<ProductDetails?>? ->
            // Process the result
            if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK && prodDetailsList != null) {
                prodDetailsList[0]?.let { signatureProduct = it }
            }
        }
    }

    private fun launchPurchaseFlow(productDetails: ProductDetails) {
        val productDetailsParamsList = ImmutableList.of(
            ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        billingClient.launchBillingFlow(this, billingFlowParams)
    }

    private fun handlePurchase(purchases: Purchase) {
        if (!purchases.isAcknowledged) {
            billingClient.acknowledgePurchase(
                AcknowledgePurchaseParams
                    .newBuilder()
                    .setPurchaseToken(purchases.purchaseToken)
                    .build()
            ) { billingResult: BillingResult ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    homeViewModel.saveShowAds(false)
                }
            }
        }
    }

    private fun restorePurchases() {
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases()
            .setListener { _: BillingResult?, _: List<Purchase?>? -> }
            .build()
        val finalBillingClient = billingClient
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                establishConnection()
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    finalBillingClient.queryPurchasesAsync(
                        QueryPurchasesParams.newBuilder()
                            .setProductType(BillingClient.ProductType.INAPP).build()
                    ) { billingResult1: BillingResult, list: List<Purchase?> ->
                        if (billingResult1.responseCode == BillingClient.BillingResponseCode.OK) {
                            if (list.isNotEmpty()) {
                                homeViewModel.saveShowAds(false)
                            } else {
                                homeViewModel.saveShowAds(true)
                            }
                        }
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        removeInterstitial()
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            requiredPermissions,
            0
        )
    }
}
