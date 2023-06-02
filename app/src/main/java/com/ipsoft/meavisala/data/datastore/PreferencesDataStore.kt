package com.ipsoft.meavisala.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

class PreferencesDataStore(private val context: Context) {

    private val hasPermissions = booleanPreferencesKey("has_permissions")
    private val showAds = booleanPreferencesKey("show_ads")
    private val isAlarmsEnabled = booleanPreferencesKey("is_alarms_enabled")

    suspend fun storeHasPermissions(hasPermission: Boolean): Boolean {
        context.preferencesDataStore.edit { preferences ->
            preferences[hasPermissions] = hasPermission
        }
        return hasPermission
    }

    suspend fun readShowAds(): Boolean {
        return context.preferencesDataStore.data.first()[showAds] ?: true
    }

    suspend fun storeShowAds(hasPermission: Boolean): Boolean {
        context.preferencesDataStore.edit { preferences ->
            preferences[showAds] = hasPermission
        }
        return hasPermission
    }

    suspend fun readHasPermissions(): Boolean {
        return context.preferencesDataStore.data.first()[hasPermissions] ?: false
    }

    suspend fun storeIsAlarmsEnabled(isEnabled: Boolean): Boolean {
        context.preferencesDataStore.edit { preferences ->
            preferences[isAlarmsEnabled] = isEnabled
        }
        return isEnabled
    }

    suspend fun readIsAlarmsEnabled(): Boolean {
        return context.preferencesDataStore.data.first()[isAlarmsEnabled]
            ?: false
    }

    private val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore("preferences")
}
