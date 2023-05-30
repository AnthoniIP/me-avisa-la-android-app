package com.ipsoft.meavisala.di

import android.content.Context
import com.google.android.gms.location.LocationServices
import com.ipsoft.meavisala.data.datastore.PreferencesDataStore
import com.ipsoft.meavisala.features.backgroundlocation.DefaultLocationClient
import com.ipsoft.meavisala.features.backgroundlocation.LocationClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLocationClient(@ApplicationContext applicationContext: Context): LocationClient =
        DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )

    @Provides
    @Singleton
    fun providesPreferencesDataStore(@ApplicationContext context: Context): PreferencesDataStore =
        PreferencesDataStore(context)
}
