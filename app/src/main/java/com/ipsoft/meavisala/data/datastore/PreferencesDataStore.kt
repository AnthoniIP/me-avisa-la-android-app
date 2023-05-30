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

    suspend fun storeHasPermissions(hasPermission: Boolean): Boolean {
        context.preferencesDataStore.edit { preferences ->
            preferences[hasPermissions] = hasPermission
        }
        return hasPermission
    }

    suspend fun readHasPermissions(): Boolean {
        return context.preferencesDataStore.data.first()[hasPermissions] ?: false
    }

    private val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore("preferences")
}
