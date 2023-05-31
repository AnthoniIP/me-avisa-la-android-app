package com.ipsoft.meavisala.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.android.gms.location.LocationServices
import com.ipsoft.meavisala.data.alarmdatabase.AlarmDatabase
import com.ipsoft.meavisala.data.alarmdatabase.AlarmDatabase.Companion.DATABASE_NAME
import com.ipsoft.meavisala.data.alarmdatabase.repository.AlarmRepository
import com.ipsoft.meavisala.data.alarmdatabase.repository.AlarmRepositoryImpl
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

    @Provides
    @Singleton
    fun providesAlarmDatabase(application: Application): AlarmDatabase = Room
        .databaseBuilder(application, AlarmDatabase::class.java, DATABASE_NAME)
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun providesAlarmRepository(database: AlarmDatabase): AlarmRepository =
        AlarmRepositoryImpl(database)
}
