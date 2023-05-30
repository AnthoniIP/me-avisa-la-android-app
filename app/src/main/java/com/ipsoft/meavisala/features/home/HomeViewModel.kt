package com.ipsoft.meavisala.features.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipsoft.meavisala.core.utils.PermissionInfo
import com.ipsoft.meavisala.data.datastore.PreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferencesDataStore: PreferencesDataStore,
) : ViewModel(), PermissionInfo.OnPermissionListener {

    private val _hasPermissions = mutableStateOf(false)
    val hasPermissions: State<Boolean> = _hasPermissions

    init {
        PermissionInfo.addListener(this)
        loadHasPermissions()
    }

    private fun loadHasPermissions() {
        viewModelScope.launch {
            _hasPermissions.value = preferencesDataStore.readHasPermissions()
        }
    }

    fun saveHasPermissions(hasPermissions: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.storeHasPermissions(hasPermissions)
        }
    }

    override fun onCleared() {
        super.onCleared()
        PermissionInfo.removeListener(this)
    }

    override fun onPermissionUpdated(hasPermissions: Boolean) {
        _hasPermissions.value = hasPermissions
    }
}
